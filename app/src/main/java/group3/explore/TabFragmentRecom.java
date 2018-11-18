package group3.explore;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.cp102group3maple.violethsu.maple.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import group3.Common;
import group3.Picture;
import group3.mypage.CommonTask;

import static android.content.Context.MODE_PRIVATE;

public class TabFragmentRecom extends Fragment {
    private static final String TAG = "TabFragmentRecom";
    private CommonTask pictureGetAllTask;
    private RecyclerView recyclerView;
    private int memberId;
    private PostTask pictureImageTask;
    private Context contentview;
    private List<Picture> pictures;
    private PostAdapter adpter;
    private SharedPreferences pref;
    List<Picture> picturelist = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getActivity().getSharedPreferences(Common.PREF_FILE,
                MODE_PRIVATE);
        String smemberId = pref.getString("MemberId", "");
        memberId=Integer.parseInt(smemberId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.explore_tab_recom, container, false);

        recyclerView = view.findViewById(R.id.rvPost);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));
        contentview=view.getContext();
        showAllPosts();
        return view;

    }
private void showAllPosts() {
    if (Common.networkConnected(getActivity())) {
        String url = Common.URL + "/PictureServlet";
        pictures = null;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "getRecom");
        jsonObject.addProperty("memberid", memberId);
        String jsonOut = jsonObject.toString();
        pictureGetAllTask = new CommonTask(url, jsonOut);
        try {
            String jsonIn = pictureGetAllTask.execute().get();
            Type listType = new TypeToken<List<Picture>>() {
            }.getType();
            pictures = new Gson().fromJson(jsonIn, listType);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        if (pictures == null||pictures.isEmpty()) {
            Toast.makeText(contentview,"貼文不存在",Toast.LENGTH_SHORT).show();
        }
        else {
            adpter=new TabFragmentRecom.PostAdapter(pictures, getActivity());
            recyclerView.setAdapter(adpter);
        }
    } else {
        Toast.makeText(contentview, "網路連線異常", Toast.LENGTH_SHORT).show();
    }

}

    public class PostAdapter extends RecyclerView.Adapter<TabFragmentRecom.PostAdapter.MyViewHolder> {
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
        public TabFragmentRecom.PostAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            View item_view = layoutInflater.inflate(R.layout.item_view_picture, parent, false);
            return new TabFragmentRecom.PostAdapter.MyViewHolder(item_view);
        }


        @Override
        public void onBindViewHolder(@NonNull TabFragmentRecom.PostAdapter.MyViewHolder myViewHolder, int position) {
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
        Log.e("QueryFragment", query);
        picturelist.clear();
        picturelist.addAll(pictures);
        final  List<Picture> filtermodelist=filter(picturelist,query);
        adpter.setfilter(filtermodelist);    }

    @Override
    public void onStop() {
        super.onStop();
        if (pictureImageTask != null) {
            pictureImageTask.cancel(true);
        }
        if (pictureGetAllTask != null) {
            pictureGetAllTask.cancel(true);
        }
    }
    public List<Picture> filter(List<Picture> pl,String query)
    {
        Log.d(TAG, "filter");
        query=query.toLowerCase();
        final List<Picture> filteredModeList=new ArrayList<>();
        for (Picture model:pl)
        {
            String text = model.getDistrict();
            if (text == null) {
                text = " ";
            }
            text=text.toLowerCase();
            if (text.startsWith(query))
            {
                filteredModeList.add(model);
            }
        }
        return filteredModeList;
    }
}


