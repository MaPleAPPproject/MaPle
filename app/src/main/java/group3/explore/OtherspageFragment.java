package group3.explore;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cp102group3maple.violethsu.maple.R;

import java.util.ArrayList;
import java.util.List;

import group3.mypage.Mypage_tab_colec_Fragment;
import group3.mypage.Ｍypage_tab_post_Fragment;

public class OtherspageFragment extends Fragment {
    private ViewPager tabviewPager;
    private TabLayout tabLayout;
    public OtherspageFragment(){};
    private Fragment fragment;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootview = inflater.inflate(R.layout.fragment_otherspage, container, false);
        tabLayout = (TabLayout) rootview.findViewById(R.id.tablayout1);
        tabLayout.setupWithViewPager(tabviewPager);
        tabviewPager= rootview.findViewById(R.id.tabviewPager1);
        tabLayout.addTab(tabLayout.newTab().setText("Grid"));
        tabLayout.addTab(tabLayout.newTab().setText("Card"));
//        tablayput綁定viewpager
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(tabviewPager));
        setupViewPager(tabviewPager);
        return rootview;
    }

    private void setupViewPager(ViewPager viewPager) {
        TabViewPagerAdapter adapter = new TabViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new Ｍypage_tab_post_Fragment(), "Grid");
        adapter.addFragment(new Mypage_tab_colec_Fragment(), "Card");
        viewPager.setAdapter(adapter);
    }

    public class TabViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> tabfragments = new ArrayList<>();
        private final List<String> tabfragmentstext = new ArrayList<>();
        private String tabTitles[] = new String[]{"Grid", "Card"};
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
