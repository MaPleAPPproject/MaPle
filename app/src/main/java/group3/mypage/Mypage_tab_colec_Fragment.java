package group3.mypage;

import android.content.Context;
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

public class Mypage_tab_colec_Fragment extends Fragment {
    private static final String TAG = "MypagetabcolecFragment";
    private List<Postdetail> postdetail;
    private CommonTask pictureGetTopTask;




    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_collection, container, false);
        handleviews(view);
        showAllPosts();
        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
//        showAllPosts();
    }

    private void showAllPosts() {
        if (Common.networkConnected(getActivity())) {
            String url = Common.URL + "/PictureServlet";
            List<Picture> picturestop = null;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAllPost");
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
                Toast.makeText(getActivity(),R.string.msg_NoPost,Toast.LENGTH_SHORT).show();
            }
            else {
            }
        } else {
        }
    }

    private void handleviews(View view) {
        RecyclerView rvCollection = view.findViewById(R.id.rvCollection);
        rvCollection.setLayoutManager(new GridLayoutManager(getActivity(),3));
        rvCollection.setAdapter(new Mypage_tab_colec_Fragment.PostAdapter(postdetail, getActivity()));
    }

    public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {
        List<Postdetail> postdetails;
        Context context;

        public PostAdapter(List<Postdetail> postdetails, Context context) {
            this.postdetails = postdetails;
            this.context = context;

        }

        @Override
        public int getItemCount() {
//            return postdetails.size();
            return 0;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
//                imageView = itemView.findViewById(R.id.ivtop);
            }
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View item_view = layoutInflater.inflate(R.layout.item_view_post, parent, false);
            return new Mypage_tab_colec_Fragment.PostAdapter.MyViewHolder(item_view);
        }


        @Override
        public void onBindViewHolder(@NonNull Mypage_tab_colec_Fragment.PostAdapter.MyViewHolder myViewHolder, int position) {
            final Postdetail postdetail = postdetails.get(position);
//            myViewHolder.imageView.setImageResource(postdetail.getImageId());
//            myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent();
//                    intent.setClass(context, Explore_PA_PostFragment.class);
//                    startActivity(intent);
            //        /* 取得Bundle物件 */
//                    int programming = Integer.parseInt(etProgramming.getText().toString());
//                    Bundle bundle = new Bundle();
//                    Postdetail post1 = new Postdetail(programming, dataStructure, algorithm);
//                    //轉成物件傳入(key,object)
//                    bundle.putSerializable("score", score);
//                    ResultFragment resultFragment = new ResultFragment();
//                    /* 將Bundle資料轉給resultFragment */
//                    resultFragment.setArguments(bundle);
//                    if(getFragmentManager()==null){
//                        return;
//
//                }
//            });
//
//        }
//    }
        }
    }
}
