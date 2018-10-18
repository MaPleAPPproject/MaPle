package group3.explore;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

import group3.Common;
import group3.Picture;
import group3.mypage.CommonTask;

public class TabFragmentLocation extends Fragment implements OnMapReadyCallback {
    private static final String TAG = "TabFragmenttop";
    private CommonTask pictureGetTopTask;
    private RecyclerView recyclerView;
    private int memberid;
    private PostTask pictureImageTask;
    private Context contentview;
    private Bundle bundle;
    private GoogleMap map;
    private Picture picturelocation;
    List<Picture> picturelist = new ArrayList<>();
    private GridLayoutManager gridLayoutManager;
    private PostAdapter adpter;
    private List<Picture>  picturestop;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SupportMapFragment supportMapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.exploremap);
        supportMapFragment.getMapAsync(this);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.explore_tab_location, container, false);
        recyclerView = view.findViewById(R.id.rvCollection);
        gridLayoutManager = new GridLayoutManager(contentview,3);
        recyclerView.setLayoutManager(gridLayoutManager);
        contentview=view.getContext();
        showAllPosts();
        return view;

    }
private void showAllPosts() {
    if (Common.networkConnected(getActivity())) {
        String url = Common.URL + "/PictureServlet";
        picturestop = null;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "getAll");
        String jsonOut = jsonObject.toString();
        pictureGetTopTask = new CommonTask(url, jsonOut);
        try {
            String jsonIn = pictureGetTopTask.execute().get();
            Type listType = new TypeToken<List<Picture>>() {
            }.getType();
            picturestop = new Gson().fromJson(jsonIn, listType);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        if (picturestop == null||picturestop.isEmpty()) {
            Toast.makeText(contentview,"貼文不存在",Toast.LENGTH_SHORT).show();
        }
        else {
            adpter=new TabFragmentLocation.PostAdapter(picturestop, getActivity());
            recyclerView.setAdapter(adpter);
//            recyclerView.setAdapter(new TabFragmentLocation.PostAdapter(picturestop, getActivity()))
        }
    } else {
        Toast.makeText(contentview,"網路連線異常", Toast.LENGTH_SHORT).show();
    }

}

    private void showMap(List<Picture> picturelist) {
        for(int i = 0;i < picturelist.size();i++) {
            picturelocation = picturelist.get(i);
            LatLng cameratarget = new LatLng(picturelist.get(0).getLat(),picturelist.get(0).getLon());
            LatLng position = new LatLng(picturelocation.getLat(), picturelocation.getLon());
            int posid = picturelocation.getPostid();

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(cameratarget)
                    .zoom(2)
                    .build();
            CameraUpdate cameraUpdate = CameraUpdateFactory
                    .newCameraPosition(cameraPosition);
            map.animateCamera(cameraUpdate);

            // ic_add spot on the map
            Marker marker = map.addMarker(new MarkerOptions()
                    .position(position)
                    .draggable(false)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.flag))
                    .anchor(0, 1));
            marker.setTag(posid);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        }
        map.getUiSettings().setZoomControlsEnabled(true);
        if (picturestop == null) {
            Toast.makeText(getActivity(),"找尋不到該地點",Toast.LENGTH_SHORT).show();
        } else {
            showMap(picturestop);
        }

    }

    public class PostAdapter extends RecyclerView.Adapter<TabFragmentLocation.PostAdapter.MyViewHolder> {
        private int imageSize;
        private List<Picture> pictures;
        private LayoutInflater layoutInflater;

        PostAdapter(List<Picture> pictures, Context context) {
            this.pictures = pictures;
            layoutInflater = LayoutInflater.from(context);
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.ivrecom);
            }
        }
        @Override
        public int getItemCount() {
            return pictures.size();
        }

        public void setfilter(List<Picture> listitem)
        {
            Log.d(TAG, "setfilter");
            pictures=new ArrayList<>();
            pictures.addAll(listitem);
            notifyDataSetChanged();
        }
        @NonNull
        @Override
        public TabFragmentLocation.PostAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            View item_view = layoutInflater.inflate(R.layout.item_view_picture, parent, false);
            return new TabFragmentLocation.PostAdapter.MyViewHolder(item_view);
        }


        @Override
        public void onBindViewHolder(@NonNull TabFragmentLocation.PostAdapter.MyViewHolder myViewHolder, int position) {
            final Picture picture = pictures.get(position);
            String url = Common.URL + "/PictureServlet";
            //發起PostTask 使用picture中的id取的圖檔資料
            int id = picture.getPostid();
            pictureImageTask = new PostTask(url, id, imageSize, myViewHolder.imageView);
            pictureImageTask.execute();
            myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), Explore_PostActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("picture", picture);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });

        }
        }
    public void beginSearch(String query) {
        Log.d("beginSearch", query);
        if(picturelist!=null){
            picturelist.clear();
        }
        picturelist.addAll(picturestop);
        final  List<Picture> filtermodelist = topfilter(picturelist,query);
        adpter.setfilter(filtermodelist);
    }
    @Override
    public void onStop() {
        super.onStop();
        if (pictureImageTask != null) {
            pictureImageTask.cancel(true);
        }
        if (pictureGetTopTask != null) {
            pictureGetTopTask.cancel(true);
        }
    }
   public List<Picture> topfilter(List<Picture> pl,String query)
    {
        Log.d(TAG, "filter");
        query = query.toLowerCase();
        final List<Picture> filteredModeList=new ArrayList<>();
        for (Picture model:pl)
        {
             String text = model.getDistrict().toLowerCase();
            if (text.startsWith(query))
            {
                filteredModeList.add(model);
            }
        }
        return filteredModeList;
    }
}


