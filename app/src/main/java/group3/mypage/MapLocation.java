package group3.mypage;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cp102group3maple.violethsu.maple.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;



import java.io.IOException;
import java.util.List;
import java.util.Locale;

import group3.Common;


public class MapLocation extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {
    private final static String TAG = "MapLocationActivity";
    private static final int REQUEST_GET_LOCATION = 4;
    private static final int RESULT_OK = 0;
    private GoogleMap map;
    private double latitude, longitude;
    private String addressLine;
    private String adminArea;
    private String countryCode;
    private String countryName;
    private String district;
    private Marker marker;
    private AutoCompleteTextView actLocationName;

    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-40, -168), new LatLng(71, 136));
    private ImageButton ibMyLocation;
    private LocationManager locMg;
    private FusedLocationProviderClient fusedLocationProviderclient;
    private boolean locationPermissionGranted = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_locaiton);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        handleViews();
        fusedLocationProviderclient = LocationServices.getFusedLocationProviderClient(this);


    }

    public void handleViews() {
        actLocationName = findViewById(R.id.actLocationName);
        ibMyLocation = findViewById(R.id.ibMyLocation);


    }

    private void getLastKnownLocation() {
        Log.e("getLastKnownLocation", "called");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderclient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        Location location = task.getResult();
                        Log.d("onComplete", "location.getLatitude()" + location.getLatitude());
                        Log.d("onComplete", "location.getLongitude()" + location.getLongitude());

                    }

                }
            });
            return;
        }

    }


    public LatLng toMyCurrentLocation() {

        if((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                && (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {

            locMg = (LocationManager) getSystemService(LOCATION_SERVICE);
            Location mylocation = locMg.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            latitude = (double)mylocation.getLatitude();
            longitude = (double)mylocation.getLongitude();
            Log.d(TAG,"test" + latitude + "," + longitude);
            LatLng coordinate = new LatLng(latitude,longitude);
            return coordinate;

        } else {
            ActivityCompat.requestPermissions(MapLocation.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return null;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        Common.askPermissions(this, permissions, Common.REQ_ACCESS_FINE_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case Common.REQ_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                    Toast.makeText(this, "請開啟定位權限，已取得您的當前位置。", Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
        }
    }

    private void hideSoftKeyboard(Context context, EditText editText) {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        setUpMap();
        setMapClick(map);

    }


    private void setUpMap() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(false);
        }
        map.getUiSettings().setZoomControlsEnabled(true);


    }

    //輸入地址後按confirm 地圖畫面移到到輸入的地址
    public void onConfirmClick(View view) {

        String locationName = actLocationName.getText().toString().trim();
        if (locationName.length() > 0) {
            locationNameToMarker(locationName);

        } else {
            showToast("Location Name is not found");
        }
    }

    private void locationNameToMarker(String locationName) {

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
            addressLine = address.getAddressLine(0);
            Log.i(TAG, "address:" + addressLine);

            moveCameraPosition(position);
            convertToPositionData(addressList);

            if (adminArea == null) {
                district = addressLine;
            } else {
                district = countryName + "," + adminArea;
            }

            showMarker(position, countryName, district);






        }
    }

    private void moveCameraPosition(LatLng position) {
        map.clear();
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(position).zoom(15).build();
        map.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
        hideSoftKeyboard(MapLocation.this, actLocationName);

    }

    private void setMapClick(final GoogleMap map) {
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                moveCameraPosition(latLng);
                if (marker != null) {
                    marker.remove();
                }

                Geocoder gc = new Geocoder(MapLocation.this, Locale.TRADITIONAL_CHINESE);
                latitude = latLng.latitude;
                longitude = latLng.longitude;
                List<Address> lstAddress = null;
                try {
                    lstAddress = gc.getFromLocation(latitude, longitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                convertToPositionData(lstAddress);
                addressLine = lstAddress.get(0).getAddressLine(0);

                if (adminArea == null) {
                    district = addressLine;
                } else {
                    district = countryName + "," + adminArea;
                }

                showMarker(latLng, countryName, district);

            }
        });


        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                wrapToIntent();

            }
        });

    }

    public void convertToPositionData(List<Address> lstAddress) {
        countryName = lstAddress.get(0).getCountryName();
        addressLine = lstAddress.get(0).getAddressLine(0);
        countryCode = lstAddress.get(0).getCountryCode();
        adminArea = lstAddress.get(0).getAdminArea();//縣市
        district = lstAddress.get(0).getSubAdminArea();
    }


    public void showMarker(LatLng latLng, String countryName, String addressLine) {
        if ( marker != null ) {
            marker.remove();
        }
        marker = map.addMarker(new MarkerOptions()
                .position(latLng)
                .title(countryName)
                .snippet(addressLine)
        );

        GoogleMapInfoWindow adapter = new GoogleMapInfoWindow(MapLocation.this);
        map.setInfoWindowAdapter(adapter);
        marker.showInfoWindow();
    }


    public void wrapToIntent() {

        getIntent().putExtra("latitude", latitude);
        getIntent().putExtra("longitude", longitude);
        getIntent().putExtra("address", addressLine);
        getIntent().putExtra("adminArea", adminArea);
        getIntent().putExtra("countryCode", countryCode);
        getIntent().putExtra("countryName", countryName);
        getIntent().putExtra("district", district);
        setResult(RESULT_OK, getIntent());
        finish();

    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void onMyLocationClick(View view) {

        LatLng myLocation = toMyCurrentLocation();
        if (myLocation == null) {
            Log.e("onMyLocationClick", "myLocation is null");

            return;

        } else {
            moveCameraPosition(myLocation);
            List<Address> lstAddress = null;
            Geocoder gc = new Geocoder(MapLocation.this, Locale.TRADITIONAL_CHINESE);
            try {
                lstAddress = gc.getFromLocation(latitude, longitude, 1);
            } catch (IOException e) {
                Log.e("onMyLocationClick", "Fail to convert position to lstAddress ");
            }

            convertToPositionData(lstAddress);
            if (adminArea == null) {
                district = addressLine;
            } else {
                district = countryName + "," + adminArea;
            }

            showMarker(myLocation, countryName, district);


        }

    }




    class GoogleMapInfoWindow implements GoogleMap.InfoWindowAdapter {


        private View window;
        private Context context;

        public GoogleMapInfoWindow(Context context) {
            this.context = context;
            this.window = LayoutInflater.from(context).inflate(R.layout.info_window, null);
        }

        private void rendowWindowText(Marker marker, View window) {
            String title = marker.getTitle();
            TextView tvTitle = (TextView) window.findViewById(R.id.tvInfoWindowTitle);

            if (!title.equals("")) {

                tvTitle.setText(title);
            } else {
                Log.d("MapLocation", "title =" + title);
            }

            String snippet = String.valueOf(marker.getSnippet());
            TextView tvSnippet = (TextView) window.findViewById(R.id.tvInfoWindowSnippet);

            if (!snippet.equals("")) {

                tvSnippet.setText(snippet);
            }
        }

        @Override
        public View getInfoWindow(Marker marker) {
            rendowWindowText(marker, window);
            return window;
        }

        @Override
        public View getInfoContents(Marker marker) {
            rendowWindowText(marker, window);
            return window;
        }
    }
}