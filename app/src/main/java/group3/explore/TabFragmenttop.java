package group3.explore;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.cp102group3maple.violethsu.maple.R;
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

public class TabFragmenttop extends Fragment {
    private static final String TAG = "TabFragmenttop";
    private CommonTask pictureGetTopTask;
    private RecyclerView recyclerView;
    private PostTask pictureImageTask;
    private Context contentview;
    private Bundle bundle;
    List<Picture> picturelist = new ArrayList<>();
    private List<Picture> picturestop;
    private PostAdapter adpter;
    private GridLayoutManager gridLayoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.tab_collection, container, false);

        recyclerView = view.findViewById(R.id.rvCollection);
        gridLayoutManager = new GridLayoutManager(contentview,3);
        recyclerView.setLayoutManager(gridLayoutManager);
        contentview=view.getContext();
        showTopPosts();
        return view;

    }
private void showTopPosts() {
    if (Common.networkConnected(getActivity())) {
        String url = Common.URL + "/PictureServlet";
        picturestop = null;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "getTop");
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
            adpter=new TabFragmenttop.PostAdapter(picturestop, getActivity());
            recyclerView.setAdapter(adpter);
        }
    } else {
        Toast.makeText(contentview,"網路連線異常", Toast.LENGTH_SHORT).show();
    }

}

    public class PostAdapter extends RecyclerView.Adapter<TabFragmenttop.PostAdapter.MyViewHolder> {
        private int imageSize;
        List<Picture> pictures;
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
        public TabFragmenttop.PostAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            View item_view = layoutInflater.inflate(R.layout.item_view_picture, parent, false);
            return new TabFragmenttop.PostAdapter.MyViewHolder(item_view);
        }


        @Override
        public void onBindViewHolder(@NonNull TabFragmenttop.PostAdapter.MyViewHolder myViewHolder, int position) {
            final Picture picture = pictures.get(position);
            String url = Common.URL + "/PictureServlet";
            //發起PostTask 使用picture中的id取的圖檔資料
            int id = picture.getPostid();
            pictureImageTask = new PostTask(url, id, imageSize, myViewHolder.imageView);
            pictureImageTask.execute();
            myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent =new Intent(getActivity(),Explore_PostActivity.class);
                    Bundle bundle=new Bundle();
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


