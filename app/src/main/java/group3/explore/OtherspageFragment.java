package group3.explore;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cp102group3maple.violethsu.maple.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import group3.Common;
import group3.Picture;
import group3.Postdetail;
import group3.mypage.CommonTask;
import group3.mypage.ImageTask;
import group3.mypage.Mypage_tab_colec_Fragment;
import group3.mypage.User_Profile;
import group3.mypage.Mypage_tab_post_Fragment;

import static android.content.Context.MODE_PRIVATE;
import static android.support.constraint.motion.MotionScene.TAG;
import static com.cp102group3maple.violethsu.maple.R.color.colorAccent;
import static com.cp102group3maple.violethsu.maple.R.color.colorRed;

public class OtherspageFragment extends Fragment {
    private static final String TAG = "OtherspageFragment";
    private ViewPager tabviewPager;
    private TabLayout tabLayout;
    private String username;
    private int memberid;
    private User_Profile user_Profile;
    private CommonTask otherprofileTask;
    private ImageTask iconTask;
    public Bundle bundlefortab;
    private SharedPreferences pref;
    private int userid;

    public OtherspageFragment(){};
    private TextView tvName,tvSelf,tvemail,tvvip;
    public  TextView tvpostcount,tvcollectcount;
    private Bundle  bundle;
    private ImageView imageView;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootview = inflater.inflate(R.layout.fragment_otherspage, container, false);
        pref = getActivity().getSharedPreferences(Common.PREF_FILE,
                MODE_PRIVATE);
        String smemberId = pref.getString("MemberId", "");
        userid=Integer.parseInt(smemberId);
        bundle=getArguments();
        username=bundle.getString("username");
        memberid=bundle.getInt("memberid");
        bundlefortab=new Bundle();
        bundlefortab.putInt("memberid",memberid);
        handleviews(rootview);
        getprofile();
        Toolbar toolbar = (Toolbar) rootview.findViewById(R.id.toolbar);
        toolbar.setTitle(username);
        tabLayout = rootview.findViewById(R.id.tablayout1);
        tabLayout.setupWithViewPager(tabviewPager);
        tabviewPager= rootview.findViewById(R.id.tabviewPager1);
        tabLayout.addTab(tabLayout.newTab().setText("貼文"));
        tabLayout.addTab(tabLayout.newTab().setText("收藏"));
//        tablayput綁定viewpager
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(tabviewPager));
        setupViewPager(tabviewPager);
        return rootview;
    }

    private void handleviews(View rootview) {
//        tvName=rootview.findViewById(R.id.tvotherName);
        tvSelf=rootview.findViewById(R.id.tvotherselfintro);
        tvemail=rootview.findViewById(R.id.tvemail);
        tvvip=rootview.findViewById(R.id.tvvipicon);
        imageView=rootview.findViewById(R.id.ivpersonicon);
        tvpostcount=rootview.findViewById(R.id.postcount);
        tvcollectcount=rootview.findViewById(R.id.collectorcount);
    }

    private void setupViewPager(ViewPager viewPager) {
        TabViewPagerAdapter adapter = new TabViewPagerAdapter(getChildFragmentManager());
        TabFragment_Card tabFragment_card=new TabFragment_Card();
        TabFragment_Collect tabFragment_collect=new TabFragment_Collect();
        tabFragment_card.setArguments(bundlefortab);
        tabFragment_collect.setArguments(bundlefortab);
        adapter.addFragment(tabFragment_card, "貼文");
        adapter.addFragment(tabFragment_collect, "收藏");
        viewPager.setAdapter(adapter);
    }

    public class TabViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> tabfragments = new ArrayList<>();
        private final List<String> tabfragmentstext = new ArrayList<>();
        private String tabTitles[] = new String[]{"貼文", "收藏"};
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

    private void getprofile() {
        if (Common.networkConnected(getActivity())) {
            String url = Common.URL + "/User_profileServlet";
            user_Profile=null;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findotherById");
            jsonObject.addProperty("memberId", memberid);
            jsonObject.addProperty("userid",userid);
            String jsonOut = jsonObject.toString();
            otherprofileTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = otherprofileTask.execute().get();
                user_Profile = new Gson().fromJson(jsonIn,User_Profile.class);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (user_Profile == null) {
                Toast.makeText(getActivity(),"貼文不存在",Toast.LENGTH_SHORT).show();
            }
            else {
//                tvName.setText(username);
                tvSelf.setText(user_Profile.getSelfIntroduction());
                tvpostcount.setText(String.valueOf(user_Profile.getPostcount()));
                tvcollectcount.setText(String.valueOf(user_Profile.getCollectcount()));
                if(user_Profile.getVipStatus()==1) {
                    tvvip.setText("VIP");
                    tvvip.setTextColor(getResources().getColor(R.color.colorRed));
                }
                tvemail.setText(user_Profile.getEmail());
            }
        } else {
            Toast.makeText(getActivity(),"網路連線異常",Toast.LENGTH_SHORT).show();
        }
        String url = Common.URL + "/User_profileServlet";
        int iconimageSize = getResources().getDisplayMetrics().widthPixels/3;
        try {
            iconTask = new ImageTask(url, memberid, iconimageSize,imageView);
            // passing null and calling get() means not to run FindImageByIdTask.onPostExecute()
            iconTask.execute().get();
//              等圖才設定文字 因為只有一張圖 且用點擊
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (otherprofileTask != null) {
            otherprofileTask.cancel(true);
        }
        if (iconTask != null) {
            iconTask.cancel(true);
        }
    }
}
