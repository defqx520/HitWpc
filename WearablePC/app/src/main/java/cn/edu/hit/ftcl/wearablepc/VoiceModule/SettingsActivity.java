package cn.edu.hit.ftcl.wearablepc.VoiceModule;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.edu.hit.ftcl.wearablepc.R;
import org.litepal.crud.DataSupport;

public class SettingsActivity extends AppCompatActivity {

    private EditText mTargetIP;
    private EditText mTargetFilePort;
    private EditText mLocalFilePort;
    private EditText mTargetTextPort;
    private EditText mLocalTextPort;

    private Button mBtnConnect;
    private Button mBtnDisconnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        mTargetIP = (EditText)findViewById(R.id.target_ip);
        mTargetFilePort = (EditText)findViewById(R.id.target_file_port);
        mLocalFilePort = (EditText)findViewById(R.id.local_file_port);
        mTargetTextPort = (EditText)findViewById(R.id.target_text_port);
        mLocalTextPort = (EditText)findViewById(R.id.local_text_port);
        mBtnConnect = (Button)findViewById(R.id.btn_connect);
        mBtnDisconnect = (Button)findViewById(R.id.btn_disconnect);
        //读取数据库，设置文本框
        String targetIP = DataSupport.where("name = ?", "target_ip").find(Parameter.class).get(0).getValue();
        mTargetIP.setText(targetIP);
        String targetFilePort = DataSupport.where("name = ?", "target_file_port").find(Parameter.class).get(0).getValue();
        mTargetFilePort.setText(targetFilePort);
        String localFilePort = DataSupport.where("name = ?", "local_file_port").find(Parameter.class).get(0).getValue();
        mLocalFilePort.setText(localFilePort);
        String targetTextPort = DataSupport.where("name = ?", "target_text_port").find(Parameter.class).get(0).getValue();
        mTargetTextPort.setText(targetTextPort);
        String localTextPort = DataSupport.where("name = ?", "local_text_port").find(Parameter.class).get(0).getValue();
        mLocalTextPort.setText(localTextPort);
        LogUtil.d("SettingsActivity onCreate", "targetFilePort:" + targetFilePort);
        LogUtil.d("SettingsActivity onCreate", "localFilePort:" + localFilePort);
        LogUtil.d("SettingsActivity onCreate", "targetIP:" + targetIP);

        mBtnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //修改数据库表parameter
                Parameter pTargetIP = new Parameter();
                pTargetIP.setValue(mTargetIP.getText().toString().trim());
                pTargetIP.updateAll("name = ?", "target_ip");
                Parameter pTargetFilePort = new Parameter();
                pTargetFilePort.setValue(mTargetFilePort.getText().toString().trim());
                pTargetFilePort.updateAll("name = ?", "target_file_port");
                Parameter pLocalFilePort = new Parameter();
                pLocalFilePort.setValue(mLocalFilePort.getText().toString().trim());
                pLocalFilePort.updateAll("name = ?", "local_file_port");
                Parameter pTargetTextPort = new Parameter();
                pTargetTextPort.setValue(mTargetTextPort.getText().toString().trim());
                pTargetTextPort.updateAll("name = ?", "target_text_port");
                Parameter pLocalTextPort = new Parameter();
                pLocalTextPort.setValue(mLocalTextPort.getText().toString().trim());
                pLocalTextPort.updateAll("name = ?", "local_text_port");
                //打开文件接收服务
                Intent startIntent = new Intent(SettingsActivity.this, VoiceReceiveService.class);
//                startService(startIntent);
                Toast.makeText(getApplicationContext(), "文件接收服务已开启，您能接收到语音消息", Toast.LENGTH_SHORT).show();
                //回到主界面
                finish();
            }
        });

        mBtnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //关闭文件接收服务
                Intent stopIntent = new Intent(SettingsActivity.this, VoiceReceiveService.class);
//                stopService(stopIntent);
                Toast.makeText(getApplicationContext(), "文件接收服务已关闭，您将接收不到任何语音消息", Toast.LENGTH_SHORT).show();
                //回到主界面
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //设置文本编辑框
        String targetIP = DataSupport.where("name = ?", "target_ip").find(Parameter.class).get(0).getValue();
        mTargetIP.setText(targetIP);
        String targetFilePort = DataSupport.where("name = ?", "target_file_port").find(Parameter.class).get(0).getValue();
        mTargetFilePort.setText(targetFilePort);
        String localFilePort = DataSupport.where("name = ?", "local_file_port").find(Parameter.class).get(0).getValue();
        mLocalFilePort.setText(localFilePort);
        LogUtil.d("SettingsActivity onStart", "targetIP:" + targetIP);
        LogUtil.d("SettingsActivity onStart", "targetFilePort:" + targetFilePort);
        LogUtil.d("SettingsActivity onStart", "localFilePort:" + localFilePort);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //设置文本编辑框
        String targetIP = DataSupport.where("name = ?", "target_ip").find(Parameter.class).get(0).getValue();
        mTargetIP.setText(targetIP);
        String targetFilePort = DataSupport.where("name = ?", "target_file_port").find(Parameter.class).get(0).getValue();
        mTargetFilePort.setText(targetFilePort);
        String localFilePort = DataSupport.where("name = ?", "local_file_port").find(Parameter.class).get(0).getValue();
        mLocalFilePort.setText(localFilePort);
        LogUtil.d("SettingsActivity onResume", "targetIP:" + targetIP);
        LogUtil.d("SettingsActivity onResume", "targetFilePort:" + targetFilePort);
        LogUtil.d("SettingsActivity onResume", "localFilePort:" + localFilePort);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
