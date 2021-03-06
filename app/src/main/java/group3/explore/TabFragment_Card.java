package group3.explore;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import com.cp102group3maple.violethsu.maple.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import group3.Common;
import group3.Picture;
import group3.mypage.CommonTask;

public class TabFragment_Card extends Fragment {
    private static final String TAG = "TabFragment_Card";
    private CommonTask pictureGetAllTask;
    private RecyclerView rvPost;
    private int memberid;
    private PostTask pictureImageTask;
    private Context contentview;
    private Bundle bundle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.tab_post, container, false);
        bundle=getArguments();
        memberid = bundle.getInt("memberid");
        rvPost = view.findViewById(R.id.rvPost);
//        EmptyRecyclerView rvPost = view.findViewById(R.id.erview);
        rvPost.setLayoutManager(new GridLayoutManager(contentview,3));
        rvPost.setNestedScrollingEnabled(false);
        contentview=view.getContext();
        showAllPosts();
        return view;

    }

    private void showAllPosts() {

        if (Common.networkConnected(getActivity())) {
            String url = Common.URL + "/PictureServlet";
            List<Picture> pictures = null;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getBymemberId");
            jsonObject.addProperty("memberid", memberid);
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
                Toast.makeText(getActivity(),"貼文不存在",Toast.LENGTH_SHORT).show();
            }
            else {
                rvPost.setAdapter(new TabFragment_Card.PostAdapter(pictures,contentview));
            }
        } else {
            Toast.makeText(getActivity(), "網路連線異常", Toast.LENGTH_SHORT).show();
        }

    }

    public class PostAdapter extends RecyclerView.Adapter<TabFragment_Card.PostAdapter.MyViewHolder> {
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

        @NonNull
        @Override
        public TabFragment_Card.PostAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            View item_view = layoutInflater.inflate(R.layout.item_view_picture, parent, false);
            return new TabFragment_Card.PostAdapter.MyViewHolder(item_view);
        }


        @Override
        public void onBindViewHolder(@NonNull TabFragment_Card.PostAdapter.MyViewHolder myViewHolder, int position) {
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
}


