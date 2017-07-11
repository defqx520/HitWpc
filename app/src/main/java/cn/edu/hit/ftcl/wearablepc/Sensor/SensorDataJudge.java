package cn.edu.hit.ftcl.wearablepc.Sensor;

/**
 * Created by defqx on 2017/6/16.
 */

public class SensorDataJudge {
    public static boolean judgeHeartRate(int heartRate){
        if(heartRate>=50 && heartRate<=160){
            return true;
        }
        return false;
    }

    public static boolean judgeHumidity(double humidity){
        if(humidity>= 0.2 && humidity<=0.5){
            return true;
        }
        return false;
    }
}
