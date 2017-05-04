package com.robin.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.MyLocationStyle;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static com.robin.myapplication.R.id.map;

public class MainActivity extends AppCompatActivity implements
        ActivityCompat.OnRequestPermissionsResultCallback,
        EasyPermissions.PermissionCallbacks {
    private static final String TAG = "MainActivity";
    MapView mMapView;
    AMap aMap;
    MyLocationStyle myLocationStyle;
    UiSettings uiSettings;
    Bundle savedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMapView = (MapView) findViewById(map);
        this.savedInstanceState = savedInstanceState;
        checkPermissions1(needPermissions);
    }

    /**
     * 需要进行检测的权限数组
     */
    protected String[] needPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };
    private static final int PERMISSON_REQUESTCODE = 0;

    private void checkPermissions1(String[] strings) {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED
                ){
            Toast.makeText(MainActivity.this, "没获通过申请,需要申请权限", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, strings, PERMISSON_REQUESTCODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Toast.makeText(this,"permissions=="+permissions+"--grantResults"+grantResults,Toast.LENGTH_LONG).show();
        if (requestCode == PERMISSON_REQUESTCODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "回调，成功", Toast.LENGTH_SHORT).show();
                initmap(savedInstanceState);
            } else {
                // Permission Denied
                Toast.makeText(MainActivity.this, "回调，失败", Toast.LENGTH_SHORT).show();
                new AppSettingsDialog.Builder(this).build().show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    
    @AfterPermissionGranted(PERMISSON_REQUESTCODE)
    private void checkPermissions(String[] strings, Bundle savedInstanceState) {
        if (EasyPermissions.hasPermissions(this, strings)) {
            Toast.makeText(this, "已获取权限", Toast.LENGTH_LONG).show();
            initmap(savedInstanceState);
        } else {
            Toast.makeText(this, "没有获取权限，需要请求", Toast.LENGTH_LONG).show();
            EasyPermissions.requestPermissions(this, "必要的权限", PERMISSON_REQUESTCODE, strings);
        }
    }

    private void initmap(Bundle savedInstanceState) {


        mMapView.onCreate(savedInstanceState);// 此方法必须重写
        if (aMap == null) {
            aMap = mMapView.getMap();
        }

        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类
//        BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher);
//        myLocationStyle.myLocationIcon(descriptor);
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(20000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.anchor(0.5f, 0.5f);//第一个值是X轴的偏移量，以anchor方法的默认值（现在是0.5F）为基准，大于0.5向左，反之向右；第二个值是Y轴偏移量，以anchor方法的默认值（现在是0.5F），大于0.5向上，反之向下。
//        myLocationStyle.radiusFillColor(R.color.colortrans);
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        uiSettings = aMap.getUiSettings();
//        uiSettings.setZoomPosition(600);
        uiSettings.setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        uiSettings.setScaleControlsEnabled(true);
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap.showIndoorMap(true);
        CameraUpdate mCameraUpdate = CameraUpdateFactory.zoomTo(17);

        aMap.moveCamera(mCameraUpdate);
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        //把申请权限的回调交由EasyPermissions处理  
//        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
//    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Toast.makeText(this, "获取成功的权限" + perms.get(0), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Toast.makeText(this, "获取失败的权限" + perms.get(0), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

}
