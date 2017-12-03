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

import java.util.ArrayList;
import java.util.List;

import cn.edu.hit.ftcl.wearablepc.Common.LogUtil;
import cn.edu.hit.ftcl.wearablepc.R;

public class ExpressionListActivity extends AppCompatActivity {

    private static final int REQUEST_EDIT = 1;
    private static final int REQUEST_ADD = 2;

    private ExpressionAdapter mAdapter;

    private List<Expression> mDataExpressionBeen = new ArrayList<>();

    private ListView mListView;

    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.secret_activity_expression_list);
        //设置ToolBar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_expression_list);
        setSupportActionBar(toolbar);

        mDataExpressionBeen = DataSupport.findAll(Expression.class);

        //ListView
        mAdapter = new ExpressionAdapter(ExpressionListActivity.this, R.layout.secret_item_expression, mDataExpressionBeen);
        mListView = (ListView)findViewById(R.id.id_list_expressions);
        mListView.setAdapter(mAdapter);
        //Button
        mButton = (Button) findViewById(R.id.id_btn_plus);

        //点击事件
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Expression clicked = mDataExpressionBeen.get(position);
                Intent intent = new Intent(ExpressionListActivity.this, ExpressionEditActivity.class);
                intent.putExtra("expression_id", clicked.getId());
                intent.putExtra("position", position);
                startActivityForResult(intent, REQUEST_EDIT);
            }
        });
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExpressionListActivity.this, ExpressionAddActivity.class);
                startActivityForResult(intent, REQUEST_ADD);
            }
        });
    }


    //    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_EDIT:
                if(resultCode == RESULT_OK){
                    LogUtil.d("ExpressionListActivity", "REQUEST_EDIT");
                    String result = data.getStringExtra("result");
                    int pos = data.getIntExtra("position", -1);
                    String content = data.getStringExtra("content");
                    if(result.equals("delete")){
                        mDataExpressionBeen.remove(pos);
                        mAdapter.notifyDataSetChanged();
                    }else if(result.equals("edit")){
                        mDataExpressionBeen.get(pos).setContent(content);
                        mAdapter.notifyDataSetChanged();
                    }
                }
                break;
            case REQUEST_ADD:
                if(resultCode == RESULT_OK){
                    LogUtil.d("ExpressionListActivity", "REQUEST_ADD");
                    int id = data.getIntExtra("id", 0);
                    String content = data.getStringExtra("content");
                    Expression expression = new Expression();
                    expression.setId(id);
                    expression.setContent(content);
                    mDataExpressionBeen.add(expression);
                    mAdapter.notifyDataSetChanged();
                }
        }
    }

    public class ExpressionAdapter extends ArrayAdapter<Expression>{
        private int resourceId;

        public ExpressionAdapter(Context context, int resource, List<Expression> objects) {
            super(context, resource, objects);
            resourceId = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Expression expression = getItem(position);
            View view;
            ViewHolder viewHolder;
            if(convertView == null){
                view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.content = (TextView)view.findViewById(R.id.id_content);
                view.setTag(viewHolder);
            }else{
                view = convertView;
                viewHolder = (ViewHolder)view.getTag();
            }
            viewHolder.content.setText(expression.getContent());
            return view;
        }

        class ViewHolder{
            TextView content;
        }
    }
}
