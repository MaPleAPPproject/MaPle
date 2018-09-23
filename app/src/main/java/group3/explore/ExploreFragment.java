package group3.explore;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import group3.Picture;
import group3.mypage.CommonTask;

//注意fragment和activity呼叫server時間點不同
public class ExploreFragment extends Fragment {
    private static final String TAG = "ExploreFragment";
    private SearchView searchView;
    private RecyclerView rvRecom;
    private RecyclerView rvTop;
    private Context contentview;
    private CommonTask pictureGetAllTask;
    private PostTask pictureImageTask;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Picture picture;
    private TextView mTextView;
    private CommonTask pictureGetTopTask;

//  當點擊照片時會進入另一個activity,用來存放aveInstanceState資訊
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
//        Log.v(TAG, "In frag's on save instance state ");
        outState.putSerializable("picture",picture);
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
//        datafromServer=savedInstanceState.getString("picture");

    }
    @Override
    public void onStart() {
        super.onStart();
        showAllPosts();
        showTopPosts();
    }
    private void showTopPosts() {
        if (Common.networkConnected(getActivity())) {
            String url = Common.URL + "/PictureServlet";
            List<Picture> picturestop = null;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getTop");
            String jsonOut = jsonObject.toString();
            pictureGetTopTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = pictureGetTopTask.execute().get();
                Type listType = new TypeToken<List<Picture>>() {
                }.getType();
                picturestop = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (picturestop == null||picturestop.isEmpty()) {
                Toast.makeText(contentview,R.string.msg_NoPost,Toast.LENGTH_SHORT).show();
            }
            else {
//                如果成功取得文字資料就將資料傳入adapter繼續後續處理
//                若是adapter需要圖的話,則是在onBindViewHolder中發起imageTask
                rvTop.setAdapter(new PostAdapter(picturestop, getActivity()));
            }
        } else {
            Toast.makeText(contentview, R.string.msg_Nonetwork, Toast.LENGTH_SHORT).show();
        }

    }
    private void showAllPosts() {
        if (Common.networkConnected(getActivity())) {
            String url = Common.URL + "/PictureServlet";
            List<Picture> pictures = null;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAll");
            String jsonOut = jsonObject.toString();
            pictureGetAllTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = pictureGetAllTask.execute().get();
                Type listType = new TypeToken<List<Picture>>() {
                }.getType();
                pictures = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (pictures == null||pictures.isEmpty()) {
                Toast.makeText(contentview,R.string.msg_NoPost,Toast.LENGTH_SHORT).show();
            }
            else {
                rvRecom.setAdapter(new PostAdapter(pictures, getActivity()));
            }
        } else {
            Toast.makeText(contentview, R.string.msg_Nonetwork, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search, menu);
        searchView.setOnQueryTextListener((SearchView.OnQueryTextListener) queryListener);
        final SearchView.SearchAutoComplete searchAutoComplete = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchAutoComplete.setDropDownBackgroundResource(android.R.color.background_light);
        // Create a new ArrayAdapter and add data to search auto complete object.
//        need to add connect from web
        String dataArr[] = {"Japan" , "Korea" , "Taiwan", "Taipei", "New York", "China", "Thailand", "USA"};
        ArrayAdapter<String> newsAdapter = new ArrayAdapter<String>(contentview, android.R.layout.simple_dropdown_item_1line, dataArr);
        searchAutoComplete.setAdapter(newsAdapter);

        // Listen to search view item on click event.
        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int itemIndex, long id) {
                String queryString=(String)adapterView.getItemAtPosition(itemIndex);
                searchAutoComplete.setText("" + queryString);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_exploretest,container, false);
//        if ((savedInstanceState != null) && (savedInstanceState.getSerializable("picture") != null)) {
//            savedInstanceState.getSerializable("picture"); }
        swipeRefreshLayout =
                view.findViewById(R.id.swiprefreshlayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                showAllPosts();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        handleviews(view);
        return view;

    }

    private void handleviews(View view) {
        rvTop = view.findViewById(R.id.rvTop);
        rvTop.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false) );
        rvRecom = view.findViewById(R.id.rvRecom);
//        rvRecom.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false) );
        rvRecom.setLayoutManager(new GridLayoutManager(getActivity(),3));
        searchView=view.findViewById(R.id.searchview);
        contentview=view.getContext();
    }
//事件處理跟之前一樣
    private class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private int imageSize;
        private List<Picture> pictures;
        PostAdapter(List<Picture> pictures, Context context) {
            this.pictures = pictures;
            layoutInflater = LayoutInflater.from(context);
//            要注意圖片尺寸隨著螢幕縮放 除三等於呈現為螢幕的三分之一大小
            imageSize = getResources().getDisplayMetrics().widthPixels / 3;
        }
        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView ivpostpicture;
            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                ivpostpicture=itemView.findViewById(R.id.ivrecom);
            }
        }
        @Override
        public int getItemCount() {
            return pictures.size();
        }

        @NonNull
        @Override
        public ExploreFragment.PostAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            View itemView = layoutInflater.inflate(R.layout.item_view_picture, parent, false);
            return new ExploreFragment.PostAdapter.MyViewHolder(itemView);
        }


        @Override
        public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
            final Picture picture = pictures.get(position);
            String url = Common.URL + "/PictureServlet";
            //發起PostTask 使用picture中的id取的圖檔資料
            int id = picture.getPostid();
            pictureImageTask = new PostTask(url, id, imageSize, myViewHolder.ivpostpicture);
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

        }
    }
//以下為searchbar的方法
    final private SearchView.OnQueryTextListener queryListener = new SearchView.OnQueryTextListener() {

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }

        @Override
        public boolean onQueryTextSubmit(String query) {
//            收起鍵盤
//            更換recycleview 顯示搜尋結果的post

            return false;
        }
    };
//
    @Override
    public void onStop() {
        super.onStop();
        if (pictureGetAllTask != null) {
            pictureGetAllTask.cancel(true);
        }
        if (pictureGetTopTask != null) {
            pictureGetTopTask.cancel(true);
        }

        if (pictureImageTask != null) {
            pictureImageTask.cancel(true);
        }
        updatedata();
    }
//  收藏
    private void updatedata() {
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden){
            //Fragment隐藏时调用
            onPause();
//            onResume();
        }else {
            //Fragment显示时调用
//            onPause();
            onResume();
        }
    }
}
