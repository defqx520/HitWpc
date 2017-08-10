package cn.edu.hit.ftcl.wearablepc.Sensor;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.model.Marker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.edu.hit.ftcl.wearablepc.MyApplication;
import cn.edu.hit.ftcl.wearablepc.R;
import cn.edu.hit.ftcl.wearablepc.Sensor.model.HeartRateTable;

public class SensorActivity extends AppCompatActivity {
    /*
    private TextView last_update_time_tv;
    private Button warnning, cancle;
    int colorPrimary;
    private TextView temperature_tv, alcohol_tv, humidity_tv, pressure_tv;
    */

    private TextView time_y_m_d,time_h_m,warning_state;
    private TextView heart_rate,temperature,humidity,pressure,alcohol,pulse;
    private Switch warning_switch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);
        // 此处 layout : activity_sensor.xml 已更改为横屏适配模式 by Yumi 2017/8/10 16:25

        //last_update_time_tv = (TextView)findViewById(R.id.last_update_time);
        //last_update_time_tv.setText(getSystemTime());
        time_y_m_d = (TextView)findViewById(R.id.time_y_m_d);
        time_h_m = (TextView)findViewById(R.id.time_h_m);
        time_y_m_d.setText(getSystemYMD());
        time_h_m.setText(getSystemHM());

        //colorPrimary = last_update_time_tv.getCurrentTextColor();
        //temperature_tv = (TextView) findViewById(R.id.temperature_tv);
        //alcohol_tv = (TextView) findViewById(R.id.alcohol_tv);
        //humidity_tv = (TextView) findViewById(R.id.humidity_tv);
        //pressure_tv = (TextView) findViewById(R.id.pressure_tv);

        heart_rate = (TextView) findViewById(R.id.heartRateNumber);
        temperature = (TextView) findViewById(R.id.temperatureNumber);
        humidity = (TextView) findViewById(R.id.humidityNumber);
        pressure = (TextView) findViewById(R.id.airPressureNumber);
        alcohol = (TextView) findViewById(R.id.alcoholNumber);
        pulse = (TextView) findViewById(R.id.pulseNumber);
        warning_state = (TextView) findViewById(R.id.warning_state);
        warning_switch = (Switch) findViewById(R.id.warning_switch);
        // switch开关用于管理预警开启和关闭
        warning_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    Toast.makeText(getApplicationContext(),"预警功能开启",Toast.LENGTH_SHORT).show();
                    warning_state.setText("已开启");
                    Intent intent = new Intent(SensorActivity.this, SensorReceiveService.class);
                    startService(intent);
                }else {
                    Toast.makeText(getApplicationContext(),"预警功能关闭",Toast.LENGTH_SHORT).show();
                    warning_state.setText("已关闭");
                    Intent stopServiceIntent = new Intent(SensorActivity.this, SensorReceiveService.class);
                    stopService(stopServiceIntent);
                }
            }
        });
        /*
        warnning = (Button)findViewById(R.id.button_warnning);
        cancle = (Button) findViewById(R.id.button_warnning_cancle);
        warnning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SensorActivity.this, SensorReceiveService.class);
                startService(intent);
            }
        });
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                TextView tv = (TextView)findViewById(R.id.heartrateValue6);
//                tv.setTextColor(colorPrimary);
                Intent stopServiceIntent = new Intent(SensorActivity.this, SensorReceiveService.class);
                stopService(stopServiceIntent);
            }
        });
        */

        // showSensorData();
    }

    /**
     * 展示传感器数据
     */
    private void showSensorData() {
        Uri uri = Uri.parse("content://hfz.example.com.bluetooth.provider");
        Cursor cursor0 = getContentResolver().query(uri,null,"TemperatureTable",null,null);
        cursor0.moveToLast();
        double temperatureValue = cursor0.getDouble(cursor0.getColumnIndex("temperature"));

        Cursor cursor1 = getContentResolver().query(uri,null,"HumidityTable",null,null);
        cursor1.moveToLast();
        double humidityValue = cursor1.getDouble(cursor1.getColumnIndex("humidity"));

        Cursor cursor2 = getContentResolver().query(uri,null,"PressureTable",null,null);
        cursor2.moveToLast();
        double pressureValue = cursor2.getDouble(cursor2.getColumnIndex("pressure"));

        Cursor cursor3 = getContentResolver().query(uri,null,"AlcoholDetectionTable",null,null);
        cursor3.moveToLast();
        int concentration = cursor3.getInt(cursor3.getColumnIndex("concentration"));

        //heart_rate.setText(); 暂无心率数据
        temperature.setText(""+temperatureValue);
        humidity.setText(""+humidityValue);
        pressure.setText(""+pressureValue);
        alcohol.setText(""+concentration);
        //pulse.setText(); 暂无脉搏数据

        /*
        temperature_tv.setText(""+temperatureValue);
        humidity_tv.setText(""+humidityValue);
        pressure_tv.setText(""+pressureValue);
        alcohol_tv.setText(""+concentration);
        */

        cursor0.close();
        cursor1.close();
        cursor2.close();
        cursor3.close();
    }

    /**
     * 获取传感器数据
     * @return
     */
    private Map<String, String> readSensorData() {
        Map<String, String> tempmap = new HashMap<>();

        return tempmap;
    }


    public String getSystemYMD(){
        SimpleDateFormat format =new SimpleDateFormat("yyyy-MM-dd");
        Date curdate = new Date(System.currentTimeMillis());
        String str = format.format(curdate);
        return str;
    }

    public String getSystemHM(){
        SimpleDateFormat format =new SimpleDateFormat("hh:mm");
        Date curdate = new Date(System.currentTimeMillis());
        String str = format.format(curdate);
        return str;
    }

    public void warn(){
        VibratorUtil.Vibrate(SensorActivity.this, 1000);
//        TextView tv = (TextView)findViewById(R.id.heartrateValue6);
//        tv.setTextColor(Color.RED);
        AlertDialog.Builder builder = new AlertDialog.Builder(MyApplication.getContext());
        builder.setTitle("警告");
        builder.setMessage("当前环境中CH4浓度超标，有爆炸风险，请注意。");
        builder.setPositiveButton("发送", new OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //do something
                Toast.makeText(getApplicationContext(),"已通知指挥端", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("取消", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //just cancle
            }
        });
        builder.show();
    }
}
