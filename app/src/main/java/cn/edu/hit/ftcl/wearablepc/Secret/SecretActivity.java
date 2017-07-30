package cn.edu.hit.ftcl.wearablepc.Secret;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.secret_activity_secret);

        //聊天消息数据初始化
        initMsgAndExpression();

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

        //点击事件
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
    private void initMsgAndExpression(){
        //数据库查询所有条密
        mDataMsgs = DataSupport.where("catagory = ?", String.valueOf(Msg.CATAGORY_TEXT)).find(Msg.class);
        //数据库查询常用短语
        List<Expression> expressionList = DataSupport.findAll(Expression.class);
        for(Expression e : expressionList){
            mDataExpressions.add(e.getContent());
        }
        mDataExpressions.add("+ 编辑常用短语");
    }
}
