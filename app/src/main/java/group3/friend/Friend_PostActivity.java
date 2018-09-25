package group3.friend;

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

    }

    public void changeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.postactivitycontainer, fragment).commit();
    }

}