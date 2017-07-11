package cn.edu.hit.ftcl.wearablepc.WifiCamera;

import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.amap.api.services.route.RouteSearch;

import org.apache.commons.net.ftp.FTPFile;

import cn.edu.hit.ftcl.wearablepc.MyApplication;
import cn.edu.hit.ftcl.wearablepc.R;
import cn.edu.hit.ftcl.wearablepc.WifiCamera.FTPUtil.*;

public class FtpFileListActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int UPDATE_FILE_NAME_LIST = 1;
    private Button getVideoNameList;
    private ListView videoNameListView;
    private WifiFileNameAdapter wifiFileNameAdapter;
    private FTPFile[] wifiFileList = {};
    private FTP ftp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ftp_file_list);


        getVideoNameList = (Button)findViewById(R.id.button_get_video_name_list);
        videoNameListView = (ListView)findViewById(R.id.video_name_list_view);
        wifiFileNameAdapter = new WifiFileNameAdapter(this);
        videoNameListView.setAdapter(wifiFileNameAdapter);
        ftp = new FTP("192.168.0.1",-1,"admin","admin");

        getVideoNameList.setOnClickListener(this);

        videoNameListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String fileName = wifiFileNameAdapter.getItem(position).toString();
                Intent intent = new Intent(FtpFileListActivity.this, DownloadPlayActivity.class);
                intent.putExtra("ClickedName", fileName);
                startActivity(intent);
            }
        });

    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case UPDATE_FILE_NAME_LIST:
                    for (FTPFile file:wifiFileList){
                        if(file.getName().contains(".avi")){
                            wifiFileNameAdapter.addFile(file.getName(),file.getTimestamp());
                            wifiFileNameAdapter.notifyDataSetChanged();
                        }
                    }
                    break;
                default:break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_get_video_name_list:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getVideoNameListFromServer();
                    }
                }).start();
                break;
        }
    }

    private void getVideoNameListFromServer(){
//        Toast.makeText(MyApplication.getContext(),"获取文件列表",Toast.LENGTH_SHORT).show();
        boolean success = ftp.ftpLogin();//登录
        if (success){
//            Toast.makeText(MyApplication.getContext(),"登陆成功",Toast.LENGTH_SHORT).show();
        }else {
//            Toast.makeText(MyApplication.getContext(),"登录失败",Toast.LENGTH_SHORT).show();
            return;
        }

        wifiFileList = ftp.getFileNameList("/");      //获取文件列表
        Message message = new Message();
        message.what = UPDATE_FILE_NAME_LIST;
        handler.sendMessage(message);
        ftp.ftpLogOut();

    }
}
