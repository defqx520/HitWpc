package cn.edu.hit.ftcl.wearablepc;

import android.app.Application;
import android.content.Context;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import org.litepal.LitePalApplication;

/**
 * 用于全局获取Context
 * Created by hzf on 2017/5/19.
 */

public class MyApplication extends  Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
        SpeechUtility.createUtility(context, SpeechConstant.APPID +"=5944c86a");
        LitePalApplication.initialize(context);
    }

    public static Context getContext(){
        return context;
    }
}
