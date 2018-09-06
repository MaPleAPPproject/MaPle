package group3;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;


import com.example.violethsu.maple.R;

import java.util.ArrayList;
import java.util.List;

public class MypageFragment extends Fragment  {
    private ViewPager tabviewPager;
    private TabLayout tabLayout;
    private ImageButton addNewPost,map,chart,snapshot;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootview = inflater.inflate(R.layout.fragment_mypage, container, false);
        handleviews(rootview);

        snapshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent = new Intent(getActivity(),MyPage_User_ProfileActivity.class);
                startActivity(profileIntent);


            }
        });

        addNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newPostIntent = new Intent(getActivity(),NewPostActivity.class);
                startActivity(newPostIntent);

            }
        });



        return rootview;
    }

    private void handleviews(View rootview) {
        tabLayout = (TabLayout) rootview.findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(tabviewPager);
        tabviewPager= rootview.findViewById(R.id.tabviewPager);
//        tabLayout.addTab(tabLayout.newTab().setText("Post"));
//        tabLayout.addTab(tabLayout.newTab().setText("Collection"));
//        tablayput綁定viewpager
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(tabviewPager));
        setupViewPager(tabviewPager);
        addNewPost = (ImageButton)rootview.findViewById(R.id.btaddNewPost);
        map = (ImageButton)rootview.findViewById(R.id.btMap);
        chart =(ImageButton) rootview.findViewById(R.id.btChart);
        snapshot = (ImageButton)rootview.findViewById(R.id.snapshot);



    }


    private void setupViewPager(ViewPager viewPager) {
        TabViewPagerAdapter adapter = new TabViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new Ｍypage_tab_post_Fragment(), "Post");
        adapter.addFragment(new Mypage_tab_colec_Fragment(), "Collection");
        viewPager.setAdapter(adapter);
    }




    public class TabViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> tabfragments = new ArrayList<>();
        private final List<String> tabfragmentstext = new ArrayList<>();
        private String tabTitles[] = new String[]{"Post", "Collection"};
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

        public void addFragment(Fragment fragment, String title) {
            tabfragments.add(fragment);
            tabfragmentstext.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }
}
