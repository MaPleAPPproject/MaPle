package group3.mypage;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cp102group3maple.violethsu.maple.R;

import java.util.List;

import group3.Postdetail;

public class Ｍypage_tab_post_Fragment extends Fragment {
    private FloatingActionButton fab;

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
//        rvPost.setAdapter(new Ｍypage_tab_post_Fragment.PostAdapter(getPosts(), getActivity()));


    }

    public class PostAdapter extends RecyclerView.Adapter<Ｍypage_tab_post_Fragment.PostAdapter.MyViewHolder> {
        List<Postdetail> postdetails;
        Context context;

        public PostAdapter(List<Postdetail> postdetails, Context context) {
            this.postdetails = postdetails;
            this.context = context;

        }

        @Override
        public int getItemCount() {
            return postdetails.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
//                imageView = itemView.findViewById(R.id.ivtop);
            }
        }

        @NonNull
        @Override
        public Ｍypage_tab_post_Fragment.PostAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View item_view = layoutInflater.inflate(R.layout.item_view_post, parent, false);
            return new Ｍypage_tab_post_Fragment.PostAdapter.MyViewHolder(item_view);
        }


        @Override
        public void onBindViewHolder(@NonNull Ｍypage_tab_post_Fragment.PostAdapter.MyViewHolder myViewHolder, int position) {
//            final Postdetail postdetail = postdetails.get(position);
//            myViewHolder.imageView.setImageResource(postdetail.getImageId());
//            myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
////                    Intent intent = new Intent();
////                    intent.setClass(context, Explore_PA_PostFragment.class);
////                    startActivity(intent);
//                    //        /* 取得Bundle物件 */
////                    int programming = Integer.parseInt(etProgramming.getText().toString());
////                    Bundle bundle = new Bundle();
////                    Postdetail post1 = new Postdetail(programming, dataStructure, algorithm);
////                    //轉成物件傳入(key,object)
////                    bundle.putSerializable("score", score);
////                    ResultFragment resultFragment = new ResultFragment();
////                    /* 將Bundle資料轉給resultFragment */
////                    resultFragment.setArguments(bundle);
////                    if(getFragmentManager()==null){
////                        return;
//
//                }
//            });
//
//        }
//    }
//    private List<Postdetail> getPosts() {
////        next step:add all information
//        List<Postdetail> postdetails = new ArrayList<>();
//        postdetails.add(new Postdetail(R.drawable.post7));
//        postdetails.add(new Postdetail(R.drawable.post8));
//        postdetails.add(new Postdetail(R.drawable.post9));
//        postdetails.add(new Postdetail(R.drawable.post10));
//        postdetails.add(new Postdetail(R.drawable.post11));
//        postdetails.add(new Postdetail(R.drawable.post12));
//        return postdetails;
//    }
        }
    }


}
