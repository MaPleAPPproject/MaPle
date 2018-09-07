package group3.friend;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import java.util.List;
import android.content.Context;
import android.support.annotation.NonNull;
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

import java.util.ArrayList;

import group3.explore.Explore_PostActivity;

public class FriendsList extends Fragment {

    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.activity_friends_list,container, false);
        setHasOptionsMenu(true);
        handleViews(view);
        return view;

    }
    private void handleViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(1,
                        StaggeredGridLayoutManager.VERTICAL));
        List<Friend> friendList = getfriendList();
        recyclerView.setAdapter(new friendAdapter(getActivity(), friendList));
    }

    private class friendAdapter extends
            RecyclerView.Adapter<FriendsList.friendAdapter.MyViewHolder> {
        private Context context;
        private List<Friend> friendList;

        friendAdapter(Context context, List<Friend> friendList) {
            this.context = context;
            this.friendList = friendList;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView tvIntro, tvName;
            Button btChat;

            MyViewHolder(View frienditem) {
                super(frienditem);
                imageView = frienditem.findViewById(R.id.imageView);
                tvIntro = frienditem.findViewById(R.id.tvIntro);
                tvName = frienditem.findViewById(R.id.tvName);
                btChat=frienditem.findViewById(R.id.btChat);
            }
        }
        @Override
        public int getItemCount() {
            return friendList.size();
        }
        @NonNull
        @Override
        public FriendsList.friendAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View frienditem = LayoutInflater.from(context).
                    inflate(R.layout.frienditem, viewGroup, false);
            return new FriendsList.friendAdapter.MyViewHolder(frienditem);
        }
        @Override
        public void onBindViewHolder(@NonNull FriendsList.friendAdapter.MyViewHolder viewHolder, int position) {
            final Friend friends = friendList.get(position);
            viewHolder.imageView.setImageResource(friends.getImage());
            viewHolder.tvIntro.setText(String.valueOf(friends.getIntro()));
            viewHolder.tvName.setText(friends.getName());
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity() , Explore_PostActivity.class);
                    startActivity(intent);
                }
            });
            //按下chat按鈕後
            viewHolder.btChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity() , chat.class);
                    startActivity(intent);
                }
            });
        }
    }
    public List<Friend> getfriendList() {
        List<Friend> friendList = new ArrayList<>();
        friendList.add(new Friend(R.drawable.p01, "Hello","Hao"));
        friendList.add(new Friend( R.drawable.p02, "Hi","Jack"));
        friendList.add(new Friend( R.drawable.p03, "olá","Mark"));
        friendList.add(new Friend(R.drawable.p04,"hallo" ,"Ben"));
        friendList.add(new Friend(R.drawable.p05,"hola" ,"James"));
        friendList.add(new Friend(R.drawable.p06,"алло" ,"David"));
        friendList.add(new Friend(R.drawable.p07, "hallom","Ken"));
        friendList.add(new Friend(R.drawable.p08, "おはようございます","Ron"));
        friendList.add(new Friend( R.drawable.p09,"salut" ,"Jerry"));
        friendList.add(new Friend(R.drawable.p10, "buenos días","Maggie"));
        friendList.add(new Friend(R.drawable.p11,"مرحبً " ,"Sue"));
        friendList.add(new Friend(R.drawable.p12, "여보세요","Cathy"));
        return friendList;
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

}
