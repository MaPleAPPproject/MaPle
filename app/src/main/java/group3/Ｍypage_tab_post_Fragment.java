package group3;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.violethsu.maple.R;

import java.util.ArrayList;
import java.util.List;

public class Ｍypage_tab_post_Fragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.tab_post, container, false);
        handleviews(view);
        return view;

    }

    private void handleviews(View view) {
        RecyclerView rvPost = view.findViewById(R.id.rvPost);
        rvPost.setLayoutManager(new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL));
        rvPost.setAdapter(new Ｍypage_tab_post_Fragment.PostAdapter(getPosts(), getActivity()));
    }

    public class PostAdapter extends RecyclerView.Adapter<Ｍypage_tab_post_Fragment.PostAdapter.MyViewHolder> {
        List<Post> posts;
        Context context;
        public PostAdapter(List<Post> posts, Context context) {
            this.posts=posts;
            this.context=context;

        }
        @Override
        public int getItemCount() {
            return posts.size();
        }
        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView=itemView.findViewById(R.id.ivtop);
            }
        }
        @NonNull
        @Override
        public Ｍypage_tab_post_Fragment.PostAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View item_view = layoutInflater.inflate(R.layout.item_view, parent, false);
            return new Ｍypage_tab_post_Fragment.PostAdapter.MyViewHolder(item_view);
        }


        @Override
        public void onBindViewHolder(@NonNull Ｍypage_tab_post_Fragment.PostAdapter.MyViewHolder myViewHolder, int position) {
            final Post post = posts.get(position);
            myViewHolder.imageView.setImageResource(post.getImageId());
            myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent();
//                    intent.setClass(context, Explore_PA_PostFragment.class);
//                    startActivity(intent);
                    //        /* 取得Bundle物件 */
//                    int programming = Integer.parseInt(etProgramming.getText().toString());
//                    Bundle bundle = new Bundle();
//                    Post post1 = new Post(programming, dataStructure, algorithm);
//                    //轉成物件傳入(key,object)
//                    bundle.putSerializable("score", score);
//                    ResultFragment resultFragment = new ResultFragment();
//                    /* 將Bundle資料轉給resultFragment */
//                    resultFragment.setArguments(bundle);
//                    if(getFragmentManager()==null){
//                        return;

                }
            });

        }
    }
    private List<Post> getPosts() {
//        next step:add all information
        List<Post> posts = new ArrayList<>();
        posts.add(new Post(R.drawable.post7));
        posts.add(new Post(R.drawable.post8));
        posts.add(new Post(R.drawable.post9));
        posts.add(new Post(R.drawable.post10));
        posts.add(new Post(R.drawable.post11));
        posts.add(new Post(R.drawable.post12));
        return posts;
    }
}

