package group3.mypage;

import android.content.Context;
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

import com.cp102group3maple.violethsu.maple.R;

import java.util.ArrayList;
import java.util.List;

import group3.Postdetail;

public class Mypage_tab_colec_Fragment extends Fragment {
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_collection, container, false);
        handleviews(view);
        return view;

    }

    private void handleviews(View view) {
        RecyclerView rvCollection = view.findViewById(R.id.rvCollection);
        rvCollection.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
//        rvCollection.setAdapter(new Mypage_tab_colec_Fragment.PostAdapter(getPosts(), getActivity()));
    }

    public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {
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
                imageView = itemView.findViewById(R.id.ivtop);
            }
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View item_view = layoutInflater.inflate(R.layout.item_view, parent, false);
            return new Mypage_tab_colec_Fragment.PostAdapter.MyViewHolder(item_view);
        }


        @Override
        public void onBindViewHolder(@NonNull Mypage_tab_colec_Fragment.PostAdapter.MyViewHolder myViewHolder, int position) {
            final Postdetail postdetail = postdetails.get(position);
//            myViewHolder.imageView.setImageResource(postdetail.getImageId());
//            myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent();
//                    intent.setClass(context, Explore_PA_PostFragment.class);
//                    startActivity(intent);
            //        /* 取得Bundle物件 */
//                    int programming = Integer.parseInt(etProgramming.getText().toString());
//                    Bundle bundle = new Bundle();
//                    Postdetail post1 = new Postdetail(programming, dataStructure, algorithm);
//                    //轉成物件傳入(key,object)
//                    bundle.putSerializable("score", score);
//                    ResultFragment resultFragment = new ResultFragment();
//                    /* 將Bundle資料轉給resultFragment */
//                    resultFragment.setArguments(bundle);
//                    if(getFragmentManager()==null){
//                        return;
//
//                }
//            });
//
//        }
//    }
//    private List<Postdetail> getPosts() {
////        next step:add all information
//        List<Postdetail> postdetails = new ArrayList<>();
//        postdetails.add(new Postdetail(R.drawable.itemviewtest));
//        postdetails.add(new Postdetail(R.drawable.post1));
//        postdetails.add(new Postdetail(R.drawable.post2));
//        postdetails.add(new Postdetail(R.drawable.post3));
//        postdetails.add(new Postdetail(R.drawable.post4));
//        postdetails.add(new Postdetail(R.drawable.post5));
//        postdetails.add(new Postdetail(R.drawable.post6));
//        return postdetails;
//    }
        }
    }
}
