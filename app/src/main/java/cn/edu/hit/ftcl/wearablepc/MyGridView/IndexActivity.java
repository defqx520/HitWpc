package cn.edu.hit.ftcl.wearablepc.MyGridView;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.litepal.crud.DataSupport;

import cn.edu.hit.ftcl.wearablepc.GDMap.mapview.MapActivity;
import cn.edu.hit.ftcl.wearablepc.GDMap.offlinemap.OfflineMapActivity;
import cn.edu.hit.ftcl.wearablepc.R;
import cn.edu.hit.ftcl.wearablepc.Sensor.SensorActivity;
import cn.edu.hit.ftcl.wearablepc.VoiceControl.VoiceControlActivity;
import cn.edu.hit.ftcl.wearablepc.Communication.Parameter;
import cn.edu.hit.ftcl.wearablepc.Communication.SettingsActivity;
import cn.edu.hit.ftcl.wearablepc.Communication.VoiceActivity;
import cn.edu.hit.ftcl.wearablepc.WifiCamera.FtpFileListActivity;

public class IndexActivity extends AppCompatActivity {
    MyGridLayout grid;
    int[] srcs = { R.drawable.actions_booktag, R.drawable.actions_about,
            R.drawable.actions_comment,  R.drawable.actions_account };
    String titles[] = { "地图", "感知", "通信", "无线" };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.index);

        grid = (MyGridLayout) findViewById(R.id.list);
        grid.setGridAdapter(new MyGridLayout.GridAdatper() {
            @Override
            public View getView(int index) {
                View view = getLayoutInflater().inflate(R.layout.actions_item, null);
                ImageView iv = (ImageView) view.findViewById(R.id.iv);
                TextView tv = (TextView) view.findViewById(R.id.tv);
                iv.setImageResource(srcs[index]);
                tv.setText(titles[index]);
                return view;
            }

            @Override
            public int getCount() {
                // TODO Auto-generated method stub
                return titles.length;
            }
        });
        grid.setOnItemClickListener(new MyGridLayout.OnItemClickListener() {

            @Override
            public void onItemClick(View v, int index) {
                switch (index){
                    case 0:
                        Intent intent0 = new Intent(IndexActivity.this, MapActivity.class);
                        startActivity(intent0);
                        break;
                    case 1:
                        Intent intent1 = new Intent(IndexActivity.this, SensorActivity.class);
                        startActivity(intent1);
                        break;
                    case 2:
                        Intent intent2 = new Intent(IndexActivity.this, VoiceActivity.class);
                        startActivity(intent2);
                        break;
                    case 3:
                        Intent intent3 = new Intent(IndexActivity.this, FtpFileListActivity.class);
                        startActivity(intent3);
                        break;
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.offline_down:
                Intent offline_intent = new Intent(IndexActivity.this, OfflineMapActivity.class);
                startActivity(offline_intent);
                return true;
            case R.id.about_us:
                return true;
            case R.id.settings:
                Intent settings_intent = new Intent(IndexActivity.this, SettingsActivity.class);
                startActivity(settings_intent);
                return true;
            case R.id.voicetest:
                Intent voice_intent = new Intent(IndexActivity.this, VoiceControlActivity.class);
                startActivity(voice_intent);
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            moveTaskToBack(false);
        }
        return true;
    }
}
