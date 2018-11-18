package group3.mypage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cp102group3maple.violethsu.maple.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import group3.Common;
import group3.Picture;
import group3.Postdetail;
import group3.explore.Explore_PostActivity;
import group3.explore.PostTask;
import group3.explore.TabFragment_Card;
import group3.mypage.CommonTask;
import group3.mypage.Mypage_SinglePost_Activity;

import static android.content.Context.MODE_PRIVATE;

public class Mypage_tab_post_Fragment extends Fragment {
    private static final String TAG = "ＭypagetabpostFragment";
    private CommonTask pictureGetAllTask;
    private RecyclerView rvPost;
    private int memberid;
    private PostTask pictureImageTask;
    private Context contentview;
    private Bundle bundle;
    private SharedPreferences pref;
    private ImageView ivShare;
    private ProgressBar progressBar;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getActivity().getSharedPreferences(Common.PREF_FILE,
                MODE_PRIVATE);
        String smemberId = pref.getString("MemberId", "");
        memberid=Integer.parseInt(smemberId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_post, container, false);
        handleviews(view);
        return view;

    }
    @Override
    public void onStart() {
        super.onStart();
        showAllPosts();
    }
    //  取得照片
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
//                progressBar.setVisibility(View.GONE);
                pictures = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
//                Log.e(TAG, e.toString());
            }
            if (pictures == null||pictures.isEmpty()) {

//               progressBar.setVisibility(View.GONE);
                rvPost.setVisibility(View.GONE);
                ivShare.setVisibility(View.VISIBLE);


            }
            else {
                ivShare.setVisibility(View.GONE);
                rvPost.setVisibility(View.VISIBLE);
                rvPost.setAdapter(new Mypage_tab_post_Fragment.PostAdapter(pictures, contentview));
            }
        } else {
            Toast.makeText(getActivity(), R.string.msg_NoNetwork, Toast.LENGTH_SHORT).show();
        }

    }

    private void handleviews(View view) {
        ivShare = (ImageView) view.findViewById(R.id.ivShare);

        rvPost = view.findViewById(R.id.rvPost);
        rvPost.setLayoutManager(new GridLayoutManager(contentview,3));
        contentview=view.getContext();
//        progressBar = view.findViewById(R.id.pregressBar);
        if (bundle != null) {


            showAllPosts();
        } else {
            rvPost.setVisibility(View.GONE);
            ivShare.setVisibility(View.VISIBLE);
//            tvEmpty.setVisibility(View.VISIBLE);

//            Toast.makeText(getActivity(), "no bundle", Toast.LENGTH_SHORT).show();
        }


    }




    public class PostAdapter extends RecyclerView.Adapter<Mypage_tab_post_Fragment.PostAdapter.MyViewHolder> {
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
        public Mypage_tab_post_Fragment.PostAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            View item_view = layoutInflater.inflate(R.layout.item_view_picture, parent, false);
            return new Mypage_tab_post_Fragment.PostAdapter.MyViewHolder(item_view);
        }


        @Override
        public void onBindViewHolder(@NonNull Mypage_tab_post_Fragment.PostAdapter.MyViewHolder myViewHolder, int position) {
            final Picture picture = pictures.get(position);
            String url = Common.URL + "/PictureServlet";
            //發起PostTask 使用picture中的id取的圖檔資料
            final int id = picture.getPostid();
            pictureImageTask = new PostTask(url, id, imageSize, myViewHolder.imageView);
            pictureImageTask.execute();
            myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent =new Intent(getActivity(),Mypage_SinglePost_Activity.class);
                    Bundle bundle=new Bundle();
                    bundle.putInt("postId", id);
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