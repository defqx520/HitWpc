package cn.edu.hit.ftcl.wearablepc.Sensor.model;

import org.litepal.crud.DataSupport;

import java.util.Date;

/**
 * Created by HFZ on 2017/6/1.
 */

public class HeartRateTable extends DataSupport {
    private Date recordDate;
    private int heartBeat;
    private String sensorName;

    public void setRecordDate(Date date){
        recordDate = date;
    }

    public void setHeartBeat(int heartBeat){
        this.heartBeat = heartBeat;
    }

    public void setSensorName(String sensorName){
        this.sensorName = sensorName;
    }

    public int getHeartBeat(){
        return heartBeat;
    }

    public Date getRecordDate(){
        return recordDate;
    }

    public String getSensorName(){
        return sensorName;
    }
}
