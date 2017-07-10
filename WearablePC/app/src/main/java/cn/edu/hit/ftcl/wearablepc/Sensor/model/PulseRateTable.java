package cn.edu.hit.ftcl.wearablepc.Sensor.model;

import org.litepal.crud.DataSupport;

import java.util.Date;

/**
 * Created by HFZ on 2017/6/2.
 */

public class PulseRateTable extends DataSupport{
    private Date recordDate;
    private int pulseRate;
    private String sensorName;

    public void setRecordDate(Date date){
        recordDate = date;
    }

    public void setPulseRate(int pulseRate){
        this.pulseRate = pulseRate;
    }

    public void setSensorName(String sensorName){
        this.sensorName = sensorName;
    }

    public int getPulseRate(){
        return pulseRate;
    }

    public Date getRecordDate(){
        return recordDate;
    }

    public String getSensorName(){
        return sensorName;
    }
}
