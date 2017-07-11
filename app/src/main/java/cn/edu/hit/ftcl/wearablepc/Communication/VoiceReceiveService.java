package cn.edu.hit.ftcl.wearablepc.Communication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * 接收语音消息服务
 */
public class VoiceReceiveService extends Service {

    NetworkUtil networkUtil;

    public VoiceReceiveService() {
        networkUtil = new NetworkUtil();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //从数据库中读取参数
        List<Parameter> targetIPList = DataSupport.where("name = ?", "target_ip").find(Parameter.class);
        String targetIP = targetIPList.get(0).getValue();//192.168.1.133
        List<Parameter> targetPortList = DataSupport.where("name = ?", "target_file_port").find(Parameter.class);
        String targetPort = targetPortList.get(0).getValue();//29999
        //接收文件
        networkUtil.receiveFileBySocket(targetIP, Integer.parseInt(targetPort), Environment.getExternalStorageDirectory() + "/HitWearable/voice");

        //接收文本消息
        List<Parameter> localPortList = DataSupport.where("name = ?", "local_text_port").find(Parameter.class);
        String textLocalPort = localPortList.get(0).getValue();//26666
        networkUtil.receiveTextByDatagram(Integer.parseInt(textLocalPort));

        //定时任务：每秒接收一次
        AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
        long triggerAtTime = SystemClock.elapsedRealtime() + 1000;
        Intent i = new Intent(this, VoiceReceiveService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }
}
