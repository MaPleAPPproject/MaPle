package group3.friend;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cp102group3maple.violethsu.maple.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class FriendsFragment extends Fragment {

    private static final String TAG = "FriendsActivity";
    private RecyclerView rvFriends;
    private LocalBroadcastManager broadcastManager;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_friends,container, false);
//        handleviews(view);
        return view;
    }

 /*   // 攔截user連線或斷線的Broadcast
    private void registerFriendStateReceiver() {
        FriendsListFragment activity = (FriendsListFragment) getActivity();
        IntentFilter openFilter = new IntentFilter("open");
        IntentFilter closeFilter = new IntentFilter("close");
        FriendStateReceiver friendStateReceiver = new FriendStateReceiver(activity);
        broadcastManager.registerReceiver(friendStateReceiver, openFilter);
        broadcastManager.registerReceiver(friendStateReceiver, closeFilter);
    }
    // 攔截user連線或斷線的broadcast，並在RecyclerView呈現
    private class FriendStateReceiver extends BroadcastReceiver {
        FriendsListFragment activity;
        FriendStateReceiver(FriendsListFragment activity) {
            this.activity = activity;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            StateMessage stateMessage = new Gson().fromJson(message, StateMessage.class);
            String type = stateMessage.getType();
            String friend = stateMessage.getUser();
            String user = activity.getUserName();
            switch (type) {
                // 有user連線
                case "open":
                    // 如果是自己連線
                    if (friend.equals(user)) {
                        // 取得server上的所有user
                        List<String> friendList = new ArrayList<>(stateMessage.getUsers());
                        friendCommon.setFriendList(friendList);
                        // 將自己從聊天清單中移除，否則會看到自己在聊天清單上
                        friendList.remove(user);
                    } else {
                        // 如果其他user連線而且清單上沒有該user，就將該user新增至目前聊天清單上
                        if (!friendCommon.getFriendList().contains(friend)) {
                            friendCommon.getFriendList().add(friend);
                        }
                        activity.showToast(friend + " is online");
                    }
                    // 重刷聊天清單
                    rvFriends.getAdapter().notifyDataSetChanged();
                    break;
                // 有user斷線
                case "close":
                    // 將斷線的user從聊天清單中移除
                    friendCommon.getFriendList().remove(friend);
                    rvFriends.getAdapter().notifyDataSetChanged();
                    activity.showToast(friend + " is offline");
                    break;
            }
            Log.d(TAG, "message: " + message);
            Log.d(TAG, "friendList: " + friendCommon.getFriendList());
        }
    }

    private class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {
        FriendList activity;

        FriendAdapter(FriendList activity) {
            this.activity = activity;
        }

        class FriendViewHolder extends RecyclerView.ViewHolder {
            TextView tvFriendName;

            FriendViewHolder(View itemView) {
                super(itemView);
                tvFriendName = itemView.findViewById(R.id.tvFrinedName);
            }
        }

        @Override
        public int getItemCount() {
            return friendCommon.getFriendList().size();
        }

        @Override
        public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(activity);
            View itemView = layoutInflater.inflate(R.layout.friend_item, parent, false);
            return new FriendViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(FriendViewHolder holder, int position) {
            final String friend = friendCommon.getFriendList().get(position);
            holder.tvFriendName.setText(friend);
            // 點選聊天清單上的user即開啟聊天頁面
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity, ChatActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("friend", friend);
                    intent.putExtras(bundle);
                    activity.startActivity(intent);
                }
            });
        }

    }


*/

}
