package cn.edu.hit.ftcl.wearablepc.GDMap.mapview;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by defqx on 2017/6/21.
 */

public class GPSInfoList implements Serializable{
    private ArrayList<GpsInfo> gpslist;

    public ArrayList<GpsInfo> getGpslist(){
        return gpslist;
    }

    public void setGpslist(ArrayList<GpsInfo> gpslist){
        this.gpslist = gpslist;
    }
}
