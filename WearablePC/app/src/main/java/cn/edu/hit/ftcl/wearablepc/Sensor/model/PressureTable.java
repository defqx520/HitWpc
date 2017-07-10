package cn.edu.hit.ftcl.wearablepc.Sensor.model;

import org.litepal.crud.DataSupport;

import java.util.Date;

/**
 * Created by HFZ on 2017/6/1.
 */

public class PressureTable extends DataSupport{
    private Date recordDate;
    private double pressure;
    private double altitude;     //根据气压算海拔
    private String sensorName;

    public void setRecordDate(Date date){
        recordDate = date;
    }

    public void setPressure(double pressure){
        this.pressure = pressure;
    }

    public void setAltitude(double altitude){
        this.altitude = altitude;
    }

    public void setSensorName(String sensorName){
        this.sensorName = sensorName;
    }

    public double getPressure(){
        return pressure;
    }

    public double getAltitude(){
        return altitude;
    }

    public Date getRecordDate(){
        return recordDate;
    }

    public String getSensorName(){
        return sensorName;
    }
}
