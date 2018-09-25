package group3.explore;

import android.graphics.Color;
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

import static android.support.constraint.motion.MotionScene.TAG;
import static com.cp102group3maple.violethsu.maple.R.color.colorRed;

public class OtherspageFragment extends Fragment {
    private static final String TAG = "OtherspageFragment";
    private ViewPager tabviewPager;
    private TabLayout tabLayout;
    private String username,email,vipstate;
    private int memberid;
    private CommonTask picturefindbyidTask;
    private User_Profile user_Profile;
    private CommonTask otherprofileTask;
    private ImageTask iconTask;
    public Bundle bundlefortab;

    public OtherspageFragment(){};
    private Fragment fragment;
    private TextView tvName,tvSelf,tvemail,tvvip;
    private Bundle  bundle;
    private ImageView imageView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootview = inflater.inflate(R.layout.fragment_otherspage, container, false);
        bundle=getArguments();
        username=bundle.getString("username");
        memberid=bundle.getInt("memberid");
        bundlefortab=new Bundle();
        bundlefortab.putInt("memberid",memberid);
        handleviews(rootview);
        getprofile();
        tabLayout = rootview.findViewById(R.id.tablayout1);
        tabLayout.setupWithViewPager(tabviewPager);
        tabviewPager= rootview.findViewById(R.id.tabviewPager1);
        tabLayout.addTab(tabLayout.newTab().setText("Post"));
        tabLayout.addTab(tabLayout.newTab().setText("Collection"));
//        tablayput綁定viewpager
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(tabviewPager));
        setupViewPager(tabviewPager);
        return rootview;
    }

    private void handleviews(View rootview) {
        tvName=rootview.findViewById(R.id.tvotherName);
        tvSelf=rootview.findViewById(R.id.tvotherselfintro);
        tvemail=rootview.findViewById(R.id.tvemail);
        tvvip=rootview.findViewById(R.id.tvvipicon);
        imageView=rootview.findViewById(R.id.ivpersonicon);
//        if(bundle !=null){
//        }else {
//            Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
//        }
    }

    private void setupViewPager(ViewPager viewPager) {
        TabViewPagerAdapter adapter = new TabViewPagerAdapter(getChildFragmentManager());
        TabFragment_Card tabFragment_card=new TabFragment_Card();
        TabFragment_Collect tabFragment_collect=new TabFragment_Collect();
        tabFragment_card.setArguments(bundlefortab);
        tabFragment_collect.setArguments(bundlefortab);
        adapter.addFragment(tabFragment_card, "Post");
        adapter.addFragment(tabFragment_collect, "Collection");
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
//            return tabfragments.get(position);
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

//    @Override
//    public void onStart() {
//        super.onStart();
//        getprofile();
//    }
//  取他人資料
    private void getprofile() {
        if (Common.networkConnected(getActivity())) {
            String url = Common.URL + "/User_profileServlet";
            user_Profile=null;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findotherById");
            jsonObject.addProperty("memberId", memberid);
            String jsonOut = jsonObject.toString();
            otherprofileTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = otherprofileTask.execute().get();
                user_Profile = new Gson().fromJson(jsonIn,User_Profile.class);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (user_Profile == null) {
                Toast.makeText(getActivity(),R.string.msg_NoPost,Toast.LENGTH_SHORT).show();
            }
            else {
                tvName.setText(username);
                tvSelf.setText(user_Profile.getSelfIntroduction());
//                tvName.setText(user_Profile.getUserName());
                if(user_Profile.getVipStatus()==1) {
                    tvvip.setText("VIP");
                    tvvip.setTextColor(getResources().getColor(R.color.colorRed));
                }
                tvemail.setText(user_Profile.getEmail());
            }
        } else {
            Toast.makeText(getActivity(),R.string.msg_Nonetwork,Toast.LENGTH_SHORT).show();
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
