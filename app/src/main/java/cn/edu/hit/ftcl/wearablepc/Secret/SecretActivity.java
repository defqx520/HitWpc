package cn.edu.hit.ftcl.wearablepc.Secret;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import cn.edu.hit.ftcl.wearablepc.Common.LogUtil;
import cn.edu.hit.ftcl.wearablepc.Common.Msg;
import cn.edu.hit.ftcl.wearablepc.Common.MsgAdapter;
import cn.edu.hit.ftcl.wearablepc.Common.MyRecyclerView;
import cn.edu.hit.ftcl.wearablepc.MyApplication;
import cn.edu.hit.ftcl.wearablepc.Network.NetworkUtil;
import cn.edu.hit.ftcl.wearablepc.Network.UserIPInfo;
import cn.edu.hit.ftcl.wearablepc.R;

public class SecretActivity extends AppCompatActivity {

    private List<Msg> mDataMsgs = new ArrayList<>();
    private List<String> mDataExpressions = new ArrayList<>();

    private LinearLayout mLayoutSecret;
    private LinearLayout mLayoutVoice;
    private LinearLayout mLayoutIndex;

    private Button mButtonVoice;
    private Button mButtonSecret;

    private Button mButtonBackVoice;
    private Button mButtonBackSecret;

    private MsgAdapter mAdapter;
    private ArrayAdapter mAdapterList;

    private MyRecyclerView mRecyclerView;

    private Button mButtonSend;

    private EditText mEditText;

    private ListView mListView;

    private AudioRecorderButton mRecorderButton;

    private IntentFilter intentFilter;
    private LocalReceiver localReceiver;
    private LocalBroadcastManager localBroadcastManager;

    private final UserIPInfo self = DataSupport.where("type = ?", String.valueOf(UserIPInfo.TYPE_SELF)).findFirst(UserIPInfo.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.secret_activity_secret);
        //设置ToolBar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_secret);
        setSupportActionBar(toolbar);

        //获取上一个Intent传入的数据
        Intent intent = getIntent();
        final int userId = intent.getIntExtra("user_id", 0);
        final String username = intent.getStringExtra("username");

        //设置标题
        this.setTitle(username);

        //聊天消息数据初始化
        initMsgAndExpression(userId);

        //RecyclerView
        mRecyclerView = (MyRecyclerView) findViewById(R.id.msg_recycler_view_secret);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new MsgAdapter(mDataMsgs);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.scrollToPosition(mDataMsgs.size() - 1);

        //Button：发送
        mButtonSend = (Button)findViewById(R.id.id_button_send);
        //EditText
        mEditText = (EditText)findViewById(R.id.id_edittext) ;

        //ListView
        mAdapterList = new ArrayAdapter<String>(SecretActivity.this, android.R.layout.simple_list_item_1, mDataExpressions);
        mListView = (ListView)findViewById(R.id.id_list_expression);
        mListView.setAdapter(mAdapterList);

        //Layout
        mLayoutSecret = (LinearLayout)findViewById(R.id.id_layout_secret);
        mLayoutVoice = (LinearLayout)findViewById(R.id.id_layout_voice);
        mLayoutIndex = (LinearLayout)findViewById(R.id.id_layout_index);
        mLayoutSecret.setVisibility(View.GONE);
        mLayoutVoice.setVisibility(View.GONE);
        mListView.setVisibility(View.GONE);

        //Button：切换到语音/条密
        mButtonSecret = (Button)findViewById(R.id.id_button_secret);
        mButtonVoice = (Button)findViewById(R.id.id_button_voice);

        //Button：返回按钮
        mButtonBackSecret = (Button)findViewById(R.id.id_button_back_secret);
        mButtonBackVoice = (Button)findViewById(R.id.id_button_back_voice);

        //Button：录音按钮
        mRecorderButton = (AudioRecorderButton) findViewById(R.id.id_recorder_button);

