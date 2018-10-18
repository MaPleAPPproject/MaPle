package group3.explore;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import java.util.ArrayList;
import java.util.List;

import group3.Common;
import group3.Picture;
import group3.mypage.CommonTask;

import static com.google.android.gms.common.util.ArrayUtils.contains;

//注意fragment和activity呼叫server時間點不同
public class ExploreFragment extends Fragment {
    private ViewPager tabviewPager;
    private TabLayout tabLayout;
    private static final String TAG = "ExploreFragment";
    private SearchView searchView;
    private Context contentview;
    private PostTask pictureImageTask;
    private PostAdapter adpter;
    private List<Picture> pictures;
    private TabFragmenttop topFragment;
    private TabFragmentRecom recFragment;
    private TabFragmentLocation locFragment;
    private CommonTask distinctTask;
    private String[] dataArr;
    ArrayList<String> distinctList =new ArrayList<>();


    //  當點擊照片時會進入另一個activity,用來存放aveInstanceState資訊
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
//        Log.v(TAG, "In frag's on save instance state ");
//        outState.putSerializable("picture",picture);
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search, menu);
        SearchView.OnQueryTextListener queryListener = new SearchView.OnQueryTextListener() {


            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "onQueryTextChange");
                PagerAdapter pagerAdapter = (PagerAdapter) tabviewPager
                        .getAdapter();
                Fragment viewPagerFragment =new Fragment();
                for (int i = 0; i < pagerAdapter.getCount(); i++) {

                    viewPagerFragment = (Fragment) tabviewPager
                            .getAdapter().instantiateItem(tabviewPager, i);
                    if (viewPagerFragment != null
                            && viewPagerFragment.isAdded()) {

                        if (viewPagerFragment instanceof TabFragmenttop) {
                            topFragment = (TabFragmenttop) viewPagerFragment;
                            if (topFragment != null) {
                                topFragment.beginSearch(newText);
                            }
                        } else if (viewPagerFragment instanceof TabFragmentRecom) {
                            recFragment = (TabFragmentRecom) viewPagerFragment;
                            if (recFragment != null) {
                                recFragment.beginSearch(newText);
                            }
                        } else if (viewPagerFragment instanceof TabFragmentLocation) {
                            locFragment = (TabFragmentLocation) viewPagerFragment;
                            if (locFragment != null) {
                                locFragment.beginSearch(newText);
                            }
                        }
                    }
                }
                return false;
            }
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
        };
        searchView.setOnQueryTextListener((SearchView.OnQueryTextListener) queryListener);
        final SearchView.SearchAutoComplete searchAutoComplete = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchAutoComplete.setDropDownBackgroundResource(android.R.color.background_light);

        ArrayAdapter<String> districtAdapter = new ArrayAdapter<String>(contentview, android.R.layout.simple_dropdown_item_1line, dataArr);
        searchAutoComplete.setAdapter(districtAdapter);

        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int itemIndex, long id) {
                String queryString=(String)adapterView.getItemAtPosition(itemIndex);
                searchAutoComplete.setText("" + queryString);
            }
        });
    }

    private void getDistinctList() {
        if (Common.networkConnected(getActivity())) {
            String url = Common.URL + "/PictureServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getDistinct");
            String jsonOut = jsonObject.toString();
            distinctTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = distinctTask.execute().get();
                Type listType = new TypeToken<ArrayList<String>>() {
                }.getType();
                distinctList = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (distinctList == null||distinctList.isEmpty()) {
                Toast.makeText(getActivity(),"貼文不存在",Toast.LENGTH_SHORT).show();
            }
            else {
                dataArr=new String [distinctList.size()];
                for(int i=0;i<distinctList.size();i++){
                    dataArr[i] = distinctList.get(i);
                }

            }
        } else {
            Toast.makeText(getActivity(),"網路連線異常", Toast.LENGTH_SHORT).show();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_explore,container, false);
        getDistinctList();
        handleviews(view);
        return view;

    }

    private void handleviews(View view) {
        tabLayout = view.findViewById(R.id.tablayout2);
        tabLayout.setupWithViewPager(tabviewPager);
        tabviewPager= view.findViewById(R.id.tabviewPager2);
        tabviewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (searchView != null && !searchView.isIconified()) {
                    //searchView.onActionViewExpanded();
                    searchView.setIconified(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        tabLayout.addTab(tabLayout.newTab().setText("熱門"));
        tabLayout.addTab(tabLayout.newTab().setText("推薦"));
        tabLayout.addTab(tabLayout.newTab().setText("最新"));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(tabviewPager));
        setupViewPager(tabviewPager);
        searchView=view.findViewById(R.id.searchview);
        searchView.setQueryHint("搜尋地點");
        contentview=view.getContext();

    }
    private void setupViewPager(ViewPager viewPager) {
        ExploreFragment.TabViewPagerAdapter adapter = new ExploreFragment.TabViewPagerAdapter(getChildFragmentManager());
        TabFragmenttop tabFragmentRecomTop =new TabFragmenttop();
        TabFragmentRecom tabFragmentRecomRec =new TabFragmentRecom();
        TabFragmentLocation tabFragmentRecomLoca =new TabFragmentLocation();
        adapter.addFragment(tabFragmentRecomTop, "熱門");
        adapter.addFragment(tabFragmentRecomRec, "推薦");
        adapter.addFragment(tabFragmentRecomLoca, "最新");

        viewPager.setAdapter(adapter);
    }
    public class TabViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> tabfragments = new ArrayList<>();
        private final List<String> tabfragmentstext = new ArrayList<>();
        private String tabTitles[] = new String[]{"熱門", "推薦","最新"};
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return tabfragments.get(0);
                case 1:
                    return tabfragments.get(1);
                case 2:
                    return tabfragments.get(2);
            }
            return null;
        }
        public TabViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return tabfragments.size();
        }

        public void addFragment(Fragment fragment, String title) {
            tabfragments.add(fragment);
            tabfragmentstext.add(title);
        }
        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }
    public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private int imageSize;
        List<Picture> pictures;
        PostAdapter(List<Picture> pictures, Context context) {
            this.pictures = pictures;
            layoutInflater = LayoutInflater.from(context);
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

        public void setfilter(List<Picture> listitem)
        {
            pictures=new ArrayList<>();
            pictures.addAll(listitem);
            notifyDataSetChanged();
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

    @Override
    public void onStop() {
        super.onStop();

        if (pictureImageTask != null) {
            pictureImageTask.cancel(true);
        }
        if (distinctTask != null) {
            distinctTask.cancel(true);
        }
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
    public List<Picture> filter(List<Picture> pl,String query)
    {
        Log.d(TAG, "filter");
        query=query.toLowerCase();
        final List<Picture> filteredModeList=new ArrayList<>();
        for (Picture model:pl)
        {
            final String text=model.getDistrict().toLowerCase();
            if (text.startsWith(query))
            {
                filteredModeList.add(model);
            }
        }
        return filteredModeList;
    }
}