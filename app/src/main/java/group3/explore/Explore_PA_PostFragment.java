package group3.explore;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cp102group3maple.violethsu.maple.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.OnTouch;
import group3.Common;
import group3.Picture;
import group3.Postdetail;
import group3.mypage.CommonTask;
import group3.mypage.ImageTask;

import static android.support.constraint.motion.MotionScene.TAG;

public class Explore_PA_PostFragment extends android.support.v4.app.Fragment {
    private Button btBack;
    private ImageView imageView,ivicon;
    private ImageView btcollect;
    private TextView tvname,tvlocation,tvcomment,tvdate,tvcollectcount,tvclickcount;
    private android.support.v4.app.Fragment fragment;
    private Bundle bundle;
    private ImageTask iconTask;
    private PostTask postTask;
    private PostDetailTask postdetailTask;
    private Picture picture;
    private Postdetail postdetail;
    private CommonTask collectTask;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view=inflater.inflate(R.layout.explore_pa_postfrag,container,false);
        Picture picture = (Picture) (getArguments() != null ? getArguments().getSerializable("picture") : null);
        bundle=new Bundle();
        bundle.putSerializable("picture", picture);
        handleviews(view);
        return view;
    }

    private void handleviews(View view) {
        btcollect=view.findViewById(R.id.btcollect);
        tvname=view.findViewById(R.id.tvName);
        tvcomment=view.findViewById(R.id.tvdescription);
        tvlocation=view.findViewById(R.id.tvlocation);
        tvclickcount=view.findViewById(R.id.tvclickcount);
        tvcollectcount=view.findViewById(R.id.tvcollection);
        tvdate=view.findViewById(R.id.tvdate);
        /* 取得探索頁面點擊該照片的詳細資料 */
        picture = (Picture) (getArguments() != null ? getArguments().getSerializable("picture") : null);
        if (picture != null) {
            tvdate.setText(String.valueOf(picture.getDate()));
            tvcomment.setText(picture.getComment());
        } else {
            Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
        }
        showdetail();
        tvname.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment = new OtherspageFragment();
                Bundle memberbundle=new Bundle();
                memberbundle.putString("tvname", postdetail.getUsername());
                memberbundle.putInt("memberid", postdetail.getMemberId());
                fragment.setArguments(memberbundle);
                changeFragment(fragment);
            }
        });
        imageView=view.findViewById(R.id.ivPhoto);
        String url = Common.URL + "/PictureServlet";
        int id = picture.getPostid();
        int imageSize = getResources().getDisplayMetrics().widthPixels;
        Bitmap bitmap = null;
        try {
            postTask = new PostTask(url, id, imageSize);
            // passing null and calling get() means not to run FindImageByIdTask.onPostExecute()
            bitmap = postTask.execute().get();
//              等圖才設定文字 因為只有一張圖 且用點擊
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageResource(R.drawable.ic_navigation_explore);
        }
        ivicon=view.findViewById(R.id.personicon);
        String urlicon = Common.URL + "/User_profileServlet";
        int memberid = postdetail.getMemberId();
        int iconimageSize = getResources().getDisplayMetrics().widthPixels/3;
        try {
            iconTask = new ImageTask(urlicon, memberid, iconimageSize,ivicon);
            // passing null and calling get() means not to run FindImageByIdTask.onPostExecute()
            iconTask.execute().get();
//              等圖才設定文字 因為只有一張圖 且用點擊
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
//      收藏鍵(need to add update when leave/if else change color)
        btcollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawableCompat.setTint(btcollect.getDrawable(), ContextCompat.getColor(getContext(),
                        R.color.colorRed));
                tvcollectcount.setText(String.valueOf(postdetail.getCollectioncount()+1));
//                updatecollect();
            }
        });
    }

    private void showdetail() {
        if (Common.networkConnected(getActivity())) {
            String url = Common.URL + "/PostDetailServlet";
            int postid= picture.getPostid();
            postdetail=null;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findById");
            jsonObject.addProperty("postid", postid);
            String jsonOut = jsonObject.toString();
            postdetailTask = new PostDetailTask(url, jsonOut);
            try {
                String jsonIn = postdetailTask.execute().get();
                postdetail = new Gson().fromJson(jsonIn,Postdetail.class);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (postdetail == null) {
                Toast.makeText(getActivity(),R.string.msg_NoPost,Toast.LENGTH_SHORT).show();
            }
            else {
                tvname.setText(postdetail.getUsername());
                tvcollectcount.setText(String.valueOf(postdetail.getCollectioncount()));
                tvclickcount.setText(String.valueOf(postdetail.getClickcount()));
                tvlocation.setText(String.valueOf(postdetail.getDistrict()));
            }
        } else {
            Toast.makeText(getActivity(),R.string.msg_Nonetwork,Toast.LENGTH_SHORT).show();
        }

    }

    private void changeFragment(android.support.v4.app.Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.postactivitycontainer, fragment).commit();
    }
    public void onStop() {
        super.onStop();
        if (postTask != null) {
            postTask.cancel(true);
        }
        if (postdetailTask != null) {
            postdetailTask.cancel(true);
        }
        if (iconTask != null) {
            iconTask.cancel(true);
        }
        if (collectTask != null) {
            collectTask.cancel(true);
        }

//        if(postdetail.getCollectioncount()!=Integer.parseInt(tvcollectcount.getText().toString())){
//            updatecollect();
//        }

    }

    private void updatecollect() {

        if (Common.networkConnected(getActivity())) {
            String url = Common.URL + "/UserPreferenceServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "userpreInsert");
            jsonObject.addProperty("postid", postdetail.getPostId());
            jsonObject.addProperty("collectorid", postdetail.getMemberId());
            //      要取偏好設定的memberid
            jsonObject.addProperty("memberid", 2);
            String jsonOut = jsonObject.toString();
            collectTask = new CommonTask(url, jsonOut);
            try {
                collectTask.execute();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }Toast.makeText(getActivity(), R.string.msg_Nonetwork, Toast.LENGTH_SHORT).show();

    }
    @Override
    public void onStart() {
        super.onStart();
        showdetail();
    }
}