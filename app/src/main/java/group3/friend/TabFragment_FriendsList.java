package group3.friend;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.cp102group3maple.violethsu.maple.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import group3.Common;
import group3.friend.Chat.ChatActivity;
import group3.friend.Chat.SocketCommon;
import group3.friend.Chat.StateMessage;
import group3.mypage.User_Profile;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;


public class TabFragment_FriendsList extends Fragment {
    private static final String TAG = "TabFragment_FriendsList";
    private FragmentActivity activity;
    private FriendTask friendGetAllTask;
    private FriendImageTask friendImageTask;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int memberid;
    private String vipStatus;
//    private static List<String> onlineFriendList;
    private LocalBroadcastManager broadcastManager;
    private HashMap<String, String> friendKeyMap = new HashMap<String, String>();



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        activity.setTitle(R.string.textTitle);
        SharedPreferences pref = activity.getSharedPreferences(Common.PREF_FILE,
                MODE_PRIVATE);
        memberid = Integer.valueOf(pref.getString("MemberId", ""));
        SharedPreferences preferences = activity.getSharedPreferences(
                "userAccountDetail", MODE_PRIVATE);
        vipStatus = preferences.getString("userVipStatus", "");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_tab_friendslist, container, false);
        handleViews(view);
        setHasOptionsMenu(true);

        // 初始化LocalBroadcastManager並註冊BroadcastReceiver
        broadcastManager = LocalBroadcastManager.getInstance(activity);
        registerFriendStateReceiver();

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
            return view;
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        showAllfriends();
        String userName = String.valueOf(TabFragment_FriendsList.this.memberid);
        SocketCommon.connectServer(userName, getActivity());
    }

    private void handleViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));

        swipeRefreshLayout = view.findViewById(R.id.swlfreindlist);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                showAllfriends();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void showAllfriends() {
        if (Common.networkConnected(activity)) {
            String url = Common.URL + "/FriendServlet";
            List<User_Profile> friendsList = null;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAll");
            jsonObject.addProperty("memberid", memberid);
            String jsonOut = jsonObject.toString();
            friendGetAllTask = new FriendTask(url, jsonOut);

            try {
                String jsonIn = friendGetAllTask.execute().get();
                Type listType = new TypeToken<List<User_Profile>>() {
                }.getType();
                friendsList = new Gson().fromJson(jsonIn, listType);
                for (User_Profile user : friendsList) {
                    friendKeyMap.put(String.valueOf(user.getMemberId()), user.getUserName());
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (friendsList == null || friendsList.isEmpty()) {
                Common.showToast(activity, R.string.msg_NoFriendsFound);
            } else {
                recyclerView.setAdapter(new friendAdapter(activity, friendsList));
            }
        } else {
            Common.showToast(activity, R.string.msg_NoNetwork);
        }
    }


    // 攔截user連線或斷線的Broadcast
    private void registerFriendStateReceiver() {
        IntentFilter openFilter = new IntentFilter("open");
        IntentFilter closeFilter = new IntentFilter("close");
        FriendStateReceiver friendStateReceiver = new FriendStateReceiver();
        broadcastManager.registerReceiver(friendStateReceiver, openFilter);
        broadcastManager.registerReceiver(friendStateReceiver, closeFilter);
    }

    // 攔截user連線或斷線的broadcast，並在RecyclerView呈現
    private class FriendStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            StateMessage stateMessage = new Gson().fromJson(message, StateMessage.class);
            String type = stateMessage.getType();
            String friend = stateMessage.getUser();
            String friendName;

            if (friendKeyMap != null) {
                friendName = friendKeyMap.get(friend);
            } else {
                showAllfriends();
                friendName = friendKeyMap.get(friend);
            }

            switch (type) {
                // 有user連線
                case "open":
                    // 如果是自己連線
                    List<String> onlineFriendList = new ArrayList<>(stateMessage.getUsers());
                    for (String name : onlineFriendList) {
                        Log.d(TAG, name);
                    }
                    SocketCommon.setonlineFriendList(onlineFriendList);
                    Toast.makeText(activity, friendName + "正在線上", Toast.LENGTH_SHORT).show();

                    // 重刷聊天清單
                    recyclerView.getAdapter().notifyDataSetChanged();
                    break;
                // 有user斷線
                case "close":
                    // 將斷線的user從聊天清單中移除
                    SocketCommon.getonlineFriendList().remove(friend);
                    recyclerView.getAdapter().notifyDataSetChanged();
                    Toast.makeText(activity, friendName + "已經下線囉", Toast.LENGTH_SHORT).show();
                    break;
            }
            Log.d(TAG, "message: " + message);
            Log.d(TAG, "friendList: " + SocketCommon.getonlineFriendList());
        }
    }


    private class friendAdapter extends RecyclerView.Adapter<TabFragment_FriendsList.friendAdapter.MyViewHolder> {
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
            Button btChat,btProfile;


            MyViewHolder(View frienditem) {
                super(frienditem);
                imageView = frienditem.findViewById(R.id.imageView);
                tvName = frienditem.findViewById(R.id.tvName);
//                tvIntro = frienditem.findViewById(R.id.tvIntro);
                btChat = frienditem.findViewById(R.id.btChat);
                btProfile = frienditem.findViewById(R.id.btProfile);
            }
        }

        @Override
        public int getItemCount() {
            return friendsList.size();
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View frienditem = layoutInflater.inflate(R.layout.item_friendlist, viewGroup, false);
            return new TabFragment_FriendsList.friendAdapter.MyViewHolder(frienditem);
        }

        @Override
        public void onBindViewHolder(@NonNull TabFragment_FriendsList.friendAdapter.MyViewHolder viewHolder, int position) {

            final User_Profile friends = friendsList.get(position);
            final String friendName = String.valueOf(friends.getUserName());
            viewHolder.tvName.setText(friendName);
//            viewHolder.tvName.setText(String.valueOf(friends.getUserName()));
//            viewHolder.tvIntro.setText(String.valueOf(friends.getSelfIntroduction()));

            //連線至User_profileServlet端的Servlet
            String url = Common.URL + "/User_profileServlet";
            final int friendid = friends.getMemberId();
            final String friend = String.valueOf(friendid);

            friendImageTask = new FriendImageTask(url, friendid, imageSize, viewHolder.imageView);
            friendImageTask.execute();

            List<String> onlineFriendList = SocketCommon.getonlineFriendList();

            //取消沒在線上的朋友清單的chat按鈕
            if (onlineFriendList == null || vipStatus.equals("0")) {
                viewHolder.btChat.setEnabled(false);
                final int color = getResources().getColor(R.color.colorGray);
                viewHolder.btChat.setBackgroundColor(color);
            } else {
                if (onlineFriendList.contains(friend)) {
                    viewHolder.btChat.setEnabled(true);
                    final int color = getResources().getColor(R.color.colorPrimary);
                    viewHolder.btChat.setBackgroundColor(color);

                } else {
                    viewHolder.btChat.setEnabled(false);
                    final int color = getResources().getColor(R.color.colorGray);
                    viewHolder.btChat.setBackgroundColor(color);
                }
            }


            viewHolder.btProfile.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Bundle bundle = new Bundle();
                    bundle.putString("tvname", friends.getUserName());
                    bundle.putInt("memberid", friends.getMemberId());
                    Intent intent = new Intent(getActivity(), Friend_PostActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });

            viewHolder.btChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    String friendStr = String.valueOf(friend);
                    bundle.putString("friendName", friendName);
                    bundle.putString("friend", friendStr);


                    Log.d(TAG, "friend : " + friend + "friendName : " + friendName);
                    intent.putExtras(bundle);


                    intent.setClass(activity, ChatActivity.class);
                    intent.addFlags(FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
            });
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


}

