package cn.edu.hit.ftcl.wearablepc.Network;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.litepal.crud.DataSupport;

import cn.edu.hit.ftcl.wearablepc.R;

public class UserIPEditActivity extends AppCompatActivity {

    private EditText mEditTextUsername;

    private EditText mEditTextIP;

    private EditText mEditTextPort;

    private Button mButtonEdit;

    private Button mButtonDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.network_activity_userip_edit);
        //设置ToolBar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_userip_edit);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        final int userId = intent.getIntExtra("user_id", 0);
        final int position = intent.getIntExtra("position", -1);
        final UserIPInfo userIPInfo = DataSupport.find(UserIPInfo.class, userId);

        mEditTextUsername = (EditText)findViewById(R.id.id_username);
        mEditTextUsername.setText(userIPInfo.getUsername());
        mEditTextUsername.setEnabled(false);

        mEditTextIP = (EditText)findViewById(R.id.id_ip);
        mEditTextIP.setText(userIPInfo.getIp());

        mEditTextPort = (EditText)findViewById(R.id.id_port);
        mEditTextPort.setText(String.valueOf(userIPInfo.getPort()));

        mButtonEdit = (Button)findViewById(R.id.id_btn_edit);
        mButtonDelete = (Button)findViewById(R.id.id_btn_delete);

        mButtonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = mEditTextIP.getText().toString().trim();
                int port = Integer.parseInt(mEditTextPort.getText().toString().trim());
                userIPInfo.setIp(ip);
                userIPInfo.setPort(port);
                userIPInfo.update(userId);

                //打开TCP接收端口
                if(userIPInfo.getType() == UserIPInfo.TYPE_SELF) {
                    NetworkUtil networkUtil = new NetworkUtil();
                    networkUtil.receiveByTCP();
                }

                Intent intent = new Intent();
                intent.putExtra("result", "edit");
                intent.putExtra("position", position);
                intent.putExtra("ip", ip);
                intent.putExtra("port", port);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        mButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataSupport.delete(UserIPInfo.class, userId);

                Intent intent = new Intent();
                intent.putExtra("result", "delete");
                intent.putExtra("position", position);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
