package group3.mypage;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cp102group3maple.violethsu.maple.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import group3.BaseFragment;
import group3.Common;
import group3.Picture;
import group3.explore.OtherspageFragment;
import group3.explore.TabFragment_Card;
import group3.explore.TabFragment_Collect;

public class MypageFragment extends Fragment {
    private static final String TAG = "MypageFragment";
    private ViewPager tabviewPager;
    private TabLayout tabLayout;
    private ImageButton addNewPost, map, chart, snapshot;
    private TextView mTextView;
    private Bundle bundle;

    public MypageFragment(){};



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootview = inflater.inflate(R.layout.fragment_mypage, container, false);
        handleviews(rootview);
        tabLayout = rootview.findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(tabviewPager);
        tabLayout.addTab(tabLayout.newTab().setText("Post"));
        tabLayout.addTab(tabLayout.newTab().setText("Collection"));
        tabviewPager= rootview.findViewById(R.id.tabviewPager);
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
        snapshot = (ImageButton) rootview.findViewById(R.id.snapshot);
    }



    private void setupViewPager(ViewPager viewPager) {
        TabViewPagerAdapter adapter = new TabViewPagerAdapter(getChildFragmentManager());
        TabFragment_Card tabFragment_card=new TabFragment_Card();
        TabFragment_Collect tabFragment_collect=new TabFragment_Collect();
        adapter.addFragment(tabFragment_card);
        adapter.addFragment(tabFragment_collect);
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

//    private void lazyLoad() {
//        if (!isFirst) {
//            isFirst = true;
//        }
//    }



}
