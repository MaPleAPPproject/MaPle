package group3.friend;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cp102group3maple.violethsu.maple.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import group3.Common;
import group3.mypage.User_Profile;
import static com.cp102group3maple.violethsu.maple.R.layout.activity_invite;
import com.cp102group3maple.violethsu.maple.R;

public class InviteActivity extends AppCompatActivity {
    private static final String TAG = "InviteActivity";
    private RecyclerView recyclerView;
    private FriendTask friendGetAllTask;
    private FriendImageTask friendImageTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        setTitle(R.string.textTitle_Invite);
        handleViews();
    }

    @Override
    protected void onStart() {
        super.onStart();
        showAllfriends();
    }

    private void handleViews() {
        recyclerView = findViewById(R.id.recyclerView2);
        recyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(1,
                        StaggeredGridLayoutManager.HORIZONTAL));
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(recyclerView);
    }

    private void showAllfriends() {
        if (Common.networkConnected(this)) {
            String url = Common.URL + "/MatchServlet";
            List<User_Profile> mfriendsList = null;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAllinvite");
            //之後要用會員登入的偏好設定取得會員資料
            jsonObject.addProperty("memberid", 6);
            String jsonOut = jsonObject.toString();
            friendGetAllTask = new FriendTask(url, jsonOut);
            try {
                String jsonIn = friendGetAllTask.execute().get();
                Type listType = new TypeToken<List<User_Profile>>() {
                }.getType();
                mfriendsList = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (mfriendsList == null || mfriendsList.isEmpty()) {
                Common.showToast(this, R.string.msg_NoMatchFriendsFound);
            } else {
                recyclerView.setAdapter(new inviteAdapter(this, mfriendsList));
            }
        } else {
            Common.showToast(this, R.string.msg_NoNetwork);
        }
    }

    private class inviteAdapter extends RecyclerView.Adapter<InviteActivity.inviteAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private List<User_Profile> mfriendsList;
        private int imageSize;
        private ImageButton ibLike, ibReject;

        inviteAdapter(Context context, List<User_Profile> mfriendsList) {
            this.layoutInflater = LayoutInflater.from(context);
            this.mfriendsList = mfriendsList;
            imageSize = getResources().getDisplayMetrics().widthPixels / 4;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            private ImageView imageView;
            private TextView tvIntro, tvName;

            MyViewHolder(View inviteView) {
                super(inviteView);
                imageView = inviteView.findViewById(R.id.imageView1);
                tvIntro = inviteView.findViewById(R.id.tvIntro1);
                tvName = inviteView.findViewById(R.id.tvName1);
                ibReject = inviteView.findViewById(R.id.ibReject);
                ibLike = inviteView.findViewById(R.id.ibLike);
            }
        }

        @Override
        public int getItemCount() {
            return mfriendsList.size();
        }

        @NonNull
        @Override
        public InviteActivity.inviteAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View inviteView = layoutInflater.inflate(R.layout.matchitem, viewGroup, false);
            return new InviteActivity.inviteAdapter.MyViewHolder(inviteView);
        }

        @Override
        public void onBindViewHolder(@NonNull InviteActivity.inviteAdapter.MyViewHolder viewHolder, final int position) {
            final User_Profile friends = mfriendsList.get(position);
            //連線至User_profileServlet端的Servlet
            String url = Common.URL + "/User_profileServlet";
            final int friendid = friends.getMemberId();
            viewHolder.tvName.setText(String.valueOf(friends.getUserName()));
            viewHolder.tvIntro.setText(String.valueOf(friends.getSelfIntroduction()));

            friendImageTask = new FriendImageTask(url, friendid, imageSize, viewHolder.imageView);
            friendImageTask.execute();

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("tvname", friends.getUserName());
                    bundle.putInt("memberid", friends.getMemberId());
                    Intent intent = new Intent(InviteActivity.this, Friend_PostActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });

            ibReject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String url = Common.URL + "/MatchServlet";
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "friendReject");
                    jsonObject.addProperty("memberid", 6);
                    jsonObject.addProperty("friendid", friendid);
                    String jsonOut = jsonObject.toString();
                    friendGetAllTask = new FriendTask(url, jsonOut);
                    int count = 0;
                    try {
                        String jsonIn = friendGetAllTask.execute().get();
                        count = Integer.valueOf(jsonIn);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                    if (count == 1) {
                        Toast.makeText(InviteActivity.this, R.string.msg_RejectFriend, Toast.LENGTH_SHORT).show();
                        mfriendsList.remove(position);
                        recyclerView.getAdapter().notifyDataSetChanged();

                    } else {
                        Toast.makeText(InviteActivity.this, R.string.msg_RejectFriendFail, Toast.LENGTH_SHORT).show();
                        mfriendsList.remove(position);
                        recyclerView.getAdapter().notifyDataSetChanged();
                    }
                }
            });


            ibLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String url = Common.URL + "/MatchServlet";
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "findByIds2");
                    jsonObject.addProperty("memberid", friendid);
                    jsonObject.addProperty("friendid", 6);
                    String jsonOut = jsonObject.toString();
                    friendGetAllTask = new FriendTask(url, jsonOut);
                    int count = 0;
                    try {
                        String jsonIn = friendGetAllTask.execute().get();
                        count = Integer.valueOf(jsonIn);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                    if (count == 0) {
                        Toast.makeText(InviteActivity.this, R.string.msg_LikeFriendFail, Toast.LENGTH_SHORT).show();
                        mfriendsList.remove(position);
                        recyclerView.getAdapter().notifyDataSetChanged();

                    } else {
                        Toast.makeText(InviteActivity.this, R.string.msg_LikeFriend, Toast.LENGTH_SHORT).show();
                        mfriendsList.remove(position);
                        recyclerView.getAdapter().notifyDataSetChanged();
                    }
                }

            });
            if (mfriendsList.size() == 0) {

            }
        }
    }
}

