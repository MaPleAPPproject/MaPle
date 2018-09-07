package group3.mypage;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cp102group3maple.violethsu.maple.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class ExGoogleMap extends FragmentActivity implements OnMapReadyCallback {

    private com.google.android.gms.maps.GoogleMap mMap;
    private Marker marker_Taipei;
    private Marker maker_Kaohsiung;
    private LatLng Taipei;
    private LatLng Kaohsiung;
    private Button btback;

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
    }

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
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(com.google.android.gms.maps.GoogleMap googleMap) {
        this.mMap =googleMap;
        setUpMap();
    }

    private void initPoints() {
        Taipei = new LatLng(25.032969, 121.565418);
        Kaohsiung = new LatLng(22.627278, 120.301435);
    }

    private void setUpMap() {

        mMap.getUiSettings().setZoomControlsEnabled(false);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(Taipei)
                .zoom(7)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        addMarkers();
        mMap.setInfoWindowAdapter(new MyInfoWindowAdapter());

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
    private com.google.android.gms.maps.GoogleMap.OnMarkerClickListener onMarkerClickListener=new com.google.android.gms.maps.GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            TextView infowindow;
            return true;
        }
    };

}
