package cn.edu.hit.ftcl.wearablepc.GDMap.mapview;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by defqx on 2017/5/18.
 */

public class GpsInfo extends DataSupport implements Serializable{
    private double latitude;   //经度
    private double longitude;   //纬度
    private String uID;  //用户标识

    public GpsInfo(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
        this.uID = "默认终端";
    }

    public GpsInfo(double latitude, double longitude, String uID){
        this.latitude = latitude;
        this.longitude = longitude;
        this.uID = uID;
    }
    public double getLatitude(){
        return latitude;
    }

    public double getlongitude() {
        return longitude;
    }

    public String getuID() {
        return uID;
    }

    public void setlatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }
}
