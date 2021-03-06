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
import android.view.View;
import android.widget.Toast;

import com.cp102group3maple.violethsu.maple.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import group3.explore.ExploreFragment;

import group3.friend.FriendFragment;
import group3.mypage.MypageFragment;
import group3.mypage.Mymap_fragment;
import group3.friend.Chat.SocketCommon;
import group3.mypage.CommonTask;
import group3.mypage.User_Profile;

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
        userAccountPrepare();
        SharedPreferences pref = getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
        memberId = Integer.parseInt(pref.getString("MemberId", ""));
        Log.d(TAG,"test"+memberId);

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
        adapter.addFragment(new FriendFragment());
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


    @Override
    protected void onDestroy() {
        SocketCommon.disconnectServer();
        super.onDestroy();
    }


    public void userAccountPrepare() {
        memberId = Integer.parseInt(getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE)
                .getString("MemberId", ""));

        if (Common.networkConnected(this)) {
            String userUrl = Common.URL + "/User_profileServlet";
            User_Profile userProfiles = null;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findById");
            jsonObject.addProperty("memberId", this.memberId);
            String jsonOut = jsonObject.toString();
            CommonTask getProfileTask = new CommonTask(userUrl, jsonOut);
            Log.d(TAG, jsonOut);
            try {
                String jsonIn = getProfileTask.execute().get();
                Log.d(TAG, jsonIn);
                userProfiles = new Gson().fromJson(jsonIn, User_Profile.class);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (userProfiles == null) {
                Toast.makeText(this, "no_profile", Toast.LENGTH_SHORT).show();
            } else {
                SharedPreferences preferences = getSharedPreferences(
                        "userAccountDetail", MODE_PRIVATE);
                preferences.edit()
                        .putString("userEmail", userProfiles.getEmail())
                        .putString("userPassword", userProfiles.getPassword())
                        .putString("userName", userProfiles.getUserName())
                        .putString("userMemberId", String.valueOf(userProfiles.getMemberId()))
                        .putString("userVipStatus", String.valueOf(userProfiles.getVipStatus()))
                        .apply();
            }
        }
    }
}
