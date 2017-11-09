package cn.edu.hit.ftcl.wearablepc.Network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.charset.Charset;

import cn.edu.hit.ftcl.wearablepc.Communication.LogUtil;

/**
 * Created by hzf on 2017/11/8.
 */

public abstract class SimpleTCPClient {
    private static final String TAG = SimpleTCPClient.class.getSimpleName();
    private static final int CONNECT_TIMEOUT = 5000;
    private static final int INPUT_STREAM_READ_TIMEOUT = 300;

    private Socket mSocket;
    private InputStream mInputStream;
    private OutputStream mOutputStream;

    public SimpleTCPClient() {
    }

    /**
     * 连接主机
     * @param host 主机IP地址
     * @param port 主机端口
     */
    public void connect(final String host, final int port) {
        if (mSocket != null) {
            close();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mSocket = new Socket();
                    SocketAddress socketAddress = new InetSocketAddress(host, port);
                    // 设置连接超时时间
                    mSocket.connect(socketAddress, CONNECT_TIMEOUT);
                    if (mSocket.isConnected()) {
                        LogUtil.d(TAG, "connected to host success");
                        // 设置读流超时时间，必须在获取流之前设置
                        mSocket.setSoTimeout(INPUT_STREAM_READ_TIMEOUT);
                        mInputStream = mSocket.getInputStream();
                        mOutputStream = mSocket.getOutputStream();
                        new ReceiveThread().start();
                    } else {
                        mSocket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 接收进程
     */
    class ReceiveThread extends Thread {
        @Override
        public void run() {
            super.run();

            while (mSocket != null && mSocket.isConnected() && mInputStream != null) {
                // 读取流
                byte[] data = new byte[0];
                byte[] buf = new byte[1024];
                int len;
                try {
                    while ((len = mInputStream.read(buf)) != -1) {
                        byte[] temp = new byte[data.length + len];
                        System.arraycopy(data, 0, temp, 0, data.length);
                        System.arraycopy(buf, 0, temp, data.length, len);
                        data = temp;
                    }
                } catch (IOException e) {
//                    e.printStackTrace();
                }

                // 处理流
                if (data.length != 0) {
                    processData(data);
                }
            }

        }
    }

    /**
     * process data from received，which is not run in the main thread。
     */
    public abstract void processData(byte[] data);

    /**
     * 发送数据
     */
    public void send(byte[] data) {
        if (mSocket != null && mSocket.isConnected() && mOutputStream != null) {
            try {
                mOutputStream.write(data);
                mOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void send(String data) {
        send(data.getBytes(Charset.forName("UTF-8")));
    }

    public void close() {
        if (mSocket != null) {
            try {
                mInputStream.close();
                mOutputStream.close();
                mSocket.close();
                mInputStream = null;
                mOutputStream = null;
                mSocket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Socket getSocket() {
        return mSocket;
    }
}
