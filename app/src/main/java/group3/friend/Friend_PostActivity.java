package group3.friend;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.cp102group3maple.violethsu.maple.R;

import group3.MainActivity;
import group3.explore.Explore_PA_PostFragment;
import group3.explore.OtherspageFragment;

public class Friend_PostActivity extends MainActivity {
    private Fragment fragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.explore_pa_main);
        getSupportActionBar().show();
        fragment=new OtherspageFragment();
        fragment.setArguments(this.getIntent().getExtras());
        changeFragment(fragment);

//        int id = getIntent().getIntExtra("id", 0);
//        if (id == 1) {
//            getSupportFragmentManager()
//                    .beginTransaction()
//                    .replace(R.id.FLlaylout, new OtherspageFragment())
//                    .addToBackStack(null)
//                    .commit();
//        }

    }

    @SuppressLint("ResourceType")
    public void changeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
//        OtherspageFragment otherspageFragment=new OtherspageFragment();
        fragmentTransaction.replace(R.id.postactivitycontainer,fragment).commit();
        //        fragmentTransaction.replace(R.id.postactivitycontainer, fragment).commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        switch(requestCode){
//            case 1:
//                Fragment fragment = new OtherspageFragment();
//                changeFragment(fragment);
//                break;
//        }

        int id = getIntent().getIntExtra("id", 1);
        if (id == 1) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.FLlaylout, new OtherspageFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }


}