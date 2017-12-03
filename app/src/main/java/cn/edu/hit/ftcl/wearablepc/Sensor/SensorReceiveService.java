package cn.edu.hit.ftcl.wearablepc.Sensor;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import cn.edu.hit.ftcl.wearablepc.R;
import cn.edu.hit.ftcl.wearablepc.Sensor.model.HeartRateTable;
import cn.edu.hit.ftcl.wearablepc.Sensor.model.HumidityTable;

import static java.lang.Thread.sleep;

/**
 * Created by defqx on 2017/6/16.
 */

public class SensorReceiveService extends Service {
    String TAG = "LOG OUT";
    Context context;
    private HeartRateTable heartRateTable;
    private HumidityTable humidityTable;
    boolean isHeartRate;
    boolean isHumidity;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        super.onCreate();
        Log.d(TAG, "onCreate: ");
        context = getApplicationContext();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        new Thread(new Runnable() {
            @Override
            public void run() {
                isHeartRate = false;//SensorDataJudge.judgeHeartRate(heartRateTable.getHeartBeat());
                isHumidity = true;//SensorDataJudge.judgeHumidity(humidityTable.getHumidity());
                while(true) {

                    if (!isHeartRate || !isHumidity) {
                        isHeartRate = true;
                        CreateInform();
                    }
                    try {
                        sleep(30000);    //每隔30秒监测一次
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

//        heartRateTable = DataSupport.findLast(HeartRateTable.class);
//        humidityTable = DataSupport.findLast(HumidityTable.class);



//        //每隔1秒检测一次
//        AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
//        long triggerAtTime = SystemClock.elapsedRealtime() + 2000;
//        Intent i = new Intent(this, SensorReceiveService.class);
//        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
//        manager.cancel(pi);
//        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }

    public void CreateInform() {
        Log.d(TAG, "CreateInform: ");
        //定义一个PendingIntent，当用户点击通知时，跳转到某个Activity(也可以发送广播等)
        Intent intent = new Intent(context, SensorActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        //创建一个通知
        NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("人体监测与环境感知")
                .setContentText("心率过速，请注意")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVibrate(new long[]{500, 500})
                .build();
        startForeground(1, notification);
        nm.notify(1, notification);
    }
}
