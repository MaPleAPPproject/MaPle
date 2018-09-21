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
    private Object locallist;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        spot = (Spot) (getArguments() != null ? getArguments().getSerializable("spot") : null);
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
        this.map = map;
        if (ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        }
        map.getUiSettings().setZoomControlsEnabled(true);
        if (locallist == null) {
            Toast.makeText(activity,"no location",Toast.LENGTH_SHORT).show();
        } else {
            showMap(locallist);
        }

    }

    private void showMap(Object locallist) {

//        LatLng position = new LatLng(locallist.getLatitude(), locallist.getLongitude());
//        show detail 可以不要
//        String snippet = getString(R.string.col_Name) + ": " + spot.getName() + "\n" +
//                getString(R.string.col_PhoneNo) + ": " + spot.getPhoneNo() + "\n" +
//                getString(R.string.col_Address) + ": " + spot.getAddress();

        // focus on the spot
//        CameraPosition cameraPosition = new CameraPosition.Builder()
//                .target(position)
//                .zoom(9)
//                .build();
//        CameraUpdate cameraUpdate = CameraUpdateFactory
//                .newCameraPosition(cameraPosition);
//        map.animateCamera(cameraUpdate);
//
//        // ic_add spot on the map
//        map.addMarker(new MarkerOptions()
//                .position(position)
//                .title(locallist.getName())
//                .snippet(snippet));
//
//        map.setInfoWindowAdapter(new MyInfoWindowAdapter(activity, spot));
//    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        findlocation();
//    }
//
//// 用postid抓經緯度
//    private void findlocation() {
//
//    }
    }
}
