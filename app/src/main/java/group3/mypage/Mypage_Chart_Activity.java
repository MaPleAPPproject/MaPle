package group3.mypage;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cp102group3maple.violethsu.maple.R;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import group3.Common;

import static android.support.constraint.motion.MotionScene.TAG;

public class Mypage_Chart_Activity extends Activity {

    private CommonTask visitedTask;
    private List<String> visited;


    GeoMapView geoMapView;
    private int memberId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chartlayout);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);



        geoMapView = (GeoMapView) findViewById(R.id.geoMap);
        geoMapView.setOnInitializedListener(new OnInitializedListener() {
            @Override
            public void onInitialized(GeoMapView geoMapView) {
                progressBar.setVisibility(View.INVISIBLE);


                geoMapView.setCountryColor("US", "#00BDBD");
                geoMapView.refresh();
            }
        });


    }

//refresh
//    private void loadPreference() {
//        SharedPreferences pf = getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
//        String memberId = pf.getString("memeberId", "");
//        if (Common.networkConnected(Mypage_Chart_Activity.this)) {
//            String url = Common.URL + "/ChartServlet";
//
//            JsonObject jsonObject = new JsonObject();
//            jsonObject.addProperty("action", "getCountryCode");
//            jsonObject.addProperty("memberId", memberId);
//            String jsonOut = jsonObject.toString();
//            visitedTask = new CommonTask(url, jsonOut);
//            List<String> countryCodeList = null;
//            try {
//                String result = visitedTask.execute().get();
//                Gson gson = new Gson();
//                Class<? extends List<String>> countryCode;
//                countryCodeList = gson.fromJson(result, countryCode);
//            } catch (Exception e) {
//                Log.e(TAG, e.toString());
//            }
//
//            if (countryCodeList == null) {
//                Toast.makeText(this, R.string.no_data_from_db,Toast.LENGTH_SHORT).show();
//            } else {
//
//
//
//            }
//        }
//    }
}