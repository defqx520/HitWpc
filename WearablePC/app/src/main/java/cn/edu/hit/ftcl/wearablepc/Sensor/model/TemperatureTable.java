package cn.edu.hit.ftcl.wearablepc.Sensor.model;

import org.litepal.crud.DataSupport;

import java.util.Date;

/**
 * Created by HFZ on 2017/6/1.
 */

public class TemperatureTable extends DataSupport {
    private Date recordDate;
    private double temperature;
    private String sensorName;

    public void setRecordDate(Date date){
        recordDate = date;
    }

    public void setTemperature(double temperature){
        this.temperature = temperature;
    }

    public void setSensorName(String sensorName){
        this.sensorName = sensorName;
    }

    public double getTemperature(){
        return temperature;
    }

    public Date getRecordDate(){
        return recordDate;
    }

    public String getSensorName(){
        return sensorName;
    }
}
