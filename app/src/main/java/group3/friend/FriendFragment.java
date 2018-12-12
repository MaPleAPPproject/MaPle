package group3.friend;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.cp102group3maple.violethsu.maple.R;

import java.util.HashMap;

import group3.Common;
import group3.Login;

import group3.friend.Chat.SocketCommon;
import kale.ui.view.dialog.EasyDialog;

import static android.content.Context.MODE_PRIVATE;


public class FriendFragment extends Fragment {
    private static final String TAG = "FriendFragment";
    private Activity activity;
    private int memberid;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    public static int lastindex = 0;
    private String vipStatus;
    private Payment payment;
    private EasyDialog easyDialog;
    private HashMap<String, String> friendKeyMap = new HashMap<String, String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        SharedPreferences pref = activity.getSharedPreferences(Common.PREF_FILE,
                MODE_PRIVATE);
        memberid = Integer.valueOf(pref.getString("MemberId", ""));

        SharedPreferences preferences = getActivity().getSharedPreferences(
                "userAccountDetail", MODE_PRIVATE);
        vipStatus = preferences.getString("userVipStatus", "");
        this.activity = this.getActivity();
        payment = new Payment(this.getContext(), this.activity, memberid);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_friend,container, false);
        handleviews(view);

        return view;
    }

    private void handleviews(View view) {
        tabLayout = view.findViewById(R.id.fTabLayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setBackgroundResource(R.color.colorBackground);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#1ea3c1"));
        tabLayout.setTabTextColors(Color.parseColor("#707070"), Color.parseColor("#007491"));
        viewPager = view.findViewById(R.id.fViewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                lastindex = i;

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        tabLayout.addTab(tabLayout.newTab().setText("好友清單"));
        tabLayout.addTab(tabLayout.newTab().setText("交友邀請"));
        tabLayout.addTab(tabLayout.newTab().setText("朋友配對"));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        setUpViewPager(viewPager);
    }

    private void setUpViewPager(ViewPager viewPager) {
        TabPagerAdapter adapter = new TabPagerAdapter(getChildFragmentManager());
        TabFragment_FriendsList tabFriendsList = new TabFragment_FriendsList();
        TabFragment_Invite tabInvite = new TabFragment_Invite();
        TabFragment_Match tabMatch = new TabFragment_Match();
        adapter.addFragment(tabFriendsList, "好友清單");
        adapter.addFragment(tabInvite, "交友邀請");
        adapter.addFragment(tabMatch, "朋友配對");

        viewPager.setAdapter(adapter);
    }

    //右上方的選單按鈕
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.friend_optionmenu, menu);

        if (payment.vipStatus == 1) {
            MenuItem menuItem = menu.findItem(R.id.optionmenu_payment);
            if (menuItem != null) {
                menuItem.setVisible(false);
            } else {
                Log.w(TAG, "matchbtPremium is null");
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.optionmenu_payment:
                paymentDialog(getContext());
                break;

            case R.id.optionmenu_logout:
            SharedPreferences pref = activity.getSharedPreferences(Common.PREF_FILE,
                    MODE_PRIVATE);
            pref.edit()
                    .putString("MemberId","")
                    .apply();
                Intent intentLogout = new Intent();
                intentLogout.setClass(activity, Login.class);
                SocketCommon.disconnectServer();
                startActivity(intentLogout);
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void paymentDialog(final Context context) {
        final android.support.v4.app.FragmentTransaction ft =
                getFragmentManager().beginTransaction();


        EasyDialog.Builder builder = EasyDialog.builder(context);
        DialogInterface.OnClickListener OkClick = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                payment.pay();
                
            }
        };
        builder.setTitle(R.string.paymentAlertTitle)
                .setIcon(R.drawable.googlepay)
                .setMessage(R.string.paymentAgreement)
                .setOnCancelListener(null)
                .setOnDismissListener(null)

                .setPositiveButton("同意", OkClick)
                .setNegativeButton("取消", null)
//                .setNeutralButton("ignore", this)
                .setCancelable(true);

        easyDialog = builder.build();
        easyDialog.show(getFragmentManager());
    }




}
