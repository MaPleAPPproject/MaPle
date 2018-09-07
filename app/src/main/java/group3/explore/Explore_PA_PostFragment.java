package group3.explore;

import android.app.WallpaperManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cp102group3maple.violethsu.maple.R;

import group3.Post;
import group3.explore.OtherspageFragment;

public class Explore_PA_PostFragment extends Fragment{
    private Button btBack;
    private ImageView imageView,ivcollect;
    private TextView tvid,tvlocation,tvcomment;
    private Fragment fragment;
    private Bundle bundle;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view=inflater.inflate(R.layout.explore_pa_postfrag,container,false);
        Post post = (Post) (getArguments() != null ? getArguments().getSerializable("post") : null);
        bundle=new Bundle();
        bundle.putSerializable("post",post);
        handleviews(view);
        return view;
    }

    private void handleviews(View view) {
        ivcollect=view.findViewById(R.id.btcollect);
        tvid=view.findViewById(R.id.tvName);
        tvcomment=view.findViewById(R.id.tvdescription);
        tvlocation=view.findViewById(R.id.tvlocation);
        imageView=view.findViewById(R.id.ivPhoto);
        /* 取得探索頁面點擊該照片的詳細資料 */
        Post post = (Post) (getArguments() != null ? getArguments().getSerializable("post") : null);
        if (post != null) {
            imageView.setImageResource(post.getImageId());
            tvid.setText(post.getPersonid());
            tvlocation.setText(post.getLocation());
            tvcomment.setText(post.getComment());
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
//      收藏鍵
//        ivcollect.setOnClickListener(new Button.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
    }

    private void changeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.postactivitycontainer, fragment);
//        fragmentTransaction.commit();
        fragmentTransaction.commitAllowingStateLoss();
    }
}