package group3.mypage;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.icu.util.Output;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cp102group3maple.violethsu.maple.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import group3.AccountTask;
import group3.Common;
import group3.MainActivity;
import group3.Post;
import group3.explore.PostTask;

public class ExGoogleMap extends FragmentActivity implements OnMapReadyCallback  {
//    private final static String URL = "http://10.0.2.2:8080/MaPle";
    private final static String TAG = "ExGoogleMap";
    String memberId;
    String bestProv;
    private Location location = null;
    private LocationManager locMg;
    private GoogleMap mMap;
    private Button btback;
    private LocationList newlocationList;
    private LocaImageTask locaImageTask;
    private LocationTask LocationGetAllTask;
    private TextView tvpostId;
    static List<LocationList> locationList=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        SharedPreferences pref = getSharedPreferences(Common.PREF_FILE,
                MODE_PRIVATE);
        memberId = pref.getString("MemberId", "");

        btback = (Button) findViewById(R.id.btback);
        btback.setOnClickListener(listener);

    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.mMap = map;
        setUpMap();
        showAllLocations();
    }

    private void setUpMap() {
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            mMap.setMyLocationEnabled(true);
        }
        else{
            ActivityCompat.requestPermissions(ExGoogleMap.this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);
        }
//        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    private void showAllLocations() {
        if (Common.networkConnected(this)) {
            String url = Common.URL + "/spotServlet";
            //準備容器接資料
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findById");//約定傳輸資料格式
            jsonObject.addProperty("MemberId", memberId);
            String jsonOut = jsonObject.toString();
            LocationGetAllTask = new LocationTask(url, jsonOut);  //一定要設onstop如果不是空值就取消設空值
            try {
                String jsonIn = LocationGetAllTask.execute().get();
                Type listType = new TypeToken<List<LocationList>>() {
                }.getType();
                locationList = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (locationList == null || locationList.isEmpty()) {
                Toast.makeText(this,"無貼文紀錄", Toast.LENGTH_LONG);
            } else {
                for(int i = 0;i < locationList.size();i++){
                    newlocationList = locationList.get(i);
                    showMap(newlocationList);
                }
            }
        } else {
            Toast.makeText(this,"網路連線異常", Toast.LENGTH_LONG);
        }
    }

    private Button.OnClickListener listener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btback: {
                    Intent intent = new Intent();
                    intent.setClass(ExGoogleMap.this, MainActivity.class);
                    startActivity(intent);
                    break;
                }
            }
        }
    };


    private void showMap(LocationList locationList) {
        LatLng position = new LatLng(locationList.getLat(), locationList.getLon());
        String snippet = locationList.getDistrict();
        int posid = locationList.getPostId();

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(position)
                .zoom(3)
                .build();
        CameraUpdate cameraUpdate = CameraUpdateFactory
                .newCameraPosition(cameraPosition);
        mMap.animateCamera(cameraUpdate);

        Marker marker=mMap.addMarker(new MarkerOptions()
                .position(position)
                .draggable(false)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.flag))
                .snippet(snippet)
                .anchor(0, 1));
        marker.setTag(posid);

        mMap.setInfoWindowAdapter(new MyInfoWindowAdapter(this, locationList));
    }

    private class MyInfoWindowAdapter  implements GoogleMap.InfoWindowAdapter {
        private final View infoWindow;
        private int imageSize;
        int postid;

        MyInfoWindowAdapter(ExGoogleMap exGoogleMap, LocationList locationList) {
            infoWindow = View.inflate(ExGoogleMap.this, R.layout.infowindow, null);
            imageSize = getResources().getDisplayMetrics().widthPixels / 4;
            postid=locationList.getPostId();
        }

        @Override
        public View getInfoWindow(Marker marker) {
            ImageView imageView = infoWindow.findViewById(R.id.ivLogo);
            String url = Common.URL + "/spotServlet";

            postid= (int) marker.getTag();
            Bitmap bitmap = null;
            try {
                //有task記得要關閉
                locaImageTask = new LocaImageTask(url, postid, imageSize);
                // passing null and calling get() means not to run FindImageByIdTask.onPostExecute()
                //不寫get()的話第一次點擊不會有圖片第二次才會有,因為主執行緒沒有等待繼續往下run,而第二次點擊出現的圖片是第一次點擊的圖片
                bitmap = locaImageTask.execute().get();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                imageView.setImageResource(R.drawable.logo);
            }

//            imageView .setImageResource(marker.get);

            TextView tvSnippet = infoWindow.findViewById(R.id.tvSnippet);
            tvSnippet.setText(marker.getSnippet());
            return infoWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (locaImageTask != null) {
            locaImageTask.cancel(true);
            locaImageTask = null;
        }
    }
}
