package group3.mypage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cp102group3maple.violethsu.maple.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import group3.Common;
import group3.Picture;
import group3.Postdetail;
import group3.explore.ExploreFragment;
import group3.explore.Explore_PostActivity;
import group3.explore.PostTask;
import group3.explore.TabFragment_Collect;

import static android.content.Context.MODE_PRIVATE;
import static android.support.constraint.motion.utils.Oscillator.TAG;

public class Mypage_tab_colec_Fragment extends Fragment {
    private static final String TAG = "Mypage_tab_colec_Fragment";
    private CommonTask pictureGetAllTask,deletePostTask;
    private RecyclerView rvCollection;
    private int memberid;
    private PostTask pictureImageTask;
    private Context contentview;
    private Bundle bundle;
    private SharedPreferences pref;
    private TextView tvCollection;
    private ImageView ivCollection;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getActivity().getSharedPreferences(Common.PREF_FILE,
                MODE_PRIVATE);
        String smemberId = pref.getString("MemberId", "");
        memberid=Integer.parseInt(smemberId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.tab_collection, container, false);
        handleviews(view);
        return view;

    }
    @Override
    public void onStart() {
        super.onStart();
        showAllPosts();
    }
    //  取得收藏的照片
    private void showAllPosts() {
        if (Common.networkConnected(getActivity())) {

//            bundle=getArguments();
//            int memberid=bundle.getInt("memberid");
            String url = Common.URL + "/PictureServlet";
            List<Picture> pictures = null;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getcollectBymemberId");
            jsonObject.addProperty("memberid", memberid);
            String jsonOut = jsonObject.toString();
            pictureGetAllTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = pictureGetAllTask.execute().get();
                Type listType = new TypeToken<List<Picture>>() {
                }.getType();
                pictures = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
//                Log.e(TAG, e.toString());
            }
            if (pictures == null||pictures.isEmpty()) {
//                Toast.makeText(getActivity(),R.string.msg_NoPost,Toast.LENGTH_SHORT).show();
                rvCollection.setVisibility(View.GONE);
                ivCollection.setVisibility(View.VISIBLE);
            }
            else {
                rvCollection.setAdapter(new Mypage_tab_colec_Fragment.PostAdapter(pictures, contentview));
            }
        } else {
            Toast.makeText(getActivity(), R.string.msg_NoNetwork, Toast.LENGTH_SHORT).show();
        }

    }

    private void handleviews(View view) {

        ivCollection = view.findViewById(R.id.ivCollection);
        rvCollection = view.findViewById(R.id.rvCollection);
        rvCollection.setLayoutManager(new GridLayoutManager(contentview,3));
        contentview = view.getContext();
        if (bundle != null) {
            showAllPosts();
        } else {
//            Toast.makeText(getActivity(), "no bundle", Toast.LENGTH_SHORT).show();
        }


    }

    public class PostAdapter extends RecyclerView.Adapter<Mypage_tab_colec_Fragment.PostAdapter.MyViewHolder> implements View.OnLongClickListener {
        private int imageSize;
        private List<Picture> pictures;
        private LayoutInflater layoutInflater;

        PostAdapter(List<Picture> pictures, Context context) {
            this.pictures = pictures;
            layoutInflater = LayoutInflater.from(context);


        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }


        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.ivcollect);
            }
        }
        @Override
        public int getItemCount() {
            return pictures.size();
        }

        @NonNull
        @Override
        public Mypage_tab_colec_Fragment.PostAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            View item_view = layoutInflater.inflate(R.layout.item_view_collectionpicture, parent, false);
            return new Mypage_tab_colec_Fragment.PostAdapter.MyViewHolder(item_view);
        }


        @Override
        public void onBindViewHolder(@NonNull Mypage_tab_colec_Fragment.PostAdapter.MyViewHolder myViewHolder, int position) {
            final Picture picture = pictures.get(position);
            String url = Common.URL + "/PictureServlet";
            //發起PostTask 使用picture中的id取的圖檔資料
            int id = picture.getPostid();
            pictureImageTask = new PostTask(url, id, imageSize, myViewHolder.imageView);
            pictureImageTask.execute();
            myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent =new Intent(getActivity(),Explore_PostActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("picture", picture);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });

            registerForContextMenu(myViewHolder.itemView);



        }
    }

//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
//        MenuInflater inflater = getActivity().getMenuInflater();
//        inflater.inflate(R.menu.collection_delete, menu);
//    }
//
//    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//
//        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
//        switch (item.getItemId()) {
//            case R.id.collectionDelete:
//                int collId = getView().getId();
//                deleteCollection(collId);
//
//
//                return true;
//            default:
//                return super.onContextItemSelected(item);
//        }
//
//
//
//    }

    public void deleteCollection(int collId){


        if (Common.networkConnected(getContext())) {
            String url = Common.URL + "/CpostServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "deleteColl");
            jsonObject.addProperty("collId", collId);
            String jsonOut = jsonObject.toString();
            deletePostTask = new CommonTask(url, jsonOut);
            int count = 0;
            try {
                String jsonIn = deletePostTask.execute().get();
                count = Integer.valueOf(jsonIn);

            } catch (Exception e) {
                Log.e("Myoage_tab_collection", e.toString());
            }
            if (count != 0 ) {
                Toast.makeText(getActivity(), "Your post has deleted", Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(getActivity(), "Fail to delete the post", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(getActivity(), "no_profile", Toast.LENGTH_SHORT).show();
        }

    }



    @Override
    public void onStop() {
        super.onStop();
        if (pictureImageTask != null) {
            pictureImageTask.cancel(true);
        }
        if (pictureGetAllTask != null) {
            pictureGetAllTask.cancel(true);
        }
    }
}
