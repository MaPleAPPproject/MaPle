package group3.explore;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cp102group3maple.violethsu.maple.R;

import group3.Common;
import group3.Picture;
import group3.Postdetail;

import static android.support.constraint.motion.MotionScene.TAG;

public class Explore_PA_PostFragment extends android.support.v4.app.Fragment {
    private Button btBack;
    private ImageView imageView,ivcollect;
    private TextView tvid,tvlocation,tvcomment;
    private android.support.v4.app.Fragment fragment;
    private Bundle bundle;
    private PostTask postTask;

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
        ivcollect=view.findViewById(R.id.btcollect);
        tvid=view.findViewById(R.id.tvName);
        tvcomment=view.findViewById(R.id.tvdescription);
        tvlocation=view.findViewById(R.id.tvlocation);
        /* 取得探索頁面點擊該照片的詳細資料 */
        Picture picture = (Picture) (getArguments() != null ? getArguments().getSerializable("picture") : null);
        if (picture != null) {
            tvid.setText(String.valueOf(picture.getPostid()));
            tvlocation.setText(String.valueOf(picture.getDate()));
            tvcomment.setText(picture.getComment());
        } else {
            Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
        }
        tvid.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment = new OtherspageFragment();
                fragment.setArguments(bundle);
                changeFragment(fragment);
            }
        });
        imageView=view.findViewById(R.id.ivPhoto);
        String url = Common.URL + "PictureServlet";
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
//      收藏鍵
//        ivcollect.setOnClickListener(new Button.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
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
    }
}