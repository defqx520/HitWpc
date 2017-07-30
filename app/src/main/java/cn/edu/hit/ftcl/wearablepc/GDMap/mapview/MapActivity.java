package cn.edu.hit.ftcl.wearablepc.GDMap.mapview;

import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import cn.edu.hit.ftcl.wearablepc.Network.NetworkUtil;
import cn.edu.hit.ftcl.wearablepc.R;
import cn.edu.hit.ftcl.wearablepc.Communication.Parameter;


public class MapActivity extends AppCompatActivity implements AMapLocationListener {

    private double myLatitude = -1, myLongitude = -1;

    private Map<String, GpsInfo> TermMapLists = new HashMap<>();    //其他终端设备位置列表
    private ArrayList<MyMarker> Marks = new ArrayList<>();

    private Button tellSafe, askHelp;

    /*标绘的图标*/
    private ImageView redflag, redstart, takecare;

    /*显示地图需要的变量*/
    private MapView mapView = null;    //地图控件
    private AMap aMap = null;          //地图对象

    /*定位需要的声明*/
    public AMapLocationClient mLocationClient = null;         //定位发起端
    public AMapLocationClientOption mLocationOption = null;   //定位参数
    public AMapLocationListener mLocationListener = null;     //定位监听器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_main);


        try {
            InitMap(savedInstanceState);      //初始化地图 包括定位
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /*==============================定义Marker拖拽的监听============================*/
        AMap.OnMarkerDragListener markerDragListener = new AMap.OnMarkerDragListener() {
            // 当marker开始被拖动时回调此方法, 这个marker的位置可以通过getPosition()方法返回。
            // 这个位置可能与拖动的之前的marker位置不一样。
            // marker 被拖动的marker对象。
            double startLatitude;
            double startLongitude;
            @Override
            public void onMarkerDragStart(Marker arg0) {
                // TODO Auto-generated method stub
                startLatitude = arg0.getPosition().latitude;
                startLongitude = arg0.getPosition().longitude;

            }
            // 在marker拖动完成后回调此方法, 这个marker的位置可以通过getPosition()方法返回。
            // 这个位置可能与拖动的之前的marker位置不一样。
            // marker 被拖动的marker对象。
            @Override
            public void onMarkerDragEnd(Marker arg0) {
                // TODO Auto-generated method stub
                double latitude = arg0.getPosition().latitude;  //纬度
                double longitude = arg0.getPosition().longitude;  //经度
                arg0.setSnippet(""+latitude+","+longitude);
                int temp = Integer.valueOf(arg0.getObject().toString());
                Log.d("tag",temp+"");
                OptionWithServer.sendMarkerInfoToSever(0,temp, new GpsInfo(arg0.getPosition().latitude, arg0.getPosition().longitude), new GpsInfo(startLatitude, startLongitude));
            }
            // 在marker拖动过程中回调此方法, 这个marker的位置可以通过getPosition()方法返回。
            // 这个位置可能与拖动的之前的marker位置不一样。
            // marker 被拖动的marker对象。
            @Override
            public void onMarkerDrag(Marker arg0) {
                // TODO Auto-generated method stub
                double latitude = arg0.getPosition().latitude;  //纬度
                double longitude = arg0.getPosition().longitude;  //经度
                arg0.setSnippet(""+latitude+","+longitude);
            }
        };
        aMap.setOnMarkerDragListener(markerDragListener);   // 绑定marker拖拽事件

        /*==============================定义 Marker 点击事件监听============================*/
        AMap.OnMarkerClickListener markerClickListener = new AMap.OnMarkerClickListener() {
            // marker 对象被点击时回调的接口
            // 返回 true 则表示接口已响应事件，否则返回false
            @Override
            public boolean onMarkerClick(Marker marker) {
                return false;
            }
        };
        aMap.setOnMarkerClickListener(markerClickListener);  // 绑定 Marker被点击事件

        /*==============================定义信息窗口点击事件监听============================*/
        AMap.OnInfoWindowClickListener listener = new AMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker arg0) {
                arg0.hideInfoWindow();
            }
        };
        aMap.setOnInfoWindowClickListener(listener);    //绑定信息窗点击事件


        /*============================标绘功能============================*/
        redflag = (ImageView) findViewById(R.id.redflag);
        redstart = (ImageView)findViewById(R.id.redstart);
        takecare = (ImageView)findViewById(R.id.takecare) ;
        redflag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Marker marker = PaintMarker(0, "终端1", aMap.getCameraPosition().target.latitude, aMap.getCameraPosition().target.longitude);
                OptionWithServer.sendMarkerInfoToSever(1, 0, new GpsInfo(marker.getPosition().latitude, marker.getPosition().longitude), null);
            }
        });
        redstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Marker marker = PaintMarker(1, "终端1", aMap.getCameraPosition().target.latitude, aMap.getCameraPosition().target.longitude);
                OptionWithServer.sendMarkerInfoToSever(1, 1, new GpsInfo(marker.getPosition().latitude, marker.getPosition().longitude), null);
            }
        });
        takecare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Marker marker = PaintMarker(2, "终端1", aMap.getCameraPosition().target.latitude, aMap.getCameraPosition().target.longitude);
                OptionWithServer.sendMarkerInfoToSever(1, 2, new GpsInfo(marker.getPosition().latitude, marker.getPosition().longitude), null);
            }
        });

        /*=============================一键保平安，请求增援============================*/
        tellSafe = (Button) findViewById(R.id.safe);
        askHelp = (Button) findViewById(R.id.askhelp);
        tellSafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OptionWithServer.sendStateToSever(1, new GpsInfo(myLatitude, myLongitude, "1"));
                Toast.makeText(getApplicationContext(), "发送成功", Toast.LENGTH_SHORT).show();
            }
        });
        askHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OptionWithServer.sendStateToSever(2, new GpsInfo(myLatitude, myLongitude, "1"));
                Toast.makeText(getApplicationContext(), "请求成功", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void InitMap(Bundle savedInstanceState) throws InterruptedException {
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        if(aMap == null){
            aMap = mapView.getMap();
        }

        aMap.moveCamera(CameraUpdateFactory.zoomTo(17));   //初始化缩放级别  范围3-19

        /*UiSetting 对地图上的控件进行管理*/
        UiSettings settings = aMap.getUiSettings();
        settings.setMyLocationButtonEnabled(true);   //显示定位控件
        settings.setScaleControlsEnabled(true);   //显示缩放控件
        settings.setCompassEnabled(true);    //显示指南针控件
        aMap.getUiSettings().setMyLocationButtonEnabled(true);   //设置默认定位按钮显示
        /*定义定位图标样式*/
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER);
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔

        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setMyLocationEnabled(true);   //显示定位蓝点

        mLocationClient = new AMapLocationClient(getApplicationContext());      //初始化定位
        mLocationClient.setLocationListener(mLocationListener);               //设置定位回调监听
        mLocationOption = new AMapLocationClientOption();                       //初始化定位参数
//        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Device_Sensors);
        mLocationOption.setOnceLocation(false);                                //设置连续定位
        mLocationClient.setLocationOption(mLocationOption);                   //给定位客户端对象设置定位参数
        mLocationClient.startLocation();                                        //启动定位

//        aMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(myLatitude, myLongitude)));

        Connector.getDatabase();  //创建数据库

        InitTerminals();   //初始化所有终端
        InitMarkers();       //初始化标绘

    }

    /*向服务器请求其他终端位置，并更新*/
    public void InitTerminals(){
//        TermMapLists = OptionWithServer.getTermMapListFromServer();
//        for(Map.Entry<String, GpsInfo> entry : TermMapLists.entrySet()){
//            LatLng latLng = new LatLng(entry.getValue().getLatitude(), entry.getValue().getlongitude());
//            System.out.println(latLng);
//            final Marker marker = aMap.addMarker(new MarkerOptions().position(latLng).title(entry.getKey()).snippet(Double.toString(latLng.latitude)+","+Double.toString(latLng.longitude)));
//            marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.user_soldier)));
//            marker.setDraggable(false);
//        }
        //从数据库中读取参数
        List<Parameter> targetIPList = DataSupport.where("name = ?", "target_ip").find(Parameter.class);
        String targetIP = targetIPList.get(0).getValue();//192.168.1.133

        List<Parameter> targetPortList = DataSupport.where("name = ?", "target_file_port").find(Parameter.class);
        String targetPort = targetPortList.get(0).getValue();//29999
        System.out.println("****************"+targetIP + "  "+targetPort);
        NetworkUtil.receiveGpsInfo(targetIP, Integer.parseInt(targetPort));

        List<GpsInfo> mGpsDatas = new ArrayList<>();
        mGpsDatas = DataSupport.findAll(GpsInfo.class);
        System.out.println("@@@@@@@@@@@@@@@大小"+mGpsDatas.size());
        for(int i=0; i<mGpsDatas.size(); i++){
            GpsInfo temp = mGpsDatas.get(i);
            LatLng latLng = new LatLng(temp.getLatitude(), temp.getlongitude());
            final Marker marker = aMap.addMarker(new MarkerOptions().position(latLng).title(temp.getuID()).snippet(Double.toString(latLng.latitude)+","+Double.toString(latLng.longitude)));
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.user_soldier)));
            marker.setDraggable(false);
        }

        Toast.makeText(getApplicationContext(), "终端初始化完毕", Toast.LENGTH_SHORT).show();
    }
    /*向服务器请求标绘数据，并更新*/
    public void InitMarkers(){
        Marks = OptionWithServer.getMarksFromServer();
        if(Marks!=null){
            for(int i=0; i<Marks.size(); i++){
                MyMarker myMark = Marks.get(i);
                Marker marker = PaintMarker(myMark.type, myMark.bywho,myMark.latitude, myMark.longitude);
            }
        }
    }


    /*定位的回调函数*/
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //可在其中解析amapLocation获取相应内容。
                myLatitude = amapLocation.getLatitude();
                myLongitude = amapLocation.getLongitude();
                Log.i("纬度,经度:", ""+amapLocation.getLatitude()+" "+amapLocation.getLongitude());
                Toast.makeText(getApplicationContext(), "纬度"+amapLocation.getLatitude()+",经度"+amapLocation.getLongitude(), Toast.LENGTH_SHORT).show();
            }else {
                //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                Log.e("AmapError","location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mapView.onSaveInstanceState(outState);
    }

    /*绑定键盘快捷键*/
    @Override
    public boolean dispatchKeyEvent(KeyEvent event){
        int SCROLL_BY_PX = 100; //移动一次的像素点
        if(event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP){
            aMap.moveCamera(CameraUpdateFactory.scrollBy(0, SCROLL_BY_PX));    //向上键——向上移动
        } else if(event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN){
            aMap.moveCamera(CameraUpdateFactory.scrollBy(0, -SCROLL_BY_PX));
        } else if(event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT){
            aMap.moveCamera(CameraUpdateFactory.scrollBy(SCROLL_BY_PX, 0));
        } else if(event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT){
            aMap.moveCamera(CameraUpdateFactory.scrollBy(-SCROLL_BY_PX, 0));
        } else if(event.getKeyCode() == KeyEvent.KEYCODE_NUMPAD_1){
            aMap.moveCamera(CameraUpdateFactory.zoomIn());            //数字键盘1——放大
        }else if(event.getKeyCode() == KeyEvent.KEYCODE_NUMPAD_0){
            aMap.moveCamera(CameraUpdateFactory.zoomOut());
        }
        return super.dispatchKeyEvent(event);
    }

    /*====================================在地图上绘制标记===================================================*/
    private Marker PaintMarker(int type, String user, double latitude, double longitude) {
        MarkerOptions markerOption = new MarkerOptions();

        markerOption.position(new LatLng(latitude, longitude));
        markerOption.title(user).snippet(latitude+ ","+longitude);

        markerOption.draggable(true);//设置Marker可拖动
        if(type==0)
            markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                    .decodeResource(getResources(),R.drawable.redflag)));
        else if(type==1)
            markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                    .decodeResource(getResources(),R.drawable.redstart)));
        else if(type==2)
            markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                    .decodeResource(getResources(),R.drawable.takecare)));
        markerOption.setFlat(false);//设置marker平贴地图效果
        Marker marker = aMap.addMarker(markerOption);
        marker.setObject(""+type);

        return marker;
    }
}