        //ListView点击事件
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clicked = mDataExpressions.get(position);
                if(clicked.equals("+ 编辑常用短语")){
                    Intent intent = new Intent(SecretActivity.this, ExpressionListActivity.class);
                    startActivity(intent);
                }else{
                    mEditText.setText(clicked);
                }
            }
        });

        //发送按钮点击事件
        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserIPInfo userIPInfo = DataSupport.find(UserIPInfo.class, userId);
                String content = mEditText.getText().toString().trim();
                if(content.isEmpty()){
                    Toast.makeText(MyApplication.getContext(), "输入不能为空", Toast.LENGTH_SHORT).show();
                }else {
                    mEditText.setText("");

                    //msg表add
                    long current = System.currentTimeMillis();
                    Msg msg = new Msg(self.getId(), userId, content, current, Msg.TYPE_SENT, Msg.CATAGORY_TEXT);
                    msg.save();
                    //secret表update
                    Secret secret = DataSupport.where("user_id = ?", String.valueOf(userId)).findFirst(Secret.class);
                    if (secret != null) {
                        secret.setContent(content);
                        secret.setTime(current);
                        secret.save();
                    } else {
                        Secret addSecret = new Secret(userId, userIPInfo.getUsername(), content, current);
                        addSecret.save();
                    }
                    //发送数据
                    NetworkUtil networkUtil = new NetworkUtil();
                    networkUtil.sendByTCP(userIPInfo.getIp(), userIPInfo.getPort(), "text", content);
                    //更新数据
                    mDataMsgs.add(msg);
                    mAdapter.notifyItemInserted(mDataMsgs.size() - 1);
                    mRecyclerView.scrollToPosition(mDataMsgs.size() - 1);
                }
            }
        });


        //录音完成后回调
        mRecorderButton.setFinishRecorderCallBack(new AudioRecorderButton.AudioFinishRecorderCallBack() {
            public void onFinish(long seconds, String filePath) {
                UserIPInfo userIPInfo = DataSupport.find(UserIPInfo.class, userId);
                //msg表add
                Msg msg = new Msg(self.getId(), userId, filePath, seconds, Msg.TYPE_SENT, Msg.CATAGORY_VOICE);
                msg.save();
                //secret表update
                Secret secret = DataSupport.where("user_id = ?", String.valueOf(userId)).findFirst(Secret.class);
                if (secret != null) {
                    secret.setContent("[语音]");
                    secret.setTime(seconds);
                    secret.save();
                } else {
                    Secret addSecret = new Secret(userId, userIPInfo.getUsername(), "[语音]", seconds);
                    addSecret.save();
                }
                //发送数据
                NetworkUtil networkUtil = new NetworkUtil();
                networkUtil.sendByTCP(userIPInfo.getIp(), userIPInfo.getPort(), "file", filePath);

                mDataMsgs.add(msg);
                //view更新数据
                mAdapter.notifyItemInserted(mDataMsgs.size() - 1);
                //设置位置
                mRecyclerView.scrollToPosition(mDataMsgs.size() - 1);
            }
        });

        //切换到语音发送
        mButtonVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLayoutIndex.setVisibility(View.GONE);
                mLayoutVoice.setVisibility(View.VISIBLE);
            }
        });

        //切换到条密发送
        mButtonSecret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLayoutIndex.setVisibility(View.GONE);
                mLayoutSecret.setVisibility(View.VISIBLE);
                mListView.setVisibility(View.VISIBLE);
            }
        });

        //回到主界面
        mButtonBackSecret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLayoutSecret.setVisibility(View.GONE);
                mListView.setVisibility(View.GONE);
                mLayoutIndex.setVisibility(View.VISIBLE);
            }
        });
        mButtonBackVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLayoutVoice.setVisibility(View.GONE);
                mLayoutIndex.setVisibility(View.VISIBLE);
            }
        });

        //注册广播接收器
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.hitwearable.LOCAL_BROADCAST_SECRET");
        localReceiver = new LocalReceiver(userId);
        localBroadcastManager.registerReceiver(localReceiver, intentFilter);
    }

    @Override
    public void onBackPressed() {//按下返回后重新进入SecretListActivity
        Intent intent = new Intent(SecretActivity.this, SecretListActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 本地广播接收器
     */
    class LocalReceiver extends BroadcastReceiver {
        private int userId;
        LocalReceiver(int userId){
            this.userId = userId;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            Msg msg = (Msg)bundle.getSerializable("msg");
            if(msg.getSender() == userId) {//如果收到的消息是当前用户发送的
                mDataMsgs.add(msg);
                //view更新数据
                mAdapter.notifyItemInserted(mDataMsgs.size() - 1);
                //设置位置
                mRecyclerView.scrollToPosition(mDataMsgs.size() - 1);
            }
        }
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
    protected void onRestart() {
        super.onRestart();
        LogUtil.d("SecretActivity", "onRestart");

        mDataExpressions = new ArrayList<>();
        //数据库查询常用短语
        List<Expression> expressionList = DataSupport.findAll(Expression.class);
        for(Expression e : expressionList){
            mDataExpressions.add(e.getContent());
        }
        mDataExpressions.add("+ 编辑常用短语");
        mAdapterList = new ArrayAdapter<String>(SecretActivity.this, android.R.layout.simple_list_item_1, mDataExpressions);
        mListView = (ListView)findViewById(R.id.id_list_expression);
        mListView.setAdapter(mAdapterList);
    }

    /**
     * 初始化消息列表和常用短语
     */
    private void initMsgAndExpression(int userId){
        //数据库查询指定队友的条密和语音
        mDataMsgs = DataSupport
                .where("(catagory = ? or catagory = ?) and (receiver = ? or sender = ?)", String.valueOf(Msg.CATAGORY_TEXT), String.valueOf(Msg.CATAGORY_VOICE), String.valueOf(userId), String.valueOf(userId))
                .find(Msg.class);
        //数据库查询常用短语
        List<Expression> expressionList = DataSupport.findAll(Expression.class);
        for(Expression e : expressionList){
            mDataExpressions.add(e.getContent());
        }
        mDataExpressions.add("+ 编辑常用短语");
    }
}
