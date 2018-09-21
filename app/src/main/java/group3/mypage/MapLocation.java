package group3.mypage;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.cp102group3maple.violethsu.maple.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapLocation extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap map;
    private final static String TAG = "MapLocationActivity";
    private double latitude,longitude;
    private String snippet;
    private String adminArea;
    private String countryCode;
    private String countryName;
    private String district;
    private static final int REQUEST_GET_LOCATION = 4;
    private static final int RESULT_OK=0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_locaiton);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        setUpMap();
        setMapLongClick(map);
    }


    private void setUpMap() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        }
        map.getUiSettings().setZoomControlsEnabled(true);

    }
    //輸入地址後按confirm 地圖到輸入的地址
    public void onConfirmClick(View view) {
        EditText etLocationName = findViewById(R.id.etLocationName);
        String locationName = etLocationName.getText().toString().trim();
        if (locationName.length() > 0) {
            locationNameToMarker(locationName);

        } else {
            showToast("Location Name is not found");
        }
    }
    private void locationNameToMarker(String locationName) {
        map.clear();
        Geocoder geocoder = new Geocoder(this);
        List<Address> addressList = null;
        int maxResults = 1;
        try {
            addressList = geocoder
                    .getFromLocationName(locationName, maxResults);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }

        if (addressList == null || addressList.isEmpty()) {
            showToast("Location not found");
        } else {
            Address address = addressList.get(0);

            LatLng position = new LatLng(address.getLatitude(),
                    address.getLongitude());

            latitude = address.getLatitude();
            longitude = address.getLongitude();

            snippet = address.getAddressLine(0);
            Log.i(TAG, "address:"+snippet);

            map.addMarker(new MarkerOptions().position(position)
                    .title(locationName).snippet(snippet));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(position).zoom(15).build();
            map.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));

            adminArea = address.getAdminArea();
            countryCode = address.getCountryCode();
            countryName = address.getCountryName();
            district = address.getSubAdminArea();






        }
    }

    private void setMapLongClick(final GoogleMap map) {
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                map.addMarker(new MarkerOptions().position(latLng));
                String snippet = String.format(Locale.getDefault(),
                        "Lat: %1$.5f, Long: %2$.5f",
                        latLng.latitude,
                        latLng.longitude);

                map.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("dropped_pin")
                        .snippet(snippet));

            }
        });
    }


    private void showToast(String msg) {
        Toast.makeText(this,msg, Toast.LENGTH_SHORT).show();
    }

    public void onBackToNewPostClick(View view) {

//        Bundle bundle = new Bundle();
//        bundle.putDouble("latitude", latitude);
//        bundle.putDouble("longitude", longitude);
//        bundle.putString("address", snippet);
//        bundle.putString("adminArea", adminArea);
//        bundle.putString("countryCode", countryCode);
//        bundle.putString("countryName", countryName);
//        bundle.putString("district", district);
//        Intent locationIntent  = new Intent(this,NewPost_Activity.class);
//        locationIntent.putExtra("result", "我在測試送回去的結果");
        getIntent().putExtra("latitude", latitude);
        getIntent().putExtra("longitude", longitude);
        getIntent().putExtra("address", snippet);
        getIntent().putExtra("adminArea", adminArea);
        getIntent().putExtra("countryCode", countryCode);
        getIntent().putExtra("countryName", countryName);
        getIntent().putExtra("district", district);
        setResult(RESULT_OK,getIntent());
        finish();
//        locationIntent.putExtras(bundle);
//        startActivity(locationIntent);
    }


//    public  void onPoiClick (PointOfInterest poi){
//
//
//        map.addMarker(new MarkerOptions()
//                .position()
//                .title("dropped_pin")
//                .snippet(snippet));
//
//    }
}
