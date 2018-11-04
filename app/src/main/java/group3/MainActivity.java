package group3;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;


import com.cp102group3maple.violethsu.maple.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import group3.explore.ExploreFragment;

import group3.friend.FriendsListFragment;
import group3.mypage.MypageFragment;
import group3.mypage.Mymap_fragment;

// need to debug mypage頁面一開始沒有tablayout
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ViewPager viewPager;
    private MainActivity mainActivity;

    private int memberId = 0;
    private int vipStatus;
    private String email, passWord, userName;
    private SharedPreferences userPref;
    private Map<String,?> userList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences pref = getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
        memberId = Integer.parseInt(pref.getString("MemberId", ""));
        Log.d(TAG,"test"+memberId);

//        if (userList.containsKey("user"+memberId)) {
//            // TODO: 2018/9/14 呼叫自動登陸List 頁面
//        } else {
//            // TODO: 2018/9/14 呼叫loginActivity
//        }

        viewPager = findViewById(R.id.viewPager);
//      避免view重複加載
        viewPager.setOffscreenPageLimit(4);
        setupViewPager(viewPager);
        final BottomNavigationView navigation =findViewById(R.id.navigation);
        BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
                = new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.navigation_mypage:
                        getSupportActionBar().show();
                        viewPager.setCurrentItem(0);
                        return true;

                    case R.id.navigation_explore:
                        getSupportActionBar().hide();
                        viewPager.setCurrentItem(1);

                        return true;
                    //點擊下方朋友選單時，跑出朋友清單
                    case R.id.navigation_friends:
                        getSupportActionBar().show();
                        viewPager.setCurrentItem(2);
                        return true;

                    case R.id.navigation_map:
                        getSupportActionBar().show();
                        viewPager.setCurrentItem(3);
                        return true;
                }
                return false;
            }
        };

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        navigation.setSelectedItemId(R.id.navigation_mypage);
                        break;

                    case 1:
                        navigation.setSelectedItemId(R.id.navigation_explore);
                        break;

                    case 2:
                        navigation.setSelectedItemId(R.id.navigation_friends);
                        break;

                    case 3:
                        navigation.setSelectedItemId(R.id.navigation_map);
                        break;
//                        default:
//                            navigation.setSelectedItemId(R.id.navigation_mypage);
//                            initContent();
//                            break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        BottomNavPagerAdapter adapter = new BottomNavPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MypageFragment());
        adapter.addFragment(new ExploreFragment());
        adapter.addFragment(new FriendsListFragment());
        adapter.addFragment(new Mymap_fragment());
        viewPager.setAdapter(adapter);
    }
    public class BottomNavPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mainFragmentList = new ArrayList<>();

        public BottomNavPagerAdapter(FragmentManager manager) {
            super(manager);
        }
        @Override
        public Fragment getItem(int position) {
            return mainFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mainFragmentList.size();
        }

        public void addFragment(Fragment fragment) {
            mainFragmentList.add(fragment);
        }
    }
    @Override
    protected void onRestart() {
        super.onRestart();

        int id = getIntent().getIntExtra("id", 0);
        if (id == 1) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mypagetest, new MypageFragment())
                    .addToBackStack(null)
                    .commit();
        }

    }
}
