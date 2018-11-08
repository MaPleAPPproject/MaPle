package group3.friend;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import static com.cp102group3maple.violethsu.maple.R.layout.activity_match;


public class MatchActivity extends AppCompatActivity {
    private static final String TAG = "MatchActivity";
    private RecyclerView recyclerView;
    private FriendTask friendGetAllTask;
    private FriendImageTask friendImageTask;
    private int memberid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_match);
        setTitle(R.string.textTitle_Match);
        handleViews();
        SharedPreferences pref = getApplication().getSharedPreferences(Common.PREF_FILE,
                MODE_PRIVATE);
        memberid = Integer.valueOf(pref.getString("MemberId",""));
    }

    @Override
    protected void onStart() {
        super.onStart();
        showAllfriends();
    }

    private void handleViews() {
        recyclerView = findViewById(R.id.recyclerView1);
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
            jsonObject.addProperty("action", "getAll");
            //之後要用會員登入的偏好設定取得會員資料
            jsonObject.addProperty("memberid", memberid);
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
                recyclerView.setAdapter(new matchAdapter(this, mfriendsList));
            }
        } else {
            Common.showToast(this, R.string.msg_NoNetwork);
        }
    }

    private class matchAdapter extends RecyclerView.Adapter<matchAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private List<User_Profile> mfriendsList;
        private int imageSize;
        private ImageButton ibLike, ibReject;

        matchAdapter(Context context, List<User_Profile> mfriendsList) {
            this.layoutInflater = LayoutInflater.from(context);
            this.mfriendsList = mfriendsList;
            imageSize = getResources().getDisplayMetrics().widthPixels / 4;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            private ImageView imageView;
            private TextView tvIntro, tvName;

            MyViewHolder(View matchView) {
                super(matchView);
                imageView = matchView.findViewById(R.id.imageView1);
                tvIntro = matchView.findViewById(R.id.tvIntro1);
                tvName = matchView.findViewById(R.id.tvName1);
                ibReject = matchView.findViewById(R.id.ibReject);
                ibLike = matchView.findViewById(R.id.ibLike);
            }
        }

        @Override
        public int getItemCount() {
            return mfriendsList.size();
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View matchView = layoutInflater.inflate(R.layout.matchitem, viewGroup, false);
            return new MyViewHolder(matchView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder viewHolder, final int position) {
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
                    Intent intent = new Intent(MatchActivity.this, Friend_PostActivity.class);
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
                    jsonObject.addProperty("memberid", memberid);
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
                        Toast.makeText(MatchActivity.this, R.string.msg_RejectFriend, Toast.LENGTH_SHORT).show();
                        mfriendsList.remove(position);
                        recyclerView.getAdapter().notifyDataSetChanged();

                    } else {
                        Toast.makeText(MatchActivity.this, R.string.msg_RejectFriendFail, Toast.LENGTH_SHORT).show();
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
                    jsonObject.addProperty("action", "findByIds");
                    jsonObject.addProperty("memberid", memberid);
                    jsonObject.addProperty("friendid", friendid);
                    String jsonOut = jsonObject.toString();
                    Log.d(TAG, jsonOut);
                    friendGetAllTask = new FriendTask(url, jsonOut);
                    int count = 0;
                    try {
                        String jsonIn = friendGetAllTask.execute().get();
                        count = Integer.valueOf(jsonIn);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                    if (count == 0) {
                        Toast.makeText(MatchActivity.this, R.string.msg_LikeFriendFail, Toast.LENGTH_SHORT).show();
                        mfriendsList.remove(position);
                        recyclerView.getAdapter().notifyDataSetChanged();

                    } else {
                        Toast.makeText(MatchActivity.this, R.string.msg_LikeFriend, Toast.LENGTH_SHORT).show();
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

