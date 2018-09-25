package group3.friend;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import java.lang.reflect.Type;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cp102group3maple.violethsu.maple.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Locale;

import group3.Common;
import group3.MainActivity;
import group3.explore.Explore_PostActivity;
import group3.mypage.MypageFragment;
import group3.mypage.Mypage_Chart_Activity;
import group3.mypage.Mypage_UserProfile_Activity;
import group3.mypage.User_Profile;

import static android.content.Context.MODE_PRIVATE;

public class FriendsList extends Fragment {

    private static final String TAG ="FriendsList";
    private RecyclerView recyclerView;
    private FragmentActivity activity;
    private FriendTask friendGetAllTask;
    private FriendImageTask friendImageTask;
    private SharedPreferences pref;
    private int memberId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity=getActivity();
        pref = getActivity().getSharedPreferences(Common.PREF_FILE,
                MODE_PRIVATE);
        String smemberId = pref.getString("MemberId", "");
        memberId=Integer.parseInt(smemberId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.activity_friends_list,container, false);
        setHasOptionsMenu(true);
        handleViews(view);
        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        showAllfriends();
    }
    private void handleViews(View view) {
       recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
    }

    private void showAllfriends(){
        if (FriendCommon.networkConnected(activity)) {
            String url = FriendCommon.URL + "/FriendServlet";
            List<User_Profile> friendsList = null;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAll");
            jsonObject.addProperty("memberid", memberId);
            String jsonOut = jsonObject.toString();
            friendGetAllTask = new FriendTask(url, jsonOut);

            try {
                String jsonIn = friendGetAllTask.execute().get();
                Type listType = new TypeToken<List<User_Profile>>() {
                }.getType();
                friendsList = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (friendsList == null || friendsList.isEmpty()) {
                FriendCommon.showToast(activity, R.string.msg_NoFriendsFound);
            } else {
                recyclerView.setAdapter(new friendAdapter(activity, friendsList));
            }
        } else {
            FriendCommon.showToast(activity, R.string.msg_NoNetwork);
        }

    }



    private class friendAdapter extends RecyclerView.Adapter<FriendsList.friendAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private List<User_Profile> friendsList;
        private int imageSize;

        friendAdapter(Context context, List<User_Profile> friendsList) {
            this.layoutInflater = LayoutInflater.from(context);
            this.friendsList = friendsList;
            imageSize = getResources().getDisplayMetrics().widthPixels / 4;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView tvIntro, tvName;
            Button btChat;

            MyViewHolder(View frienditem) {
                super(frienditem);
                imageView = frienditem.findViewById(R.id.imageView);
                tvName = frienditem.findViewById(R.id.tvName);
                tvIntro = frienditem.findViewById(R.id.tvIntro);
                btChat=frienditem.findViewById(R.id.btChat);
            }
        }
        @Override
        public int getItemCount() {
            return friendsList.size();
        }
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View frienditem = layoutInflater.inflate(R.layout.frienditem, viewGroup, false);
            return new FriendsList.friendAdapter.MyViewHolder(frienditem);
        }
        @Override
        public void onBindViewHolder(@NonNull FriendsList.friendAdapter.MyViewHolder viewHolder, int position) {
            final User_Profile friends = friendsList.get(position);
            viewHolder.tvName.setText(String.valueOf(friends.getUserName()));
            viewHolder.tvIntro.setText(String.valueOf(friends.getSelfIntroduction()));

            //連線至User_profileServlet端的Servlet
            String url =FriendCommon.URL+"/User_profileServlet";
            final int friendid=friends.getMemberId();
            friendImageTask = new FriendImageTask(url,friendid, imageSize, viewHolder.imageView);
            friendImageTask.execute();

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    Bundle bundle=new Bundle();
                    bundle.putString("username",friends.getUserName());
                    bundle.putInt("memberid", friends.getMemberId());
                    intent.putExtras(bundle);
                    intent.setClass(getActivity() , Friend_PostActivity.class);
                    startActivity(intent);
                }
            });
            //按下chat按鈕後
            viewHolder.btChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity() , MainActivity.class);
                    startActivity(intent);
                }
            });
        }
    }
    /*假資料
    public List<Friend> getfriendsList() {
        List<Friend> friendsList = new ArrayList<>();
        friendsList.add(new Friend(R.drawable.p01, "Hello","Hao"));
        friendsList.add(new Friend( R.drawable.p02, "Hi","Jack"));
        friendsList.add(new Friend( R.drawable.p03, "olá","Mark"));
        friendsList.add(new Friend(R.drawable.p04,"hallo" ,"Ben"));
        friendsList.add(new Friend(R.drawable.p05,"hola" ,"James"));
        friendsList.add(new Friend(R.drawable.p06,"алло" ,"David"));
        friendsList.add(new Friend(R.drawable.p07, "hallom","Ken"));
        friendsList.add(new Friend(R.drawable.p08, "おはようございます","Ron"));
        friendsList.add(new Friend( R.drawable.p09,"salut" ,"Jerry"));
        friendsList.add(new Friend(R.drawable.p10, "buenos días","Maggie"));
        friendsList.add(new Friend(R.drawable.p11,"مرحبً " ,"Sue"));
        friendsList.add(new Friend(R.drawable.p12, "여보세요","Cathy"));
        return friendsList;
    }*/

    //右上方的選單按鈕
    //vio:改成fragment的onCreateOptionMenu
    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.fl_option, menu);
        super.onCreateOptionsMenu(menu,menuInflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.optionmenu_match:
                Intent intentmatch = new Intent();
                intentmatch.setClass(getActivity() , Match.class);
                startActivity(intentmatch);
                break;
            case R.id.optionmenu_payment:
                Intent intentpayment = new Intent();
                intentpayment.setClass(getActivity() , Payment.class);
                startActivity(intentpayment);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void changeFragment(Fragment fragment) {
        if (getFragmentManager() != null) {
            getFragmentManager().beginTransaction().
                    replace(R.id.content, fragment).addToBackStack(null).commit();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (friendGetAllTask != null) {
            friendGetAllTask.cancel(true);
            friendGetAllTask = null;
        }

        if (friendImageTask != null) {
            friendImageTask.cancel(true);
            friendImageTask = null;
        }
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden){
            //Fragment隐藏时调用
//            onResume();
        }else {
            //Fragment显示时调用
//            onPause();
        }
    }

}
