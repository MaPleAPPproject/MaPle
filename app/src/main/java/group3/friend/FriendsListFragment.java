package group3.friend;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import java.lang.reflect.Type;
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

import group3.Common;
import group3.mypage.User_Profile;



public class FriendsListFragment extends Fragment {
    private static final String TAG ="FriendsListFragment";
    private FragmentActivity activity;
    private FriendTask friendGetAllTask;
    private FriendImageTask friendImageTask;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity=getActivity();
//      setTitle(R.string.textTitle_Friend);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.activity_friends_list,container, false);
        setHasOptionsMenu(true);
        swipeRefreshLayout =
                view.findViewById(R.id.swiprefreshlayout1);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                showAllfriends();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
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
        if (Common.networkConnected(activity)) {
            String url = Common.URL + "/FriendServlet";
            List<User_Profile> friendsList = null;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAll");
            //之後要用會員登入的偏好設定取得會員資料
            jsonObject.addProperty("memberid", 6);
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
                Common.showToast(activity, R.string.msg_NoFriendsFound);
            } else {
                recyclerView.setAdapter(new friendAdapter(activity, friendsList));
            }
        } else {
            Common.showToast(activity, R.string.msg_NoNetwork);
        }

    }
    private class friendAdapter extends RecyclerView.Adapter<FriendsListFragment.friendAdapter.MyViewHolder> {
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
            return new FriendsListFragment.friendAdapter.MyViewHolder(frienditem);
        }
        @Override
        public void onBindViewHolder(@NonNull FriendsListFragment.friendAdapter.MyViewHolder viewHolder, int position) {

            final User_Profile friends = friendsList.get(position);
            viewHolder.tvName.setText(String.valueOf(friends.getUserName()));
            viewHolder.tvIntro.setText(String.valueOf(friends.getSelfIntroduction()));

            //連線至User_profileServlet端的Servlet
            String url =Common.URL+"/User_profileServlet";
            final int friendid=friends.getMemberId();

            friendImageTask = new FriendImageTask(url,friendid, imageSize, viewHolder.imageView);
            friendImageTask.execute();


            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {

                //點擊之後透friendid轉過MemberId去相對應的個人頁面
                @Override
                public void onClick(View v) {

                    Bundle bundle =new Bundle();
                    bundle.putString("tvname", friends.getUserName());
                    bundle.putInt("memberid", friends.getMemberId());
                    Intent intent =new Intent(getActivity(),Friend_PostActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });

            //按下chat按鈕後
            viewHolder.btChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity() , ChatActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

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

            //這裡要判斷使用者是否為vip
            case R.id.optionmenu_match:
                Intent intentmatch = new Intent();
                intentmatch.setClass(getActivity() , MatchActivity.class);
                startActivity(intentmatch);
                break;
            case R.id.optionmenu_payment:
                Intent intentpayment = new Intent();
                intentpayment.setClass(getActivity() , Payment.class);
                startActivity(intentpayment);
                break;
            case R.id.optionmenu_invite:
                Intent intentinvite = new Intent();
                intentinvite.setClass(getActivity() , InviteActivity.class);
                startActivity(intentinvite);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
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

