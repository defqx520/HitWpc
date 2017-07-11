package cn.edu.hit.ftcl.wearablepc.Communication;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import cn.edu.hit.ftcl.wearablepc.R;

public class VoiceActivity extends AppCompatActivity {
    private static final String TAG = "VoiceActivity";

    private List<Msg> mDatas = new ArrayList<>();

    private AudioRecorderButton mRecorderButton;
    private MyRecyclerView mRecyclerView;
    private Button mTextButton;
    private Button mPictureButton;
    private Button mSendButton;
    private Button mVoiceButton;
    private Button mVoiceButton2;
    private Button mImageButton;
    private Button mVideoButton;
    private LinearLayout mTextLayout;
    private LinearLayout mVoiceLayout;
    private LinearLayout mPictureLayout;
    private EditText mEditText;

    private MsgAdapter mAdapter;
    private NetworkUtil networkUtil;

    private IntentFilter intentFilter;
    private LocalReceiver localReceiver;
    private LocalBroadcastManager localBroadcastManager;

    private Uri imageUri;
    private File outputImage;

    private Uri videoUri;
    private File outputVideo;

    private long networkSpeed;

    private Handler mHnadler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 100:
                    networkSpeed = Long.valueOf(msg.obj.toString()) > networkSpeed ? Long.valueOf(msg.obj.toString()) : networkSpeed;
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.communication_activity_message);
        //设置ToolBar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //创建数据库
        Connector.getDatabase();

        //聊天消息数据初始化
        initMsg();

        //RecyclerView
        mRecyclerView = (MyRecyclerView) findViewById(R.id.msg_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new MsgAdapter(mDatas);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.scrollToPosition(mDatas.size() - 1);
        //Button
        mRecorderButton = (AudioRecorderButton) findViewById(R.id.id_recorder_button);
        mTextButton = (Button)findViewById(R.id.id_button_text);
        mPictureButton = (Button)findViewById(R.id.id_button_picture);
        mVoiceButton = (Button)findViewById(R.id.id_button_voice);
        mVoiceButton2 = (Button)findViewById(R.id.id_button_voice_2);
        mSendButton = (Button)findViewById(R.id.id_button_send);
        mImageButton = (Button)findViewById(R.id.id_button_image);
        mVideoButton = (Button)findViewById(R.id.id_button_video);
        //LinearLayout
        mTextLayout = (LinearLayout)findViewById(R.id.id_layout_text);
        mVoiceLayout = (LinearLayout)findViewById(R.id.id_layout_voice);
        mPictureLayout = (LinearLayout)findViewById(R.id.id_layout_picture);
        //TextView
        mEditText = (EditText)findViewById(R.id.id_edittext);

        //按钮点击事件
        mTextLayout.setVisibility(View.GONE);
        mVoiceLayout.setVisibility(View.VISIBLE);
        mPictureLayout.setVisibility(View.GONE);
        mTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextLayout.setVisibility(View.VISIBLE);
                mVoiceLayout.setVisibility(View.GONE);
                mPictureLayout.setVisibility(View.GONE);

            }
        });
        mVoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextLayout.setVisibility(View.GONE);
                mVoiceLayout.setVisibility(View.VISIBLE);
                mPictureLayout.setVisibility(View.GONE);
            }
        });
        mVoiceButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextLayout.setVisibility(View.GONE);
                mVoiceLayout.setVisibility(View.VISIBLE);
                mPictureLayout.setVisibility(View.GONE);
            }
        });
        mPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextLayout.setVisibility(View.GONE);
                mVoiceLayout.setVisibility(View.GONE);
                mPictureLayout.setVisibility(View.VISIBLE);
            }
        });
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开摄像头拍照
                File dirImage = new File(Environment.getExternalStorageDirectory() + "/HitWearable/image");
                if (!dirImage.exists()) {
                    dirImage.mkdirs();//文件夹不存在，则创建文件夹
                }
                outputImage = new File(dirImage, UUID.randomUUID().toString() + ".jpg");
                try {
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(Build.VERSION.SDK_INT >= 24){
                    imageUri = FileProvider.getUriForFile(VoiceActivity.this, "com.hitwearable.fileprovider", outputImage);
                }else {
                    imageUri = Uri.fromFile(outputImage);
                }
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, 1);
            }
        });
        mVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(networkSpeed < 150){
                    Toast.makeText(getApplicationContext(), "网速不佳：" + networkSpeed + "kB/s，将发送图片",Toast.LENGTH_SHORT).show();
                    //打开摄像头拍照
                    File dirImage = new File(Environment.getExternalStorageDirectory() + "/HitWearable/image");
                    if (!dirImage.exists()) {
                        dirImage.mkdirs();//文件夹不存在，则创建文件夹
                    }
                    outputImage = new File(dirImage, UUID.randomUUID().toString() + ".jpg");
                    try {
                        outputImage.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(Build.VERSION.SDK_INT >= 24){
                        imageUri = FileProvider.getUriForFile(VoiceActivity.this, "com.hitwearable.fileprovider", outputImage);
                    }else {
                        imageUri = Uri.fromFile(outputImage);
                    }
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, 1);
                }else{
                    Toast.makeText(getApplicationContext(), "网速较佳：" + networkSpeed + "kB/s",Toast.LENGTH_SHORT).show();
                    //打开摄像头摄像
                    File dirVoice = new File(Environment.getExternalStorageDirectory() + "/HitWearable/video");
                    if (!dirVoice.exists()) {
                        dirVoice.mkdirs();//文件夹不存在，则创建文件夹
                    }
                    outputVideo = new File(dirVoice, UUID.randomUUID().toString() + ".mp4");
                    try {
                        outputVideo.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(Build.VERSION.SDK_INT >= 24){
                        videoUri = FileProvider.getUriForFile(VoiceActivity.this, "com.hitwearable.fileprovider", outputVideo);
                    }else {
                        videoUri = Uri.fromFile(outputVideo);
                    }
                    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);//create a intent to record video
                    // set the video file name
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
                    // set the video quality high
                    intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                    // start the video capture Intent
                    startActivityForResult(intent, 2);
                }
            }
        });
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //发送文字
                String textContent = mEditText.getText().toString().trim();
                if(textContent.length() != 0) {
                    //数据库新增
                    Msg msg = new Msg(textContent, Msg.TYPE_SENT, System.currentTimeMillis(), Msg.CATAGORY_TEXT);
                    msg.save();
                    //发送文字到接收端
                    List<Parameter> targetIPList = DataSupport.where("name = ?", "target_ip").find(Parameter.class);
                    String targetIP = targetIPList.get(0).getValue();
                    List<Parameter> targetPortList = DataSupport.where("name = ?", "target_text_port").find(Parameter.class);
                    String targetTextPort = targetPortList.get(0).getValue();//27777
                    networkUtil.sendTextByDatagram(textContent, targetIP, Integer.parseInt(targetTextPort));

                    mDatas.add(msg);
                    //view更新
                    mAdapter.notifyItemInserted(mDatas.size() - 1);
                    //设置位置
                    mRecyclerView.scrollToPosition(mDatas.size() - 1);
                }
                mEditText.setText("");
            }
        });

        //NetworkUtil对象初始化
        networkUtil = new NetworkUtil();

        //录音完成后回调
        mRecorderButton.setFinishRecorderCallBack(new AudioRecorderButton.AudioFinishRecorderCallBack() {
            public void onFinish(long seconds, String filePath) {
                //数据库新增
                Msg msg = new Msg(filePath, Msg.TYPE_SENT, seconds);
                msg.save();
                //发送语音到接收端
                List<Parameter> localPortList = DataSupport.where("name = ?", "local_file_port").find(Parameter.class);
                String localPort = localPortList.get(0).getValue();//28888
                networkUtil.sendFileBySocket(Integer.parseInt(localPort), filePath);

                mDatas.add(msg);
                //view更新数据
                mAdapter.notifyItemInserted(mDatas.size() - 1);
                //设置位置
                mRecyclerView.scrollToPosition(mDatas.size() - 1);
            }
        });

        //注册广播接收器
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.hitwearable.LOCAL_BROADCAST");
        localReceiver = new LocalReceiver();
        localBroadcastManager.registerReceiver(localReceiver, intentFilter);

        //申请权限===很重要
        if(ContextCompat.checkSelfPermission(VoiceActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(VoiceActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(VoiceActivity.this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(VoiceActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.INTERNET}, 1);
        }

        //检测网速
        new NetworkSpeedUtil(this,mHnadler).startShowNetSpeed();
    }

    /**
     * 本地广播接收器
     */
    class LocalReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            Msg msg = (Msg)bundle.getSerializable("msg");
            mDatas.add(msg);
            //view更新数据
            mAdapter.notifyItemInserted(mDatas.size() - 1);
            //设置位置
            mRecyclerView.scrollToPosition(mDatas.size() - 1);
        }
    }

    /**
     * 初始化消息列表
     */
    private void initMsg(){
        mDatas = DataSupport.findAll(Msg.class);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.d("VoiceActivity", "onActivityResult() has executed");
        LogUtil.d("VoiceActivity", "requestCode is " + requestCode);
        LogUtil.d("VoiceActivity", "resultCode is " + resultCode);
        switch (requestCode){
            case 1:
                if (resultCode == RESULT_OK) {
                    //数据库新增
                    Msg msg = new Msg(outputImage.toString(), Msg.TYPE_SENT, System.currentTimeMillis(), Msg.CATAGORY_IMAGE);
                    msg.save();
                    //发送图片到接收端
                    List<Parameter> localPortList = DataSupport.where("name = ?", "local_file_port").find(Parameter.class);
                    String localPort = localPortList.get(0).getValue();//28888
                    networkUtil.sendFileBySocket(Integer.parseInt(localPort), outputImage.toString());

                    mDatas.add(msg);
                    //view更新数据
                    mAdapter.notifyItemInserted(mDatas.size() - 1);
                    //设置位置
                    mRecyclerView.scrollToPosition(mDatas.size() - 1);
                }
                break;
            case 2:
                if(resultCode == RESULT_OK){
                    LogUtil.d("VoiceActivity", "RESULT_OK");
                    //数据库新增
                    Msg msg = new Msg(outputVideo.toString(), Msg.TYPE_SENT, System.currentTimeMillis(), Msg.CATAGORY_VIDEO);
                    msg.save();
                    //发送视频到接收端
                    List<Parameter> localPortList = DataSupport.where("name = ?", "local_file_port").find(Parameter.class);
                    String localPort = localPortList.get(0).getValue();//28888
                    networkUtil.sendFileBySocket(Integer.parseInt(localPort), outputVideo.toString());

                    mDatas.add(msg);
                    //view更新数据
                    mAdapter.notifyItemInserted(mDatas.size() - 1);
                    //设置位置
                    mRecyclerView.scrollToPosition(mDatas.size() - 1);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            //进入设置界面
            case R.id.settings:
                Intent intent = new Intent(VoiceActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
            //删除msg表所有数据
//            case R.id.delete:
//                AlertDialog.Builder dialog = new AlertDialog.Builder(VoiceActivity.this);
//                dialog.setTitle("警告");
//                dialog.setMessage("消息删除后将不可恢复，您确定要删除所有消息吗？");
//                dialog.setCancelable(false);
//                dialog.setPositiveButton("确定删除", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        DataSupport.deleteAll(Msg.class);
//                    }
//                });
//                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                });
//                dialog.show();
//                break;
            default:
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MediaPlayerManager.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MediaPlayerManager.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaPlayerManager.release();
        localBroadcastManager.unregisterReceiver(localReceiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED){

                }else {
                    if(grantResults[0] == PackageManager.PERMISSION_DENIED){
                        Toast.makeText(this, "拒绝权限将无法收听语音", Toast.LENGTH_SHORT).show();
                    }
                    if(grantResults[1] == PackageManager.PERMISSION_DENIED){
                        Toast.makeText(this, "拒绝录音权限将无法发送语音", Toast.LENGTH_SHORT).show();
                    }
                    if(grantResults[2] == PackageManager.PERMISSION_DENIED){
                        Toast.makeText(this, "拒绝网络权限将无法接受和发送语音", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
        }
    }

}
