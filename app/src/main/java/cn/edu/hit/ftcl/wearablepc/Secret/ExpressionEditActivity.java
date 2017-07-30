package cn.edu.hit.ftcl.wearablepc.Secret;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.litepal.crud.DataSupport;

import cn.edu.hit.ftcl.wearablepc.R;

public class ExpressionEditActivity extends AppCompatActivity {

    private EditText mEditText;

    private Button mButtonEdit;

    private Button mButtonDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.secret_activity_expression_edit);

        Intent intent = getIntent();
        final int expressionId = intent.getIntExtra("expression_id", 0);
        final int position = intent.getIntExtra("position", -1);
        final Expression expression = DataSupport.find(Expression.class, expressionId);

        mEditText = (EditText)findViewById(R.id.id_edit_content);
        mEditText.setText(expression.getContent());
        mButtonEdit = (Button)findViewById(R.id.id_btn_edit);
        mButtonDelete = (Button)findViewById(R.id.id_btn_delete);

        mButtonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = mEditText.getText().toString().trim();
                expression.setContent(content);
                expression.update(expressionId);

                Intent intent = new Intent();
                intent.putExtra("result", "edit");
                intent.putExtra("position", position);
                intent.putExtra("content", content);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        mButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataSupport.delete(Expression.class, expressionId);

                Intent intent = new Intent();
                intent.putExtra("result", "delete");
                intent.putExtra("position", position);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
