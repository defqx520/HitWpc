package cn.edu.hit.ftcl.wearablepc.GDMap.mapview;

import com.amap.api.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by defqx on 2017/6/11.
 */

public class OptionWithServer {


    /*向服务器请求其他终端的位置数据*/
    public static Map<String, GpsInfo> getTermMapListFromServer(){
        Map<String, GpsInfo> lists = new HashMap<>();
        //向服务器请求数据
        lists.put("1", new GpsInfo(45.742591, 126.633269, "1"));
        lists.put("2", new GpsInfo(45.741679, 126.631989, "2"));
        lists.put("3", new GpsInfo(45.741255, 126.634944, "3"));
        return lists;
    }
    /*向服务器请求最新的标记信息*/
    public static ArrayList<MyMarker> getMarksFromServer(){
        ArrayList<MyMarker> marks = new ArrayList<>();
        //向服务器请求数据
        marks.add(new MyMarker(45.742109, 126.637406, 0, "1"));
        marks.add(new MyMarker(45.742475, 126.634118, 1, "1"));
        marks.add(new MyMarker(45.743967, 126.632004, 2, "3"));
        return marks;
    }

    /*向服务器发送消息    type=0 表示仅为GPS信息    type=1表示安好     type=2表示支援*/
    public static void sendStateToSever(int type, GpsInfo gpsInfo){
        //向服务器发送
    }

    /**
     * 向服务器发送Marker标绘信息
     * @param UpdateOrAddint  =0表示update   =1表示新增
     * @param markertype       =0表示红旗   =1表示红星    =2表示注意
     * @param markerGpsInfo1    若是新增，表示新增标记坐标；若是更新表示更新后坐标
     * @param markerGpsInfo2    若是更新，表示更新前坐标；否则为null
     */
    public static void sendMarkerInfoToSever(int UpdateOrAddint, int markertype, GpsInfo markerGpsInfo1, GpsInfo markerGpsInfo2){
        System.out.println("已向服务器发送该标绘信息");
        //向服务器发送
    }

}
