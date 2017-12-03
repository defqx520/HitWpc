package cn.edu.hit.ftcl.wearablepc.Secret;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import cn.edu.hit.ftcl.wearablepc.Common.Msg;
import cn.edu.hit.ftcl.wearablepc.MyApplication;
import cn.edu.hit.ftcl.wearablepc.Network.NetworkUtil;
import cn.edu.hit.ftcl.wearablepc.Network.UserIPInfo;
import cn.edu.hit.ftcl.wearablepc.R;

public class GroupActivity extends AppCompatActivity {

    private List<UserIPInfo> mDatas = new ArrayList<>();

    private UserIPAdapter mAdapter;

    private ListView mListView;

    private EditText mEditText;

    private Button mButton;

    private List<String> mDatasExp = new ArrayList<>();

    private ArrayAdapter mAdapterExp;

    private ListView mListViewExp;

    private List<UserIPInfo> mDatasGroup = new ArrayList<>();//选中的用户

    private final UserIPInfo self = DataSupport.where("type = ?", String.valueOf(UserIPInfo.TYPE_SELF)).findFirst(UserIPInfo.class);

    private boolean isChooseAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.secret_activity_group);
        //数据初始化
        initUserIPInfoAndExpression();

        //Toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_group);
        setSupportActionBar(toolbar);

        //ListView：userip列表
        mAdapter = new UserIPAdapter(GroupActivity.this, R.layout.secret_item_userip_group, mDatas);
        mListView = (ListView)findViewById(R.id.id_list_user_ip_group);
        mListView.setAdapter(mAdapter);

        //EditText
        mEditText = (EditText)findViewById(R.id.id_edittext_group);
        //Button
        mButton = (Button)findViewById(R.id.id_button_send_group);

        //ListView：expression列表
        mAdapterExp = new ArrayAdapter<String>(GroupActivity.this, android.R.layout.simple_list_item_1, mDatasExp);
        mListViewExp = (ListView)findViewById(R.id.id_list_expression_group);
        mListViewExp.setAdapter(mAdapterExp);

        //ListView点击事件
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserIPInfo clicked = mDatas.get(position);
                if(clicked.getType() != UserIPInfo.TYPE_SELF) {//限制不可点击自己
                    UserIPAdapter.ViewHolder viewHolder = (UserIPAdapter.ViewHolder) view.getTag();
                    CheckBox checkBox = viewHolder.checkBox;
                    if (mDatasGroup.contains(clicked)) {
                        mDatasGroup.remove(clicked);
                        checkBox.setChecked(false);
                    } else {
                        mDatasGroup.add(clicked);
                        checkBox.setChecked(true);
                    }
                }
            }
        });

        //Button点击事件
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = mEditText.getText().toString().trim();
                if(content.isEmpty()){
                    Toast.makeText(MyApplication.getContext(), "输入不能为空", Toast.LENGTH_SHORT).show();
                }else if(mDatasGroup.size() == 0){
                    Toast.makeText(MyApplication.getContext(), "请选择您要群发的对象", Toast.LENGTH_SHORT).show();
                }else {
                    mEditText.setText("");

                    //Msg表add
                    long current = System.currentTimeMillis();
                    List<Msg> msgList = new ArrayList<>();
                    for (UserIPInfo ele : mDatasGroup) {
                        Msg msg = new Msg(self.getId(), ele.getId(), content, current, Msg.TYPE_SENT, Msg.CATAGORY_TEXT);
                        msgList.add(msg);
                    }
                    DataSupport.saveAll(msgList);
                    //Secret表update
                    for (UserIPInfo ele : mDatasGroup){
                        Secret secret = DataSupport.where("user_id = ?", String.valueOf(ele.getId())).findFirst(Secret.class);
                        if(secret != null){
                            secret.setContent(content);
                            secret.setTime(current);
                            secret.save();
                        }else{
                            Secret addSecret = new Secret(ele.getId(), ele.getUsername(), content, current);
                            addSecret.save();
                        }
                    }
                    //TODO: 发送数据，需要改成调用广播接口
                    NetworkUtil networkUtil = new NetworkUtil();
                    for (UserIPInfo ele : mDatasGroup){
                        networkUtil.sendByTCP(ele.getIp(), ele.getPort(), "text", content);
                    }
                    Toast.makeText(MyApplication.getContext(), "发送成功", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //List点击事件
        mListViewExp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clicked = mDatasExp.get(position);
                if(clicked.equals("+ 编辑常用短语")){
                    Intent intent = new Intent(GroupActivity.this, ExpressionListActivity.class);
                    startActivity(intent);
                }else{
                    mEditText.setText(clicked);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {//按下返回后重新进入SecretListActivity
        Intent intent = new Intent(GroupActivity.this, SecretListActivity.class);
        startActivity(intent);
        finish();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.toolbar_group, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()){
//            case R.id.choose_all:
//                if(!isChooseAll) {
//                    isChooseAll = true;
//                    item.setTitle("取消");
//                    mDatasGroup.clear();
//                    mDatasGroup = DataSupport.where("type != ?", String.valueOf(UserIPInfo.TYPE_SELF)).find(UserIPInfo.class);
//
//                }else{
//                    isChooseAll = false;
//                    item.setTitle("全选");
//                    mDatasGroup.clear();
//
//                }
//        }
//        return true;
//    }

    //数据初始化
    private void initUserIPInfoAndExpression(){
        //查询IP配置
        mDatas = DataSupport.findAll(UserIPInfo.class);
        //查询常用短语
        List<Expression> expressionList = DataSupport.findAll(Expression.class);
        for(Expression e : expressionList){
            mDatasExp.add(e.getContent());
        }
        mDatasExp.add("+ 编辑常用短语");
    }

    public class UserIPAdapter extends ArrayAdapter<UserIPInfo> {
        private int resourceId;

        public UserIPAdapter(Context context, int resource, List<UserIPInfo> objects) {
            super(context, resource, objects);
            resourceId = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            UserIPInfo userIPInfo = getItem(position);
            View view;
            ViewHolder viewHolder;
            if(convertView == null){
                view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.username = (TextView)view.findViewById(R.id.id_username_group);
                viewHolder.ip = (TextView)view.findViewById(R.id.id_ip_group);
                viewHolder.port = (TextView)view.findViewById(R.id.id_port_group);
                viewHolder.checkBox = (CheckBox)view.findViewById(R.id.id_checkbox_group);
                view.setTag(viewHolder);
            }else{
                view = convertView;
                viewHolder = (ViewHolder)view.getTag();
            }
            viewHolder.username.setText(userIPInfo.getUsername());
            if(userIPInfo.getIp() == null || userIPInfo.getIp().isEmpty()){
                viewHolder.ip.setText("未设置");
                viewHolder.checkBox.setEnabled(false);//IP地址未配置，多选框不可用
            }else {
                viewHolder.ip.setText(userIPInfo.getIp());
            }
            if(userIPInfo.getPort() == 0) {
                viewHolder.port.setText("未设置");
                viewHolder.checkBox.setEnabled(false);//IP地址未配置，多选框不可用
            }else{
                viewHolder.port.setText(String.valueOf(userIPInfo.getPort()));
            }
            if(userIPInfo.getType() == UserIPInfo.TYPE_SELF){
                viewHolder.checkBox.setEnabled(false);//不能发给自己，多选框不可用
            }
            return view;
        }

        class ViewHolder{
            TextView username;
            TextView ip;
            TextView port;
            CheckBox checkBox;
        }
    }
}
