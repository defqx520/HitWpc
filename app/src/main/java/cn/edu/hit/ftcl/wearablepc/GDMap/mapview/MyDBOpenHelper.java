package cn.edu.hit.ftcl.wearablepc.GDMap.mapview;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.amap.api.maps.model.Marker;

import java.util.ArrayList;

/**
 * Created by defqx on 2017/5/26.
 */

public class MyDBOpenHelper extends SQLiteOpenHelper {

    private Context mContext;
    private static final String CREATE_MARK = "CREATE TABLE MARKER(" +
            "mid INTEGER PRIMARY KEY AUTOINCREMENT," +
            "longitude DOUBLE," +
            "latitude DOUBLE," +
            "type INTEGER," +
            "bywho VARCHAR(20))";

    public MyDBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MARK);
        Toast.makeText(mContext, "Create DB Success!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public MyMarker insertdata(Marker marker, SQLiteDatabase db, String table_name, int type){
        ContentValues values = new ContentValues();
        values.put("bywho", marker.getTitle());
        values.put("longitude", marker.getPosition().longitude);
        values.put("latitude", marker.getPosition().latitude);
        values.put("type", type);
        long rowid = db.insert(table_name, null, values);

        MyMarker myMarker = new MyMarker();
        myMarker.mid = (int)rowid;
        myMarker.latitude = marker.getPosition().latitude;
        myMarker.longitude = marker.getPosition().longitude;
        myMarker.type = type;
        myMarker.bywho = marker.getTitle();

        Toast.makeText(mContext, "标记保存成功"+rowid, Toast.LENGTH_SHORT).show();
        return myMarker;
    }

    public void updatedata(Marker marker, SQLiteDatabase db, String table_name, int mid){
        ContentValues values = new ContentValues();
        values.put("longitude", marker.getPosition().longitude);
        values.put("latitude", marker.getPosition().latitude);
        db.update(table_name, values, "mid=?", new String[]{String.valueOf(mid)});

        Toast.makeText(mContext, "DB中标记位置已修改", Toast.LENGTH_SHORT).show();
    }

    public void deletedata(SQLiteDatabase db, String table_name, int mid){
        db.delete(table_name, "mid=?", new String[]{String.valueOf(mid)});
        Toast.makeText(mContext, "DB中标记已删除", Toast.LENGTH_SHORT).show();
    }

    public ArrayList<MyMarker> getMarkers(SQLiteDatabase db, String table_name){
        ArrayList<MyMarker> markers = new ArrayList<>();
        Cursor cursor;
        cursor = db.query(table_name, null, null, null, null, null, null);
        if(cursor.getCount()>0){
            cursor.moveToFirst();
            int count=cursor.getCount();
            for(int i=0;i<count;++i){
                cursor.moveToPosition(i);
                MyMarker mark = new MyMarker();
                mark.mid = cursor.getInt(cursor.getColumnIndex("mid"));
                mark.longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
                mark.latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
                mark.bywho = cursor.getString(cursor.getColumnIndex("bywho"));
                mark.type = cursor.getInt(cursor.getColumnIndex("type"));
                markers.add(mark);
            }
        }else{
            markers = null;
        }
        return markers;
    }
}
