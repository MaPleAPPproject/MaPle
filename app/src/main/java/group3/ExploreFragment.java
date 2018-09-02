package group3;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.violethsu.maple.R;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ValidFragment")
public class ExploreFragment extends Fragment {
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mainActivity=(MainActivity) getActivity();
//    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search, menu);
        SearchView searchView = (SearchView)menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener((SearchView.OnQueryTextListener) queryListener);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_exploretest,container, false);
        handleviews(view);
        return view;

    }

    private void handleviews(View view) {
        RecyclerView rvTop = view.findViewById(R.id.rvTop);
        rvTop.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false) );
        rvTop.setAdapter(new ExploreFragment.PostAdapter(getPosts(), getActivity()));
        RecyclerView rvRecom = view.findViewById(R.id.rvRecom);
        rvRecom.setLayoutManager(new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL));
        rvRecom.setAdapter(new ExploreFragment.PostAdapter(getPosts(), getActivity()));
        SearchView searchView=view.findViewById(R.id.searchview);
    }

    public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {
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
        public ExploreFragment.PostAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View item_view = layoutInflater.inflate(R.layout.item_view, parent, false);
            return new ExploreFragment.PostAdapter.MyViewHolder(item_view);
        }


        @Override
        public void onBindViewHolder(@NonNull ExploreFragment.PostAdapter.MyViewHolder myViewHolder, int position) {
            final Post post = posts.get(position);
            myViewHolder.imageView.setImageResource(post.getImageId());
            myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(context, PostActivity.class);
                    startActivity(intent);
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
        posts.add(new Post(R.drawable.itemviewtest));
        posts.add(new Post(R.drawable.post1));
        posts.add(new Post(R.drawable.post2));
        posts.add(new Post(R.drawable.post3));
        posts.add(new Post(R.drawable.post4));
        posts.add(new Post(R.drawable.post5));
        posts.add(new Post(R.drawable.post6));
        return posts;
    }
//以下為searchbar的方法
    private String grid_currentQuery = null; // holds the current query...
    final private android.widget.SearchView.OnQueryTextListener queryListener = new android.widget.SearchView.OnQueryTextListener() {

        @Override
        public boolean onQueryTextChange(String newText) {
//            if (TextUtils.isEmpty(newText)) {
//                getActivity().getActionBar().setSubtitle("List");
//                grid_currentQuery = null;
//            } else {
//                getActivity().getActionBar().setSubtitle("List - Searching for: " + newText);
//                grid_currentQuery = newText;
//                return true;
//
//            }
//            getLoaderManager().restartLoader(0, null, ExploreFragment.this);
            return false;
        }

        @Override
        public boolean onQueryTextSubmit(String query) {
            Toast.makeText(getActivity(), "Searching for: " + query + "...", Toast.LENGTH_SHORT).show();
            return false;
        }
    };
}
