package cn.edu.hit.ftcl.wearablepc.Network;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.litepal.crud.DataSupport;

import cn.edu.hit.ftcl.wearablepc.MyGridView.IndexActivity;
import cn.edu.hit.ftcl.wearablepc.R;
import cn.edu.hit.ftcl.wearablepc.Security.EncryptionUtil;


/**
 * 记录小组其他成员的IP信息
 * Created by hzf on 2017/11/8.
 */

public class UserIPAddActivity extends AppCompatActivity {
    private EditText mUsername;
    private EditText mIP;
    private EditText mPort;
    private Button mButtonAdd;
    private Button mButtonClear;
    private LinearLayout mWrongInput;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.network_activity_userip_add);
        //设置ToolBar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_userip_add);
        setSupportActionBar(toolbar);

        mUsername = (EditText)findViewById(R.id.id_username);
        mIP = (EditText)findViewById(R.id.id_ip);
        mPort = (EditText) findViewById(R.id.id_port);
        mButtonAdd = (Button)findViewById(R.id.id_btn_add);
        mButtonClear = (Button)findViewById(R.id.id_btn_clear);
        mWrongInput = (LinearLayout)findViewById(R.id.id_wrong_input);

        mButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mUsername.getText().toString().trim();
                String ip = mIP.getText().toString().trim();
                int port = Integer.parseInt(mPort.getText().toString().trim());

                if(DataSupport.where("username = ? or ip = ?", username, ip).find(UserIPInfo.class).size() != 0){//验证用户名和IP是否已存在
                    mWrongInput.setVisibility(View.VISIBLE);
                }else {
                    //数据库新增
                    UserIPInfo userIPInfo = new UserIPInfo(username, ip, port);
                    userIPInfo.save();

                    Intent intent = new Intent();
                    intent.putExtra("id", userIPInfo.getId());
                    intent.putExtra("username", userIPInfo.getUsername());
                    intent.putExtra("ip", userIPInfo.getIp());
                    intent.putExtra("port", userIPInfo.getPort());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

        mButtonClear.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mUsername.setText("");
                mIP.setText("");
                mPort.setText("");
            }
        });
    }
}
