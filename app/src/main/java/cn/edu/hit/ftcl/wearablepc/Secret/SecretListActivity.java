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
import android.widget.ListView;
import android.widget.TextView;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cn.edu.hit.ftcl.wearablepc.R;

public class SecretListActivity extends AppCompatActivity {
    private static final String TAG = SecretListActivity.class.getSimpleName();

    private SecretListAdapter mAdapter;
    private ListView mListView;
    private Button mButton;

    private List<Secret> mDatas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.secret_activity_secret_list);

        //设置ToolBar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_secret_list);
        setSupportActionBar(toolbar);

        initDatas();

        mAdapter = new SecretListAdapter(SecretListActivity.this, R.layout.secret_item_secret, mDatas);
        mListView = (ListView)findViewById(R.id.id_list_secret);
        mListView.setAdapter(mAdapter);
        mButton = (Button)findViewById(R.id.id_btn_broadcast);

        //点击事件
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Secret clicked = mDatas.get(position);
                //进入条密语音对话界面
                Intent intent = new Intent(SecretListActivity.this, SecretActivity.class);
                intent.putExtra("user_id", clicked.getUser_id());
                intent.putExtra("username", clicked.getUsername());
                startActivity(intent);
                //finish：解决数据更新的问题
                finish();
            }
        });
        //长按事件
//        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                Secret clicked = mDatas.get(position);
//                //删除
//                clicked.delete();
//                //更新UI
//                mDatas.remove(position);
//                mAdapter.notifyDataSetChanged();
//                return true;
//            }
//        });
        //按钮点击事件
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进入广播通信界面
                Intent intent = new Intent(SecretListActivity.this, GroupActivity.class);
                startActivity(intent);
                //finish
                finish();
            }
        });
    }

    private void initDatas() {
        mDatas = DataSupport.findAll(Secret.class);
    }

    public class SecretListAdapter extends ArrayAdapter<Secret> {
        private int resourceId;

        public SecretListAdapter(Context context, int resource, List<Secret> objects) {
            super(context, resource, objects);
            resourceId = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Secret secret = getItem(position);
            View view;
            ViewHolder viewHolder;
            if(convertView == null){
                view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.username = (TextView)view.findViewById(R.id.id_username);
                viewHolder.content = (TextView)view.findViewById(R.id.id_content);
                viewHolder.time = (TextView)view.findViewById(R.id.id_time);
                view.setTag(viewHolder);
            }else{
                view = convertView;
                viewHolder = (ViewHolder)view.getTag();
            }
            viewHolder.username.setText(secret.getUsername());
            viewHolder.content.setText(secret.getContent());
            viewHolder.time.setText(new SimpleDateFormat("MM-dd HH:mm").format(secret.getTime()));
            return view;
        }

        class ViewHolder{
            TextView username;
            TextView content;
            TextView time;
        }
    }
}
