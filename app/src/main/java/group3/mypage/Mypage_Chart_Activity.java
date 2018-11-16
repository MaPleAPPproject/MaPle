package group3.mypage;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.motion.utils.Oscillator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cp102group3maple.violethsu.maple.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import group3.Common;
import group3.MainActivity;

public class Mypage_Chart_Activity extends AppCompatActivity {

    private CommonTask visitedTask;
    private HashSet<String> countryCodeSet = new HashSet<>();
    private GeoMapView geoMapView;
    private int memberId;
    private HashMap<String, Integer> visitedCount;
    private TextView tvresult, tvresultasia, tvresulteurope, tvresultnorthamerica, tvresultsouthamerica, tvresultafrica, tvresultoceania;
    private final String TAG = "Mypage_Chart_Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chartlayout);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        SharedPreferences pref = getSharedPreferences(Common.PREF_FILE,
                MODE_PRIVATE);
        memberId = Integer.valueOf(pref.getString("MemberId",""));
        handleView();
        getVisitedStatic(memberId);
        geoMapView = (GeoMapView) findViewById(R.id.geoMap);
        geoMapView.setOnInitializedListener(new OnInitializedListener() {
            @Override
            public void onInitialized(GeoMapView geoMapView) {
                progressBar.setVisibility(View.INVISIBLE);
                loadMapStatic(memberId);
                geoMapView.refresh();

            }
        });

    }

    private void handleView() {
        tvresult = findViewById(R.id.tvresult);
        tvresultasia = findViewById(R.id.tvresultasia);
        tvresulteurope = findViewById(R.id.tvresulteurope);
        tvresultnorthamerica = findViewById(R.id.tvresultnorthamerica);
        tvresultsouthamerica = findViewById(R.id.tvresultsouthamerica);
        tvresultafrica = findViewById(R.id.tvresultafrica);
        tvresultoceania = findViewById(R.id.tvresultoceania);





    }


    public void loadMapStatic(int memberId) {

        if (Common.networkConnected(this)) {
            String url = Common.URL + "/ChartServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getCountryCode");
            jsonObject.addProperty("memberId", memberId);
            String jsonOut = jsonObject.toString();
            visitedTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = visitedTask.execute().get();
                Type setType = new TypeToken<HashSet<String>>() {
                }.getType();
                countryCodeSet = new Gson().fromJson(jsonIn, setType);

            } catch (Exception e) {
                Log.e(Oscillator.TAG, e.toString());
            }
            if (countryCodeSet == null) {
//                Toast.makeText(this, "no_profile", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Fail to get contrycodeset from server.");
            } else {
                if (!countryCodeSet.isEmpty()) {
                    for (String code : countryCodeSet) {

                        if (code == "TWN") {
                            geoMapView.setCountryColor("TW", "#00BDBD");
                        } else {
                            geoMapView.setCountryColor(code, "#00BDBD");
                        }

                        geoMapView.refresh();
                        return;
                    }
                } else {
                    return;

                }

            }

        } else {
//
            Log.e(TAG, "Fail to connect to server.");
        }

    }


    public void getVisitedStatic(int memberId) {


        if (Common.networkConnected(this)) {
            String url = Common.URL + "/ChartServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getVisitedStatic");
            jsonObject.addProperty("memberId", memberId);
            String jsonOut = jsonObject.toString();
            visitedTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = visitedTask.execute().get();
                Type setType = new TypeToken<HashMap<String, Integer>>() {
                }.getType();
                visitedCount = new Gson().fromJson(jsonIn, setType);

            } catch (Exception e) {
                Log.e(Oscillator.TAG, e.toString());
            }
            if (visitedCount == null || visitedCount.isEmpty()) {
                Toast.makeText(this, "no_profile", Toast.LENGTH_SHORT).show();
            } else {

                Iterator<Map.Entry<String, Integer>> iter = visitedCount.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<String, Integer> entry = iter.next();
                    String key = entry.getKey();
                    Integer value = entry.getValue();
                    DecimalFormat df = new DecimalFormat("######0.00");


                    switch (key) {
                        case "Visited countries":
                            int countryAmountTol = 234;
                            String percentTol = "(" + value + "/" + countryAmountTol + ")   " + df.format(value*100.0f / countryAmountTol) + "%";
                            tvresult.setText(percentTol);
                            tvresult.setTextColor(Color.rgb(30,163,193));
                            break;

                        case "Asia":
                            int countryAmountAC = 51;
                            String percentAC = "(" + value + "/" + countryAmountAC + ")   " + df.format(value*100.0f / countryAmountAC) + "%";
                            tvresultasia.setText(percentAC);
                            break;

                        case "Europe":
                            int countryAmountEU = 46;
                            String percentEU = "(" + value + "/" + countryAmountEU + ")   " + df.format(value*100.0f / countryAmountEU)+ "%";
                            tvresulteurope.setText(percentEU);
                            break;

                        case "North America":
                            int countryAmountNA = 37;
                            String percentNA = "(" + value + "/" + countryAmountNA + ")   " + df.format(value*100.0f / countryAmountNA) + "%";
                            tvresultnorthamerica.setText(percentNA);
                            break;

                        case "South America":
                            int countryAmount_SA = 14;
                            String percentSA = "(" + value + "/" + countryAmount_SA + ")   " + df.format(value*100.0f / countryAmount_SA) + "%";
                            tvresultsouthamerica.setText(percentSA);
                            break;

                        case "Africa":
                            int countryAmount_AF = 58;
                            String percentAF = "(" + value + "/" + countryAmount_AF + ")   " + df.format(value*100.0f / countryAmount_AF) + "%";
                            tvresultafrica.setText(percentAF);
                            break;

                        case "Oceania":
                            int countryAmount_OC = 28;
                            String percentOC = "(" + value + "/" + countryAmount_OC + ")   " + df.format(value*100.0f / countryAmount_OC) + "%";
                            tvresultoceania.setText(percentOC);
                            break;

                        default:

                    }


                }


            }

        } else {
            Toast.makeText(this, "no_profile", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    protected void onStop() {
        super.onStop();
        if (visitedTask != null) {
            visitedTask.cancel(true);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(Mypage_Chart_Activity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }


}