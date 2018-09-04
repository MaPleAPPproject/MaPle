package group3;

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

import com.example.violethsu.maple.R;

import java.util.List;

public class Explore_PA_PostFragment extends Fragment{
    private Button btBack;
    private ImageView imageView;
    private TextView tvid,tvlocation,tvcomment;
    private Fragment fragment;
    private Bundle bundle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view=inflater.inflate(R.layout.explore_pa_postfrag,container,false);
        handleviews(view);
        return view;
    }

    private void handleviews(View view) {
        imageView=view.findViewById(R.id.ivPhoto);
        tvid=view.findViewById(R.id.tvName);
        tvcomment=view.findViewById(R.id.tvdescription);
        tvlocation=view.findViewById(R.id.tvlocation);
        tvid.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment = new OtherspageFragment();
                changeFragment(fragment);
            }
        });
        imageView=view.findViewById(R.id.btcollect);
//        imageView.setOnClickListener(new Button.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
        /* 取得Bundle物件 */
        Bundle bundle = getArguments();
        /* 如果Bundle不為null，進一步取得Friend物件 */
        Post post = bundle == null ? null : (Post) bundle.getSerializable("post");
        /* 如果Friend物件不為null，顯示各個屬性值，否則顯示錯誤訊息 */
        if (post != null) {
            imageView.setImageResource(post.getImageId());
            tvid.setText(post.getPersonid());
            tvlocation.setText(post.getLocation());
            tvcomment.setText(post.getComment());
        } else {
            Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
        }
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