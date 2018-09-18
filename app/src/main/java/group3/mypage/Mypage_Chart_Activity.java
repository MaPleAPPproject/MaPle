package group3.mypage;

import android.app.Activity;
import android.os.Bundle;
import android.support.constraint.motion.utils.Oscillator;
import android.util.Log;
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

public class Mypage_Chart_Activity extends Activity {

    private CommonTask visitedTask;
    private HashSet<String> countryCodeSet = new HashSet<>();
    private GeoMapView geoMapView;
    private int memberId = 2;
    private HashMap<String, Integer> visitedCount;
    private TextView tvresult, tvresultasia, tvresulteurope, tvresultnorthamerica, tvresultsouthamerica, tvresultafrica, tvresultoceania;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chartlayout);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
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
                Toast.makeText(this, "no_profile", Toast.LENGTH_SHORT).show();
            } else {


                for (String code : countryCodeSet) {

                    if (code == "TWN") {
                        geoMapView.setCountryColor("TW", "#00BDBD");
                    } else {
                        geoMapView.setCountryColor(code, "#00BDBD");
                    }

                    geoMapView.refresh();
                }

            }

        } else {
            Toast.makeText(this, "no_profile", Toast.LENGTH_SHORT).show();
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
//                    int countryAmount = 234;
//                    int visitedCountry =
//                            tvresult.setText();
                    DecimalFormat df = new DecimalFormat("######0.00");


                    switch (key) {
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


}
