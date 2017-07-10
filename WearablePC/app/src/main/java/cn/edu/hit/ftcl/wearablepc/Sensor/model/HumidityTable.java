package cn.edu.hit.ftcl.wearablepc.Sensor.model;

import org.litepal.crud.DataSupport;

import java.util.Date;

/**
 * Created by HFZ on 2017/6/2.
 */

public class HumidityTable extends DataSupport {
    private Date recordDate;
    private double humidity;
    private String sensorName;

    public void setRecordDate(Date date){
        recordDate = date;
    }

    public void setHumidity(double humidity){
        this.humidity = humidity;
    }

    public void setSensorName(String sensorName){
        this.sensorName = sensorName;
    }

    public double getHumidity(){
        return humidity;
    }

    public Date getRecordDate(){
        return recordDate;
    }

    public String getSensorName(){
        return sensorName;
    }
}
