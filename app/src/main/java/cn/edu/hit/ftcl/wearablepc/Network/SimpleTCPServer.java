package cn.edu.hit.ftcl.wearablepc.Network;

import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import cn.edu.hit.ftcl.wearablepc.Communication.LogUtil;

/**
 * Created by hzf on 2017/11/8.
 */

public class SimpleTCPServer {
    private static final String TAG = SimpleTCPServer.class.getSimpleName();
    private static final int INPUT_STREAM_READ_TIMEOUT = 300;

    /**
     * 服务器，连接服务器的客户端
     */
    private ServerSocket mServer;
    private List<Socket> mClientList = new ArrayList<>();

    private Handler mHandler;

    public SimpleTCPServer(Handler handler) {
        this.mHandler = handler;
    }

    public void listen(final int port) {
        if (mServer != null && !mServer.isClosed()) {
            close();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mServer = new ServerSocket(port);
                    while (mServer != null && !mServer.isClosed()) {
                        LogUtil.d(TAG, "start to accept");
                        Socket client = mServer.accept();
                        if (client.isConnected()) {
                            LogUtil.d(TAG, String.format("accepted from: %s[%d]", client.getInetAddress().getHostAddress(), client.getPort()));
                            mClientList.add(client);
                            new Thread(new ReceiveRunnable(mClientList.size() - 1, client)).start();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 接收客户端数据的子线程
     */
    class ReceiveRunnable implements Runnable {
        private int which;
        private Socket mSocket;

        ReceiveRunnable(int which, Socket socket) {
            this.which = which;
            this.mSocket = socket;
        }

        @Override
        public void run() {
            try {
                // 给读取流设置超时时间，否则会一直在read()那阻塞
                mSocket.setSoTimeout(INPUT_STREAM_READ_TIMEOUT);
                InputStream in = mSocket.getInputStream();
                while (mSocket != null && mSocket.isConnected()) {
                    // 读取流
                    byte[] data = new byte[0];
                    byte[] buf = new byte[1024];
                    int len;
                    try {
                        while ((len = in.read(buf)) != -1) {
                            byte[] temp = new byte[data.length + len];
                            System.arraycopy(data, 0, temp, 0, data.length);
                            System.arraycopy(buf, 0, temp, data.length, len);
                            data = temp;
                        }
                    } catch (SocketTimeoutException stExp) {
                        // 只catch，不做任何处理
                        // stExp.printStackTrace();
                    }

                    // 处理流
                    if (data.length != 0) {
                        pushMsgToHandler(which, data);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 推送信息给Handler
     */
    private void pushMsgToHandler(int which, byte[] data) {
        Message message = mHandler.obtainMessage();
        message.what = which;
        message.obj = data;
        mHandler.sendMessage(message);
    }

    /**
     * 发送数据
     */
    public boolean sendData(int which, byte[] bytes) {
        if (which < 0 || which >= mClientList.size()) {
            return false;
        }

        Socket socket = mClientList.get(which);
        if (socket != null && socket.isConnected()) {
            try {
                OutputStream out = socket.getOutputStream();
                out.write(bytes);
                out.flush();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean sendData(int which, String data) {
        return sendData(which, data.getBytes(Charset.forName("UTF-8")));
    }

    public void close() {
        if (mServer != null) {
            try {
                mServer.close();
                mServer = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (Socket client : mClientList) {
            try {
                client.close();
                client = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mClientList.clear();
    }

    public Socket getClient(int which) {
        return which < 0 || which >= mClientList.size() ? null : mClientList.get(which);
    }

    public int getClientCount() {
        return mClientList.size();
    }
}
