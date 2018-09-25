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
import android.support.v7.widget.StaggeredGridLayoutManager;
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
import group3.Postdetail;
import group3.explore.ExploreFragment;
import group3.explore.Explore_PostActivity;
import group3.explore.PostTask;
import group3.explore.TabFragment_Collect;

import static android.content.Context.MODE_PRIVATE;

public class Mypage_tab_colec_Fragment extends Fragment {
    private static final String TAG = "Mypage_tab_colec_Fragment";
    private CommonTask pictureGetAllTask;
    private RecyclerView rvCollection;
    private int memberid;
    private PostTask pictureImageTask;
    private Context contentview;
    private Bundle bundle;
    private SharedPreferences pref;

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
        View view=inflater.inflate(R.layout.tab_collection, container, false);
        handleviews(view);
        return view;

    }
    @Override
    public void onStart() {
        super.onStart();
        showAllPosts();
    }
    //  取得收藏的照片
    private void showAllPosts() {
        if (Common.networkConnected(getActivity())) {

//            bundle=getArguments();
//            int memberid=bundle.getInt("memberid");
            String url = Common.URL + "/PictureServlet";
            List<Picture> pictures = null;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getcollectBymemberId");
            jsonObject.addProperty("memberid", memberid);
            String jsonOut = jsonObject.toString();
            pictureGetAllTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = pictureGetAllTask.execute().get();
                Type listType = new TypeToken<List<Picture>>() {
                }.getType();
                pictures = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
//                Log.e(TAG, e.toString());
            }
            if (pictures == null||pictures.isEmpty()) {
                Toast.makeText(getActivity(),R.string.msg_NoPost,Toast.LENGTH_SHORT).show();
            }
            else {
                rvCollection.setAdapter(new Mypage_tab_colec_Fragment.PostAdapter(pictures, contentview));
            }
        } else {
            Toast.makeText(getActivity(), R.string.msg_NoNetwork, Toast.LENGTH_SHORT).show();
        }

    }

    private void handleviews(View view) {
        if (bundle != null) {
            showAllPosts();
        } else {
            Toast.makeText(getActivity(), "no bundle", Toast.LENGTH_SHORT).show();
        }
        rvCollection = view.findViewById(R.id.rvCollection);
        rvCollection.setLayoutManager(new GridLayoutManager(contentview,3));
        contentview=view.getContext();
    }

    public class PostAdapter extends RecyclerView.Adapter<Mypage_tab_colec_Fragment.PostAdapter.MyViewHolder> {
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
                imageView = itemView.findViewById(R.id.ivcollect);
            }
        }
        @Override
        public int getItemCount() {
            return pictures.size();
        }

        @NonNull
        @Override
        public Mypage_tab_colec_Fragment.PostAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            View item_view = layoutInflater.inflate(R.layout.item_view_collectionpicture, parent, false);
            return new Mypage_tab_colec_Fragment.PostAdapter.MyViewHolder(item_view);
        }


        @Override
        public void onBindViewHolder(@NonNull Mypage_tab_colec_Fragment.PostAdapter.MyViewHolder myViewHolder, int position) {
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
