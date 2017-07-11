package cn.edu.hit.ftcl.wearablepc.GDMap.mapview;

/**
 * Created by defqx on 2017/5/26.
 */

public class MyMarker {
    public int mid;  //数据库中唯一标识
    public double longitude;  //经度
    public double latitude;   //纬度
    public int type;           //标记类型
    public String bywho;       //由哪个单兵做的标记

    public MyMarker(double latitude, double longitude, int type, String bywho){
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
        this.bywho = bywho;
    }

    public  MyMarker(){

    }
}
