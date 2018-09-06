package group3.friend;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.violethsu.maple.R;
import java.util.ArrayList;
import java.util.List;

import group3.friend.Friend;

import static com.example.violethsu.maple.R.layout.activity_match;


public class Match extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Friend> mfriendList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_match);
        setTitle(R.string.textTitle_Match);
        handleViews();
    }

    private void handleViews() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(1,
                        StaggeredGridLayoutManager.HORIZONTAL));
        mfriendList = getmfriendList();
        recyclerView.setAdapter(new matchAdapter(this, mfriendList));
        recyclerView.setOnFlingListener(null);
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(recyclerView);
    }

    public void onLikeClick(View view) {
        int j=mfriendList.size();
        for(int i=0;i<j-1;i++){
        recyclerView.smoothScrollToPosition(i);
        break;
        }
    }

    public void onRejectClick(View view) {
        recyclerView.smoothScrollToPosition(mfriendList.size() - 1);
    }

    private class matchAdapter extends
            RecyclerView.Adapter<matchAdapter.MyViewHolder> {
        private Context context;
        private List<Friend> mfriendList;

        matchAdapter(Context context, List<Friend> mfriendList) {
            this.context = context;
            this.mfriendList = mfriendList;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView tvIntro, tvName;

            MyViewHolder(View matchView) {
                super(matchView);
                imageView = matchView.findViewById(R.id.imageView);
                tvIntro = matchView.findViewById(R.id.tvIntro);
                tvName = matchView.findViewById(R.id.tvName);
            }
        }
        @Override
        public int getItemCount() {
            return mfriendList.size();
        }
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
          View matchView = LayoutInflater.from(context).
                   inflate(R.layout.matchitem, viewGroup, false);
            return new MyViewHolder(matchView);
        }
        @Override
        public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int position) {
            final Friend friends = mfriendList.get(position);
            viewHolder.imageView.setImageResource(friends.getImage());
            viewHolder.tvIntro.setText(String.valueOf(friends.getIntro()));
            viewHolder.tvName.setText(friends.getName());
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }
   public List<Friend> getmfriendList() {
        List<Friend> mfriendList = new ArrayList<>();
        mfriendList.add(new Friend(R.drawable.p01, "Hello","Hao"));
        mfriendList.add(new Friend( R.drawable.p02, "Hi","Jack"));
        mfriendList.add(new Friend( R.drawable.p03, "olá","Mark"));
        mfriendList.add(new Friend(R.drawable.p04,"hallo" ,"Ben"));
        mfriendList.add(new Friend(R.drawable.p05,"hola" ,"James"));
        mfriendList.add(new Friend(R.drawable.p06,"алло" ,"David"));
        mfriendList.add(new Friend(R.drawable.p07, "hallom","Ken"));
        mfriendList.add(new Friend(R.drawable.p08, "おはようございます","Ron"));
        mfriendList.add(new Friend( R.drawable.p09,"salut" ,"Jerry"));
        mfriendList.add(new Friend(R.drawable.p10, "buenos días","Maggie"));
        mfriendList.add(new Friend(R.drawable.p11,"مرحبً " ,"Sue"));
        mfriendList.add(new Friend(R.drawable.p12, "여보세요","Cathy"));
        return mfriendList;
    }
}
