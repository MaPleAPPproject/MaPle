package group3.explore;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.cp102group3maple.violethsu.maple.R;

import java.util.ArrayList;
import java.util.List;

import group3.MainActivity;
import group3.Post;

public class ExploreFragment extends Fragment {
    MainActivity mainActivity;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

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
        rvTop.setAdapter(new PostAdapter(getPosts(), getActivity()));
        RecyclerView rvRecom = view.findViewById(R.id.rvRecom);
        rvRecom.setLayoutManager(new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL));
        rvRecom.setAdapter(new PostAdapter(getPosts(), getActivity()));
        SearchView searchView=view.findViewById(R.id.searchview);
    }

    private class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {
        private List<Post> posts;
        private Context context;
        PostAdapter(List<Post> posts, Context context) {
            this.posts=posts;
            this.context=context;

        }
        @Override
        public int getItemCount() {
            return posts.size();
        }
        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
//            TextView tvId;
//            TextView tvLocation;
//            TextView tvComment;
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
        public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
            final Post post = posts.get(position);
            myViewHolder.imageView.setImageResource(post.getImageId());
            myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent =new Intent(context,Explore_PostActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("post",post);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });

        }
    }
    protected List<Post> getPosts() {
//        next step:add all information
        List<Post> posts = new ArrayList<>();
        posts.add(new Post(R.drawable.itemviewtest,"Jack123","Hi","usa","aaaaaaaaaaaa"));
        posts.add(new Post(R.drawable.post1,"Apple222","Hoooo","Lodon","bbbbbbbb"));
        posts.add(new Post(R.drawable.post2,"Orange345","Nooo","Japan","ddddd"));
        posts.add(new Post(R.drawable.post3,"Frankly","apppleee","TW","reeeee"));
        posts.add(new Post(R.drawable.post4,"Cocooo","yo","China","gggg"));
        posts.add(new Post(R.drawable.post5,"JJ","ii","Korea","eeeeeee"));
        posts.add(new Post(R.drawable.post6,"OPPP","pp","Iceland","66666"));
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
