package group3.mypage;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.List;

import group3.Common;

import static android.content.Context.MODE_PRIVATE;

public class Mymap_fragment extends Fragment implements OnMapReadyCallback,LocationListener {

    private FragmentActivity activity;
    private Bundle bundle;
    private final static String TAG = "Mymap_fragment";
    String memberId;
    String bestProv;
    private Location mylocation = null;
    private LocationManager locMg;
    private GoogleMap mMap;
    private LocationList newlocationList;
    private LocaImageTask locaImageTask;
    private LocationTask LocationGetAllTask;
    private RecyclerView mymapgorecyclerView;
    static List<LocationList> locationList = null;
    private ImageView chat;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        SharedPreferences pref = getActivity().getSharedPreferences(Common.PREF_FILE,
                MODE_PRIVATE);
        memberId = pref.getString("MemberId", "");

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bundle=getArguments();
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.activity_mymap_fragment, container, false);

    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        /* the SupportMapFragment is a child fragment of SpotDetailFragment;
        using getChildFragmentManager() instead of getFragmentManager() */
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().
                        findFragmentById(R.id.testmap);
        mapFragment.getMapAsync(this);

        mymapgorecyclerView = activity.findViewById(R.id.mymapgorecyclerView);
        mymapgorecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(1,
                        StaggeredGridLayoutManager.HORIZONTAL));

    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.mMap = map;
        setUpMap();
        showAllLocations();
    }

    private void setUpMap() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                mMap.setMyLocationEnabled(true);
//                locMg = (LocationManager) getSystemService(LOCATION_SERVICE);
//            mylocation = locMg.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//            double lat=(int)mylocation.getLatitude();
//            double lon=(int)mylocation.getLongitude();
//                Log.d(TAG,"test"+lon+","+lat);
            }
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

//        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                int postid = (int) marker.getTag();
                Intent intent = new Intent(activity, Mypage_SinglePost_Activity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("postId",postid );
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void showAllLocations() {
        if (Common.networkConnected(activity)) {
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
                Toast.makeText(activity, "無貼文紀錄", Toast.LENGTH_LONG);
            } else {
                for (int i = 0; i < locationList.size(); i++) {
                    newlocationList = locationList.get(i);
                    showMap(newlocationList);
                }
                mymapgorecyclerView.setAdapter(new Mymap_fragment.locationAdapter(activity, locationList));
                mymapgorecyclerView.setOnFlingListener(null);
                PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
                pagerSnapHelper.attachToRecyclerView(mymapgorecyclerView);
            }
        } else {
            Toast.makeText(activity, "網路連線異常", Toast.LENGTH_LONG);
        }
    }

    private void showMap(LocationList locationList) {
        LatLng position = new LatLng(locationList.getLat(), locationList.getLon());
        String snippet = locationList.getDistrict();
        int postid = locationList.getPostId();

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(position)
                .zoom(3)
                .build();
        CameraUpdate cameraUpdate = CameraUpdateFactory
                .newCameraPosition(cameraPosition);
        mMap.animateCamera(cameraUpdate);

        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(position)
                .draggable(false)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.flag))
                .snippet(snippet)
                .anchor(0, 1));
        marker.setTag(postid);
        mMap.setInfoWindowAdapter(new Mymap_fragment.MyInfoWindowAdapter(activity, locationList));
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


    private class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        private final View infoWindow;
        private int imageSize;
        private ImageView imageView;
        int postid;

        MyInfoWindowAdapter(Activity activity, LocationList locationList) {
            infoWindow = View.inflate(activity, R.layout.infowindow, null);
            imageSize = getResources().getDisplayMetrics().widthPixels / 4;
            postid = locationList.getPostId();

        }

        @Override
        public View getInfoWindow(Marker marker) {
            imageView = infoWindow.findViewById(R.id.ivLogo);
            String url = Common.URL + "/spotServlet";
            postid = (int) marker.getTag();
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

            TextView tvSnippet = infoWindow.findViewById(R.id.tvSnippet);
            tvSnippet.setText(marker.getSnippet());
            return infoWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
    }

    private class locationAdapter extends
            RecyclerView.Adapter<Mymap_fragment.locationAdapter.MyViewHolder> {
        private Context context;
        private List<LocationList> locationList;
        private LocationList locationList1;

        locationAdapter(Context context, List<LocationList> locationList) {
            this.context = context;
            this.locationList = locationList;

        }


        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView loimageView;
            TextView tvdsname;

            MyViewHolder(final View matchView) {
                super(matchView);
                loimageView = matchView.findViewById(R.id.loimageView);
                tvdsname = matchView.findViewById(R.id.tvdsname);

                matchView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        for (int i = 0; i < locationList.size(); i++) {
                            locationList1 = locationList.get(i);
                            if (locationList1.getDistrict() == tvdsname.getText()) {
                                LatLng position = new LatLng(locationList1.getLat(), locationList1.getLon());
                                int postid = locationList1.getPostId();
                                String snippet = locationList1.getDistrict();

                                CameraPosition cameraPosition = new CameraPosition.Builder()
                                        .target(position)
                                        .zoom(5)
                                        .build();
                                CameraUpdate cameraUpdate = CameraUpdateFactory
                                        .newCameraPosition(cameraPosition);
                                mMap.animateCamera(cameraUpdate);

                                Marker marker = mMap.addMarker(new MarkerOptions()
                                        .position(position)
                                        .draggable(false)
                                        .snippet(snippet)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.flag))
                                        .anchor(0, 1));
                                marker.setTag(postid);
                                mMap.setInfoWindowAdapter(new Mymap_fragment.MyInfoWindowAdapter(activity, locationList1));
                                marker.showInfoWindow();
                            }
                        }

                    }
                });

            }
        }

        @Override
        public int getItemCount() {
            return locationList.size();
        }

        @NonNull
        @Override
        public Mymap_fragment.locationAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View matchView = LayoutInflater.from(context).
                    inflate(R.layout.locationitem, viewGroup, false);
            return new Mymap_fragment.locationAdapter.MyViewHolder(matchView);
        }

        @Override
        public void onBindViewHolder(@NonNull final Mymap_fragment.locationAdapter.MyViewHolder viewHolder, int position) {
            final LocationList locationList1 = locationList.get(position);
            viewHolder.tvdsname.setText(locationList1.getDistrict());

            int imageSize = getResources().getDisplayMetrics().widthPixels / 4;
            String url = Common.URL + "/spotServlet";
            int postid = (int) locationList1.getPostId();
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
                viewHolder.loimageView.setImageBitmap(bitmap);

            } else {
                viewHolder.loimageView.setImageResource(R.drawable.logo);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.mypage_mapview, menu);
        super.onCreateOptionsMenu(menu,menuInflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_charts:
                Intent intentmatch = new Intent();
                intentmatch.setClass(getActivity(), Mypage_Chart_Activity.class);
                startActivity(intentmatch);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
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
