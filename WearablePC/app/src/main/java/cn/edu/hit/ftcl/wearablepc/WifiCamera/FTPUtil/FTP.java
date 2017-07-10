package cn.edu.hit.ftcl.wearablepc.WifiCamera.FTPUtil;

/**
 * Created by HFZ on 2017/6/5.
 */

import android.util.Log;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.TimeZone;


public class FTP {
    private final static String TAG = "FTPClass: ";
    private FTPClient ftpClient;     //FTP客户端
    private String strIp;           //目的IP
    private int intPort;            //目的端口号
    private String user;            //用户名
    private String password;        //密码

    /**
     * FTP构造函数
     */
    public FTP(String strIp, int intPort, String user, String password){
        this.strIp = strIp;
        this.intPort = intPort;
        this.user = user;
        this.password = password;
        this.ftpClient = new FTPClient();
    }

    /**
     * FTP登陆
     * @return
     */
    public boolean ftpLogin(){
        boolean isLogin = false;
        FTPClientConfig conf = new FTPClientConfig();
        conf.setServerTimeZoneId(TimeZone.getDefault().getID());
        ftpClient.setControlEncoding("GBK");
        ftpClient.configure(conf);
        try {
            if(intPort > 0){
                ftpClient.connect(strIp, intPort);
            }else{
                System.out.println("正在连接FTP服务器……");
                ftpClient.connect(strIp);
            }
            int reply = ftpClient.getReplyCode();
            if(!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                System.err.println("FTP服务器拒绝连接.");
                System.exit(1);
            }
            System.out.println("正在登陆FTP服务器……");
            ftpClient.login(user, password);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            System.out.println("恭喜" + user + "成功登陆FTP服务器");
            isLogin = true;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("登录服务器失败");
        }
        ftpClient.setBufferSize(1024*2);
        ftpClient.setDataTimeout(30*1000);
        return isLogin;
    }


    /**
     * 退出关闭服务器连接
     */
    public void ftpLogOut() {
        if(null != ftpClient && ftpClient.isConnected()){
            try {
                boolean result = ftpClient.logout();
                if(result){
                    System.out.println("成功退出服务器");
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("退出FTP服务器异常" + e.getMessage());
            } finally {
                try {
                    ftpClient.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("关闭FTP服务器连接异常");
                }
            }
        }
    }

    /**
     * 上传FTP文件
     * localFile本地文件
     * remoteUploadPath上传服务器路径，应该以/结束
     */
    public boolean uploadFile(File localFile, String remoteUploadPath) {
        BufferedInputStream inStream = null;
        boolean success = false;
        try {
            ftpClient.changeWorkingDirectory(remoteUploadPath);
            inStream = new BufferedInputStream(new FileInputStream(localFile));
            success = ftpClient.storeFile(localFile.getName(), inStream);
            if(success){
                System.out.println("上传成功");
                return success;
            }
            else{
                System.out.println("上传失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("上传失败1");
        } finally{
            if(inStream != null){
                try {
                    inStream.close();
                    System.out.println("输入流关闭成功");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return success;
    }

    /**
     * 下载文件
     * remoteFileName待下载文件名称
     * localDires下载至本地的路径
     * remoteDownLoadPath远程文件的路径
     */
    public boolean downloadFile(String remoteFileName, String localDires, String remoteDownLoadPath){
        boolean success = false;
        String strFilePath = localDires + remoteFileName;
        BufferedOutputStream outStream = null;

        System.out.println("开始下载……");
        try {
            ftpClient.changeWorkingDirectory(remoteDownLoadPath);
            outStream = new BufferedOutputStream(new FileOutputStream(strFilePath));
            success = ftpClient.retrieveFile(remoteFileName, outStream);
            if(success){
                System.out.println(remoteFileName + "成功下载到" + strFilePath);
                return success;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("下载文件失败");
        } finally {
            if(outStream != null){
                try {
                    outStream.flush();
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if(success == false){
            System.out.println("下载失败");
        }
        return success;
    }


    /**
     * 上传文件夹
     * localDirectory本地文件夹
     * remoteDirectoryPath上传服务器路径，应以/结束
     */
    public boolean uploadDirectory(String localDirectory, String remoteDirectoryPath){
        File src = new File(localDirectory);
        remoteDirectoryPath = remoteDirectoryPath + src.getName() + "/";
        try {
            ftpClient.makeDirectory(remoteDirectoryPath);
            System.out.println("创建目录成功");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("创建目录失败");
        }

        File[] allFile = src.listFiles();
        for(int currentFile=0; currentFile<allFile.length; currentFile++){
            if(!allFile[currentFile].isDirectory()){
                String srcName = allFile[currentFile].getPath().toString();
                uploadFile(new File(srcName), remoteDirectoryPath);
            }
        }
        for(int currentFile=0; currentFile<allFile.length; currentFile++){
            if(allFile[currentFile].isDirectory()){
                uploadDirectory(allFile[currentFile].getPath().toString(), remoteDirectoryPath);
            }
        }
        return true;
    }


    public FTPFile[] getFileNameList(String remoteDirectoryPath){
        FTPFile[] ftpFileList = {};
        try {
            ftpClient.changeWorkingDirectory(remoteDirectoryPath);
            ftpFileList = ftpClient.listFiles();
            for (FTPFile file : ftpFileList){
                Log.d(TAG, "getFileNameList: " + file.getName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ftpFileList;
    }
}