package cn.edu.hit.ftcl.wearablepc.Sensor.model;

import org.litepal.crud.DataSupport;

import java.util.Date;

/**
 * Created by HFZ on 2017/6/1.
 */

public class AlcoholDetectionTable extends DataSupport {
    private Date recordDate;
    private int concentration;//浓度
    private String sensorName;

    public void setRecordDate(Date date){
        recordDate = date;
    }

    public void setConcentration(int concentration){
        this.concentration = concentration;
    }

    public void setSensorName(String sensorName){
        this.sensorName = sensorName;
    }

    public int getConcentration(){
        return concentration;
    }

    public Date getRecordDate(){
        return recordDate;
    }

    public String getSensorName(){
        return sensorName;
    }
}
