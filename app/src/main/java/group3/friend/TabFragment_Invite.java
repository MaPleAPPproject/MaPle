package group3.friend;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
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


public class TabFragment_Invite extends Fragment {
    private static final String TAG = "TabFragment_Invite";
    private FragmentActivity activity;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FriendTask friendGetAllTask;
    private FriendImageTask friendImageTask;
    private int memberid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        SharedPreferences pref = getActivity().getSharedPreferences(Common.PREF_FILE,
                Context.MODE_PRIVATE);
        memberid = Integer.valueOf(pref.getString("MemberId",""));

    }

    @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_tab_invite, container ,false);
        handleViews(view);

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
            return view;
        }
//        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
//        pagerSnapHelper.attachToRecyclerView(recyclerView);
        return view;
    }

    private void handleViews(View view) {
        recyclerView = view.findViewById(R.id.rvInvite);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));

        swipeRefreshLayout = view.findViewById(R.id.swlinvite);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                showInvitefriends();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        showInvitefriends();
    }

    private void showInvitefriends() {
        if (Common.networkConnected(activity)) {
            String url = Common.URL + "/MatchServlet";
            List<User_Profile> mfriendsList = null;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAllinvite");
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
                Common.showToast(activity, R.string.msg_NoMatchFriendsFound);
            } else {
                recyclerView.setAdapter(new TabFragment_Invite.inviteAdapter(activity, mfriendsList));
            }
        } else {
            Common.showToast(activity, R.string.msg_NoNetwork);
        }
    }


    public class inviteAdapter extends RecyclerView.Adapter<TabFragment_Invite.inviteAdapter.MyViewHolder> {
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
        public TabFragment_Invite.inviteAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View inviteView = layoutInflater.inflate(R.layout.item_match_invite, viewGroup, false);
            return new TabFragment_Invite.inviteAdapter.MyViewHolder(inviteView);
        }

        @Override
        public void onBindViewHolder(@NonNull TabFragment_Invite.inviteAdapter.MyViewHolder viewHolder, final int position) {
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
                    Intent intent = new Intent(activity, Friend_PostActivity.class);
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
                        Toast.makeText(activity, R.string.msg_RejectFriend, Toast.LENGTH_SHORT).show();
                        mfriendsList.remove(position);
                        recyclerView.getAdapter().notifyDataSetChanged();

                    } else {
                        Toast.makeText(activity, R.string.msg_RejectFriendFail, Toast.LENGTH_SHORT).show();
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
                    jsonObject.addProperty("friendid", memberid);
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
                        Toast.makeText(activity, R.string.msg_LikeFriendFail, Toast.LENGTH_SHORT).show();
                        mfriendsList.remove(position);
                        recyclerView.getAdapter().notifyDataSetChanged();

                    } else {
                        Toast.makeText(activity, R.string.msg_LikeFriend, Toast.LENGTH_SHORT).show();
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
