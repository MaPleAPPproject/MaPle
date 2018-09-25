package group3.mypage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.cp102group3maple.violethsu.maple.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import group3.Common;
import group3.explore.TabFragment_Card;
import group3.explore.TabFragment_Collect;

import static android.content.Context.MODE_PRIVATE;


public class MypageFragment extends Fragment {
    private static final String TAG = "MypageFragment";
    private ViewPager tabviewPager;
    private TabLayout tabLayout;
    private ImageButton addNewPost, map, chart, snapshot;
    private TextView mTextView;
    private Bundle bundle;
    private int memberId;
    private CommonTask getNameTask;
    private TextView userName;
    private byte[] image;
    private SharedPreferences pref;

    public MypageFragment() {
    }

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
        super.onCreateView(inflater, container, savedInstanceState);
        View rootview = inflater.inflate(R.layout.fragment_mypage, container, false);
        handleviews(rootview);
        loadForMypage();
        tabLayout = rootview.findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(tabviewPager);
        tabLayout.addTab(tabLayout.newTab().setText("Post"));
        tabLayout.addTab(tabLayout.newTab().setText("Collection"));
        tabviewPager = rootview.findViewById(R.id.tabviewPager);
//        tablayput綁定viewpager
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(tabviewPager));
        setupViewPager(tabviewPager);
        snapshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent = new Intent(getActivity(), Mypage_UserProfile_Activity.class);
                startActivity(profileIntent);


            }
        });

        addNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newPostIntent = new Intent(getActivity(), NewPost_Activity.class);
                startActivity(newPostIntent);

            }
        });

        chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chartIntent = new Intent(getActivity(), Mypage_Chart_Activity.class);
                startActivity(chartIntent);
            }
        });
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chartIntent = new Intent(getActivity(), ExGoogleMap.class);
                startActivity(chartIntent);
            }
        });
        return rootview;

    }
//
//    @Override
//    protected int getLayoutResource() {
//        return R.layout.fragment_mypage;
//    }

//    @Override
//    protected void initView() {
//
//    }
//
//    @Override
//    protected void onFragmentVisibleChange(boolean isVisible) {
////        super.onFragmentVisibleChange(isVisible);
//        if (isVisible) {
//            //可见，并且是第一次加载
//            lazyLoad();
//        } else {
//
//        }
//
//    }

    private void handleviews(View rootview) {
        addNewPost = (ImageButton) rootview.findViewById(R.id.btaddNewPost);
        map = (ImageButton) rootview.findViewById(R.id.btMap);
        chart = (ImageButton) rootview.findViewById(R.id.btChart);
        userName = rootview.findViewById(R.id.tvusername);
        snapshot = rootview.findViewById(R.id.snapshot);
        snapshot.setImageResource(R.drawable.icon_facev);

    }


    private void setupViewPager(ViewPager viewPager) {
        TabViewPagerAdapter adapter = new TabViewPagerAdapter(getChildFragmentManager());
        Mypage_tab_colec_Fragment mypage_tab_colec_fragment_ = new Mypage_tab_colec_Fragment();
        Mypage_tab_post_Fragment mypage_tab_post_fragment = new  Mypage_tab_post_Fragment();
        adapter.addFragment(mypage_tab_post_fragment);
        adapter.addFragment(mypage_tab_colec_fragment_);
        viewPager.setAdapter(adapter);
    }

    public class TabViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> tabfragments = new ArrayList<>();
        private final List<String> tabfragmentstext = new ArrayList<>();
        private String tabTitles[] = new String[]{"Postdetail", "Collection"};

        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return tabfragments.get(0);
                case 1:
                    return tabfragments.get(1);
            }
            return null;
        }

        public TabViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return tabfragments.size();
        }

        public void addFragment(Fragment fragment) {
            tabfragments.add(fragment);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            //Fragment隐藏时调用
//            onResume();
        } else {
            //Fragment显示时调用
//            onPause();
        }
    }

    public void loadForMypage() {

        if (Common.networkConnected(getActivity())) {
            String url = Common.URL + "/User_profileServlet";
            User_Profile userProfiles = null;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findById");
            jsonObject.addProperty("memberId", memberId);
            String jsonOut = jsonObject.toString();
            getNameTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = getNameTask.execute().get();
                userProfiles = new Gson().fromJson(jsonIn, User_Profile.class);

            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (userProfiles == null) {
                Toast.makeText(getActivity(), "no_profile", Toast.LENGTH_SHORT).show();
            } else {
                userName.setText(userProfiles.getUserName());

            }

            int imageSize = getResources().getDisplayMetrics().widthPixels / 4;
            Bitmap bitmap = null;

            try {
                bitmap = new ImageTask(url, memberId, imageSize, snapshot).execute().get();

            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (bitmap != null) {
                snapshot.setImageBitmap(bitmap);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                image = out.toByteArray();

            } else {
                Toast.makeText(getActivity(), "no_image", Toast.LENGTH_SHORT).show();

            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (getNameTask != null)
            getNameTask.cancel(true);

    }

    //    private void lazyLoad() {
//        if (!isFirst) {
//            isFirst = true;
//        }
//    }


}
