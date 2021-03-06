package group3.explore;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ToggleButton;

import com.cp102group3maple.violethsu.maple.R;
import com.google.android.gms.flags.impl.SharedPreferencesFactory;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.OnTouch;
import group3.Common;
import group3.Picture;
import group3.Post;
import group3.Postdetail;
import group3.mypage.CommonTask;
import group3.mypage.ImageTask;

import static android.content.Context.MODE_PRIVATE;
import static android.support.constraint.motion.MotionScene.TAG;

public class Explore_PA_PostFragment extends android.support.v4.app.Fragment {
    private static final String TAG = "Explore_PA_PostFragment";
    private Button btBack;
    private ImageView imageView,ivicon;
//    private ImageView btcollect;
    private ToggleButton btcollect;
    private TextView tvname,tvlocation,tvcomment,tvdate,tvcollectcount,tvclickcount;
    private android.support.v4.app.Fragment fragment;
    private Bundle bundle;
    private ImageTask iconTask;
    private PostTask postTask;
    private PostDetailTask postdetailTask;
    private Picture picture;
    private Postdetail postdetail;
    private CommonTask collectTask;
    private CommonTask collectcheckTask;
    private int collectorid;
    private SharedPreferences pref;
    private CommonTask collectOffTask;
    private int collectCount;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getActivity().getSharedPreferences(Common.PREF_FILE,
                MODE_PRIVATE);
        String smemberId = pref.getString("MemberId", "");
        collectorid=Integer.parseInt(smemberId);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view=inflater.inflate(R.layout.explore_pa_otherspage_card,container,false);
        Picture picture = (Picture) (getArguments() != null ? getArguments().getSerializable("picture") : null);
        bundle=new Bundle();
        bundle.putSerializable("picture", picture);
        handleviews(view);
        btcollect.setOnClickListener(new checkButton_OnclickLister());
        return view;
    }

    private void handleviews(View view) {
        btcollect=view.findViewById(R.id.btcollect);
        tvname=view.findViewById(R.id.tvName);
        tvcomment=view.findViewById(R.id.tvdescription);
        tvlocation=view.findViewById(R.id.tvlocation);
        tvcollectcount=view.findViewById(R.id.tvcollection);
        tvdate=view.findViewById(R.id.tvdate);
        /* 取得探索頁面點擊該照片的詳細資料 */
        picture = (Picture) (getArguments() != null ? getArguments().getSerializable("picture") : null);
        if (picture != null) {
            tvdate.setText(picture.getFormatedDate());
            tvcomment.setText(picture.getComment());
        } else {
//            Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
        }
        showdetail();
        tvname.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment = new OtherspageFragment();
                Bundle memberbundle=new Bundle();
                memberbundle.putString("username", postdetail.getUsername());
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
            bitmap = postTask.execute().get();
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
            iconTask.execute().get();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

            tvlocation.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fragment = new MapFragment();
                    Bundle bundle = new Bundle();
                    bundle.putLong("lat", postdetail.getLat());
                    bundle.putLong("lon",postdetail.getLon());
                    bundle.putString("district",postdetail.getDistrict());
                    fragment.setArguments(bundle);
                    changeFragment(fragment);
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
                Toast.makeText(getActivity(),"Po文不存在",Toast.LENGTH_SHORT).show();
            }
            else {
                tvname.setText(postdetail.getUsername());
                tvcollectcount.setText(String.valueOf(postdetail.getCollectioncount()));
                tvlocation.setText(String.valueOf(postdetail.getDistrict()));
                iscollectable(collectorid,postdetail.getPostId());
            }
        } else {
            Toast.makeText(getActivity(),"請確認網路連線",Toast.LENGTH_SHORT).show();
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
        if (collectcheckTask != null) {
            collectcheckTask.cancel(true);
        }
        if (collectOffTask != null) {
            collectOffTask.cancel(true);
        }
    }

    private void addcollect() {
        if (Common.networkConnected(getActivity())) {
            String url = Common.URL + "/UserPreferenceServlet";
            //      要取偏好設定的memberid
            UserPreference userPreference=new UserPreference(postdetail.getPostId(),collectorid,postdetail.getMemberId(),postdetail.getCollectioncount()+1);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "userpreInsert");
            jsonObject.addProperty("userpe", new Gson().toJson(userPreference));
            String jsonOut = jsonObject.toString();
            collectTask = new CommonTask(url, jsonOut);
            int count = 0;
            try {
                String result = collectTask.execute().get();
                count = Integer.valueOf(result);

            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (count == 0) {
                Toast.makeText(getActivity(),"收藏失敗",Toast.LENGTH_SHORT).show();
            } else {
                showdetail();
                Toast.makeText(getActivity(),"已加入收藏",Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "網路連線失敗", Toast.LENGTH_SHORT).show();
        }

    }
    private void cancelcollect(int collectorid, int postid) {
        if (Common.networkConnected(getActivity())) {
            String url = Common.URL + "/UserPreferenceServlet";
            //      要取偏好設定的memberid
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "userpreDelete");
            jsonObject.addProperty("postid", postid);
            jsonObject.addProperty("collectorid", collectorid);//      要取偏好設定的memberid
            String jsonOut = jsonObject.toString();
            collectOffTask = new CommonTask(url, jsonOut);
            int count = 0;
            try {
                String result = collectOffTask.execute().get();
                count = Integer.valueOf(result);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (count == 0) {
                Toast.makeText(getActivity(),"取消收藏失敗",Toast.LENGTH_SHORT).show();
            } else {
                showdetail();
                Toast.makeText(getActivity(),"已刪除收藏",Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "網路連線失敗", Toast.LENGTH_SHORT).show();
        }
    }
    private boolean iscollectable(int collectorid, int postid) {
        boolean iscollectable=false;
        if (Common.networkConnected(getActivity())) {
            String url = Common.URL + "/UserPreferenceServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "userValid");
            jsonObject.addProperty("collectorid", collectorid);//      要取偏好設定的memberid
            jsonObject.addProperty("postid", postid);
            String jsonOut = jsonObject.toString();
            collectcheckTask = new CommonTask(url, jsonOut);
            try {
                String result = collectcheckTask.execute().get();
                iscollectable = Boolean.valueOf(result);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                iscollectable = false;
            }
            btcollect.setChecked(!iscollectable);
        } else {
            Toast.makeText(getActivity(), "網路連線失敗", Toast.LENGTH_SHORT).show();
        }
        return iscollectable;
    }
    @Override
    public void onStart() {
        super.onStart();
        showdetail();
    }

   private class checkButton_OnclickLister implements ToggleButton.OnClickListener {

        @Override
        public void onClick(View v) {
            if (btcollect.isChecked()) {
                cancelcollect(collectorid,postdetail.getPostId());//isChecked() ->true 為按鈕被選取

            }else {
                addcollect();
            }
        }
    }

}
