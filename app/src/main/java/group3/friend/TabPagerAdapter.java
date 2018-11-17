package group3.friend;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class TabPagerAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> tabfragments = new ArrayList<>();
    private List<String> tabfragmentstext = new ArrayList<>();
    private String tabTitles[] = new String[]{"好友清單","交友邀請","朋友配對"};

    public TabPagerAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return tabfragments.get(0);
            case 1:
                return tabfragments.get(1);
            case 2:
                return tabfragments.get(2);
        }
        return null;
    }
    @Override
    public int getCount() {
        return tabfragments.size();
    }
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    public void addFragment(Fragment fragment, String title) {
        tabfragments.add(fragment);
        tabfragmentstext.add(title);
    }

}

