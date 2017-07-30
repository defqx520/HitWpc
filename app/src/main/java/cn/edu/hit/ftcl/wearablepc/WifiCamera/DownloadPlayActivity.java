package cn.edu.hit.ftcl.wearablepc.WifiCamera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;


import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.edu.hit.ftcl.wearablepc.Network.NetworkUtil;
import cn.edu.hit.ftcl.wearablepc.R;
import cn.edu.hit.ftcl.wearablepc.Communication.Parameter;
import cn.edu.hit.ftcl.wearablepc.WifiCamera.FTPUtil.FTP;
import cn.edu.hit.ftcl.wearablepc.WifiCamera.FTPUtil.GetFilesUtils;

import static android.os.Environment.getExternalStorageDirectory;

public class DownloadPlayActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "WifiDisplayFragment: ";
    private static final int DOWNLOAD_SUCCESS = 2;
    private static final int DOWNLOAD_FAILURE = 3;
    private String intentFileName;
    private TextView videoFileName;
    private Button buttonDownloadVideo;
    private Button buttonDisplayVideo;
    private Button buttonSendVideo;
    private VideoView videoView;

    private String downloadFileNameString;
    private String displayFileNameString;

    private GetFilesUtils GFU;
    private List<Map<String,Object>> fileListInDownloadMonitorVideo;
    private FTP ftp;

    private boolean isDownload;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_play);

        buttonDisplayVideo = (Button)findViewById(R.id.button_display_video);
        buttonDownloadVideo = (Button)findViewById(R.id.button_download_video);
        buttonSendVideo = (Button) findViewById(R.id.button_send_video);
        videoFileName = (TextView)findViewById(R.id.video_file_name);
        videoView = (VideoView)findViewById(R.id.display_video_view);

        buttonDownloadVideo.setOnClickListener(this);
        buttonDisplayVideo.setOnClickListener(this);
        buttonSendVideo.setOnClickListener(this);

        GFU = GetFilesUtils.getInstance();

        ftp = new FTP("192.168.0.1",-1,"admin","admin");
        intentFileName = getIntent().getStringExtra("ClickedName");
        setFileName(intentFileName);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_download_video:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        downLoadFile();
                    }
                }).start();
                break;
            case R.id.button_display_video:
                displayVideoFile();
                break;
            case R.id.button_send_video:
                List<Parameter> localPortList = DataSupport.where("name = ?", "local_file_port").find(Parameter.class);
                String localPort = localPortList.get(0).getValue();//28888
                Toast.makeText(getApplicationContext(), "正在传输，请勿退出。", Toast.LENGTH_SHORT).show();
                NetworkUtil.sendFileBySocket(Integer.parseInt(localPort), getExternalStorageDirectory().toString()+"/HitWearable/downloadMonitorVideo/"+displayFileNameString);
                Toast.makeText(getApplicationContext(), "正在传输，请勿退出。", Toast.LENGTH_SHORT).show();
            default:break;
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case DOWNLOAD_FAILURE:
                    buttonDisplayVideo.setEnabled(false);
                    buttonDownloadVideo.setEnabled(true);
                    Toast.makeText(getApplicationContext(),"文件下载失败，点击重新下载",Toast.LENGTH_SHORT).show();
                    break;
                case DOWNLOAD_SUCCESS:
                    buttonDisplayVideo.setEnabled(true);
                    buttonDownloadVideo.setEnabled(false);
                    Toast.makeText(getApplicationContext(),"文件下载完成",Toast.LENGTH_SHORT).show();
                    videoFileName.setText(intentFileName+"已下载");
                    break;
                default:break;
            }
        }
    };

    private void displayVideoFile(){        //文件播放
        requestExternalStoragePermissions();
        videoView.setMediaController(new MediaController(DownloadPlayActivity.this));
        videoView.setVideoPath(getExternalStorageDirectory().toString()+"/HitWearable/downloadMonitorVideo/"+displayFileNameString);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.start();
            }

        });

        /*File file = new File(getExternalStorageDirectory().toString()+"/downloadMonitorVideo/",displayFileNameString);
        videoView.setVideoPath(file.getPath());
        videoView.start();*/
    }



    private void downLoadFile(){            //文件下载
        Message message = new Message();
        boolean successLogin = ftp.ftpLogin();//登录
        if (successLogin){
            Log.d(TAG, "downLoadFile: 登录成功");
        }else {
            Log.d(TAG, "downLoadFile: 登录失败");
            message.what = DOWNLOAD_FAILURE;
            handler.sendMessage(message);
            return;
        }

        boolean successDownload = ftp.downloadFile(downloadFileNameString, getExternalStorageDirectory().toString()+"/HitWearable/downloadMonitorVideo/","/");
        if (successDownload){
            message.what = DOWNLOAD_SUCCESS;
            handler.sendMessage(message);
        }else{
            message.what = DOWNLOAD_FAILURE;
            handler.sendMessage(message);
        }
        ftp.ftpLogOut();
    }

    public void setFileName(String fileName) {
        downloadFileNameString = fileName;
        displayFileNameString = fileName;

        isDownload = false;

        List<String> fileNameInStorage = new ArrayList<String>();

        requestExternalStoragePermissions();//申请权限

        File dire = new File(getExternalStorageDirectory().toString()+"/HitWearable/downloadMonitorVideo");
        if(!dire.exists()){
            dire.mkdirs();
        }
        fileListInDownloadMonitorVideo = GFU.getSonNode(getExternalStorageDirectory().toString()+"/HitWearable/downloadMonitorVideo");
        if (fileListInDownloadMonitorVideo==null || fileListInDownloadMonitorVideo.size() <= 0){
            isDownload = false;
            buttonDownloadVideo.setEnabled(true);
            buttonDisplayVideo.setEnabled(false);
        }else{
            for (Map<String,Object> unit : fileListInDownloadMonitorVideo){
                for (String unitString : unit.keySet()){
                    if (unitString == GFU.FILE_INFO_NAME){
                        Log.d(TAG, "setFileName: 已有的文件" + GFU.FILE_INFO_NAME + " " + unit.get(GFU.FILE_INFO_NAME));
                        fileNameInStorage.add(unit.get(GFU.FILE_INFO_NAME).toString());
                    }
                }
            }       //找到现有的视频文件列表
            if (fileNameInStorage.contains(fileName)){//点击的文件在列表中
                Log.d(TAG, "setFileName: 文件在列表中，不需要下载，可直接播放");
                isDownload = true;
                buttonDownloadVideo.setEnabled(false);
                buttonDisplayVideo.setEnabled(true);
            }else{                                  //点击的文件不在列表中
                Log.d(TAG, "setFileName: 文件不在列表中，需要下载后才能播放");
                isDownload = false;
                buttonDownloadVideo.setEnabled(true);
                buttonDisplayVideo.setEnabled(false);
            }
        }
        if(isDownload){
            videoFileName.setText(fileName+"已下载");
        }else{
            videoFileName.setText(fileName+"未下载");
        }
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    /**
     * Checks if the app has permission to write to device storage
     * If the app does not has permission then the user will be prompted to grant permissions
     */
    public void requestExternalStoragePermissions() {
        // Check if we have write permission
        int permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PermissionChecker.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_EXTERNAL_STORAGE);
        }else{
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case REQUEST_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    return;
                }else{
                    Toast.makeText(getApplicationContext(),"拒绝权限，无法使用",Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (videoView != null){
            videoView.suspend();
        }
    }
}
