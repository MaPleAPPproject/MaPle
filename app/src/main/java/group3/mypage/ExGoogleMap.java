package group3.mypage;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cp102group3maple.violethsu.maple.R;
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
import java.util.List;

import group3.Common;
import group3.Login;
import group3.MainActivity;

import static android.location.LocationManager.*;


public class ExGoogleMap extends FragmentActivity implements OnMapReadyCallback, LocationListener {
    private final static String URL = "http://10.0.2.2:8080/MaPle";
    private final static String TAG = "ExGoogleMap";
    private LocationManager locMg;
    String bestProv;
    private com.google.android.gms.maps.GoogleMap mMap;
    private Marker marker_Taipei;
    private Marker maker_Kaohsiung;
    private LatLng Taipei;
    private LatLng Kaohsiung;
    private Button btback;

    private CommonTask locationListsGetAllTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        initPoints();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        btback = (Button) findViewById(R.id.btback);
        btback.setOnClickListener(listener);

//        if (networkConnected()) {
//            if (Location(PostId, Lan, Lon)) {
//                SharedPreferences preferences = getSharedPreferences(
//                        Common.PREF_FILE, MODE_PRIVATE);
//                preferences.edit().putBoolean("login", true)
//                        .putInt("PostId", PostId)
//                        .putDouble("Lan", Lan)
//                        .putDouble("Lon",Lon).apply();
//                setResult(RESULT_OK);
//                Intent intent = new Intent();
//                intent.setClass(ExGoogleMap.this, MainActivity.class);
//                startActivity(intent);
//                Toast toast = Toast.makeText(ExGoogleMap.this, "歡迎使用MaPle", Toast.LENGTH_SHORT);
//                toast.show();
//                finish();
//            } else {
//                Toast toast = Toast.makeText(ExGoogleMap.this, "帳號或密碼錯誤", Toast.LENGTH_SHORT);
//                toast.show();
//            }
//        } else {
//            Toast toast = Toast.makeText(ExGoogleMap.this, "連線異常請檢查", Toast.LENGTH_SHORT);
//            toast.show();
//        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        getSystemService(LOCATION_SERVICE);
        locMg = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteriar = new Criteria();
        bestProv = locMg.getBestProvider(criteriar, true);
        if ((locMg.isProviderEnabled(LocationManager.GPS_PROVIDER)) || (locMg.isProviderEnabled(LocationManager.NETWORK_PROVIDER))) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locMg.requestLocationUpdates(bestProv, 1000, 1, this);
            }
        } else {
            Toast.makeText(this, "請開啟定位服務", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onMapReady(com.google.android.gms.maps.GoogleMap googleMap) {
        this.mMap = googleMap;
        setUpMap();

    }

//    private void requestPermissions() {
//    if(Build.VERSION.SDK_INT >=23){
//        int hasPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
//        if (hasPermission !=PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},1);
//            return;
//        }
//    }
//        setMyLocation();
//    }

    //    @Override
//    public void onRequestPermissionResult(int requestCode,String[] permissoins,int[] grantResults){
//        if(requestCode == 1){
//            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
//                setMyLocation();
//                else{
//                    Toast.makeText(this,"未取得授權！",Toast.LENGTH_LONG).show();
//                    finish();
//                }
//            }else{
//                super.onRequestPermissionResult(requestCode,permissoins,grantResults);
//            }
//        }
//    }

    private Button.OnClickListener listener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btback: {
                    Intent intent = new Intent();
                    intent.setClass(ExGoogleMap.this, group3.Login.class);
                    startActivity(intent);
                    break;
                }
            }
        }
    };


    private void initPoints() {
        Taipei = new LatLng(25.032969, 121.565418);
        Kaohsiung = new LatLng(22.627278, 120.301435);
    }

    private void setUpMap() {
        if (mMap != null) {
            mMap.getUiSettings().setZoomControlsEnabled(true);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Location mylocation = locMg.getLastKnownLocation(bestProv);
            drawMarker(mylocation);
        }
        addMarkers();
        mMap.setInfoWindowAdapter(new MyInfoWindowAdapter());
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);

    }

    private void addMarkers() {
        marker_Taipei = mMap.addMarker(new MarkerOptions()
                .position(Taipei)
                .title("台北")
                .draggable(false)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.flag))
                .snippet(getString(R.string.marker_snippet_taipei))
                .anchor(0, 1));
        maker_Kaohsiung = mMap.addMarker(new MarkerOptions()
                .position(Kaohsiung)
                .title("高雄")
                .draggable(false)
                .snippet(getString(R.string.marker_snippet_Kaohsiung))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.flag))
                .anchor(0, 1));
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private class MyInfoWindowAdapter implements com.google.android.gms.maps.GoogleMap.InfoWindowAdapter {
        private final View infoWindow;

        MyInfoWindowAdapter() {
            infoWindow = View.inflate(ExGoogleMap.this, R.layout.infowindow, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            int logoId;
            if (marker.equals(marker_Taipei)) {
                logoId = R.drawable.tai;
            } else if (marker.equals(maker_Kaohsiung)) {
                logoId = R.drawable.kao;
            } else {
                logoId = 0;
            }

            ImageView ivLogo = infoWindow.findViewById(R.id.ivLogo);
            ivLogo.setImageResource(logoId);

            String title = marker.getTitle();
            TextView tvTitle = infoWindow.findViewById(R.id.tvTitle);
            tvTitle.setText(title);

            String snippet = marker.getSnippet();
            TextView tvSnippet = infoWindow.findViewById(R.id.tvSnippet);
            tvSnippet.setText(snippet);
            return infoWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }

    }

    private com.google.android.gms.maps.GoogleMap.OnMarkerClickListener onMarkerClickListener = new com.google.android.gms.maps.GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            TextView infowindow;
            return true;
        }
    };

    private void drawMarker(Location location) {
        if (mMap != null) {
            LatLng gps = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.addMarker(new MarkerOptions()
                    .position(gps)
                    .title("現在位置"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(gps, 7));
        }
    }
    //    private boolean networkConnected() {
//        ConnectivityManager conManager =
//                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo = conManager != null ? conManager.getActiveNetworkInfo() : null;
//        return networkInfo != null && networkInfo.isConnected();
//    }
//
//    private void showAllLocation() {
//        if (Common.networkConnected(activity)) {
//            String url = Common.URL + "/LocationListServlet";
//            List<LocationList> locationLists = null;                        //準備容器接資料
//            JsonObject jsonObject = new JsonObject();
//            jsonObject.addProperty("action", "getAll");//約定傳輸資料格式
//            String jsonOut = jsonObject.toString();
//            locationListsGetAllTask = new CommonTask(url, jsonOut);  //一定要設onstop如果不是空值就取消設空值
//            try {
//                //呼叫spotGetAllTask.execute()後到CommonTask裡的doinBackground開始執行
//                String jsonIn = locationListsGetAllTask.execute().get();
//                Type listType = new TypeToken<List<>>() {
//                }.getType();
//                locationLists = new Gson().fromJson(jsonIn, listType);
//            } catch (Exception e) {
//                Log.e(TAG, e.toString());
//            }
//            if (locationLists == null || locationLists.isEmpty()) {
//                Common.showToast(activity, R.string.msg_NoSpotsFound);
//            } else {
//                rvSpots.setAdapter(new SpotsRecyclerViewAdapter(activity, spots));
//            }
//        } else {
//            Common.showToast(activity, R.string.msg_NoNetwork);
//        }
//    }
    @Override
    protected void onPause() {
        super.onPause();
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            locMg.removeUpdates(this);
        }
    }
}
