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
import android.widget.ListView;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import cn.edu.hit.ftcl.wearablepc.Communication.LogUtil;
import cn.edu.hit.ftcl.wearablepc.Communication.Msg;
import cn.edu.hit.ftcl.wearablepc.Communication.MsgAdapter;
import cn.edu.hit.ftcl.wearablepc.Communication.MyRecyclerView;
import cn.edu.hit.ftcl.wearablepc.Network.NetworkUtil;
import cn.edu.hit.ftcl.wearablepc.Network.UserIPInfo;
import cn.edu.hit.ftcl.wearablepc.R;

public class SecretActivity extends AppCompatActivity {

    private List<Msg> mDataMsgs = new ArrayList<>();
    private List<String> mDataExpressions = new ArrayList<>();

    private MsgAdapter mAdapter;
    private ArrayAdapter mAdapterList;

    private MyRecyclerView mRecyclerView;

    private Button mButtonSend;

    private EditText mEditText;

    private ListView mListView;

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

        Intent intent = getIntent();
        final int userId = intent.getIntExtra("user_id", 0);
        final String username = intent.getStringExtra("username");

        //设置标题
        this.setTitle(username);

        //聊天消息数据初始化
        initMsgAndExpression(userId);

        //RecyclerView
        mRecyclerView = (MyRecyclerView) findViewById(R.id.msg_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new MsgAdapter(mDataMsgs);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.scrollToPosition(mDataMsgs.size() - 1);

        //Button
        mButtonSend = (Button)findViewById(R.id.id_button_send);
        //EditText
        mEditText = (EditText)findViewById(R.id.id_edittext) ;

        //ListView
        mAdapterList = new ArrayAdapter<String>(SecretActivity.this, android.R.layout.simple_list_item_1, mDataExpressions);
        mListView = (ListView)findViewById(R.id.id_list_expression);
        mListView.setAdapter(mAdapterList);

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
                String content = mEditText.getText().toString();

                //msg表add
                long current = System.currentTimeMillis();
                Msg msg = new Msg(self.getId(), userId, content, current, Msg.TYPE_SENT, Msg.CATAGORY_TEXT);
                msg.save();
                //secret表update
                Secret secret = DataSupport.where("user_id = ?", String.valueOf(userId)).findFirst(Secret.class);
                if(secret != null){
                    secret.setContent(content);
                    secret.setTime(current);
                    secret.save();
                }else {
                    Secret addSecret = new Secret(userId, userIPInfo.getUsername(), content, current);
                    addSecret.save();
                }
                //发送数据
                NetworkUtil networkUtil = new NetworkUtil();
                networkUtil.sendByTCP(userIPInfo.getIp(), userIPInfo.getPort(), "text", content);
            }
        });

        //注册广播接收器
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.hitwearable.LOCAL_BROADCAST");
        localReceiver = new LocalReceiver(userId);
        localBroadcastManager.registerReceiver(localReceiver, intentFilter);
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
        //数据库查询指定队友的条密
        mDataMsgs = DataSupport
                .where("catagory = ? and (receiver = ? or sender = ?)", String.valueOf(Msg.CATAGORY_TEXT), String.valueOf(userId), String.valueOf(userId))
                .find(Msg.class);
        //数据库查询常用短语
        List<Expression> expressionList = DataSupport.findAll(Expression.class);
        for(Expression e : expressionList){
            mDataExpressions.add(e.getContent());
        }
        mDataExpressions.add("+ 编辑常用短语");
    }
}
