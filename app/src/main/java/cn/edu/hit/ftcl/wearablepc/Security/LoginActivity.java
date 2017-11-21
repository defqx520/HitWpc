package cn.edu.hit.ftcl.wearablepc.Security;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import cn.edu.hit.ftcl.wearablepc.MyGridView.IndexActivity;
import cn.edu.hit.ftcl.wearablepc.Network.NetworkUtil;
import cn.edu.hit.ftcl.wearablepc.R;


/**
 * 登录
 * Created by hzf on 2017/7/29.
 */

public class LoginActivity extends AppCompatActivity {
    private EditText mUsername;
    private EditText mPassword;
    private Button mButtonLogin;
    private Button mButtonClear;
    private LinearLayout mWrongPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.security_activity_login);

        mUsername = (EditText)findViewById(R.id.id_username);
        mPassword = (EditText)findViewById(R.id.id_password);
        mButtonLogin = (Button)findViewById(R.id.id_btn_login);
        mButtonClear = (Button)findViewById(R.id.id_btn_clear);
        mWrongPassword = (LinearLayout)findViewById(R.id.id_wrong_password);

        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mUsername.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                if(EncryptionUtil.validateIdentity(username, password)){
                    //打开新的Intent并清除栈里的其他Intent
                    Intent intent = new Intent(LoginActivity.this, IndexActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }else{
                    mWrongPassword.setVisibility(View.VISIBLE);
                }
            }
        });

        mButtonClear.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mUsername.setText("");
                mPassword.setText("");
            }
        });
    }
}
