package group3.explore;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cp102group3maple.violethsu.maple.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import group3.Common;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap map;
    private FragmentActivity activity;
    private Bundle bundle;
//    locallist class


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bundle=getArguments();
        return inflater.inflate(R.layout.showmap, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        /* the SupportMapFragment is a child fragment of SpotDetailFragment;
        using getChildFragmentManager() instead of getFragmentManager() */
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().
                        findFragmentById(R.id.fmMap);
        mapFragment.getMapAsync(this);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        if (ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        }
        map.getUiSettings().setZoomControlsEnabled(true);
        if (bundle == null) {
            Toast.makeText(activity,"no location",Toast.LENGTH_SHORT).show();
        } else {
            showMap();
        }

    }

    private void showMap() {

        LatLng position = new LatLng(getArguments().getLong("lat"), getArguments().getLong("lon"));
//        show detail
        String snippet = getArguments().getString("district");

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(position)
                .zoom(2)
                .build();
        CameraUpdate cameraUpdate = CameraUpdateFactory
                .newCameraPosition(cameraPosition);
        map.animateCamera(cameraUpdate);

        // ic_add spot on the map
        map.addMarker(new MarkerOptions()
                .position(position)
                .title(getArguments().getString("district"))
                .snippet(snippet));

    }

    @Override
    public void onStart() {
        super.onStart();

    }

}
