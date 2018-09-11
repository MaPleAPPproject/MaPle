package group3.mypage;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cp102group3maple.violethsu.maple.R;

import java.util.ArrayList;
import java.util.Collection;

public class Mypage_Chart_Activity extends Activity{


    GeoMapView geoMapView;
    TextView percent;
    private float times=35;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chartlayout);
//        percent = findViewById(R.id.tvPercent);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
//        final FitChart fitChart = (FitChart)findViewById(R.id.fitChart);
//        fitChart.setMinValue(0f);
        geoMapView = (GeoMapView) findViewById(R.id.geoMap);
        geoMapView.setOnInitializedListener(new OnInitializedListener() {
            @Override
            public void onInitialized(GeoMapView geoMapView) {
                progressBar.setVisibility(View.INVISIBLE);
                geoMapView.setCountryColor("US", "#00BDBD");
                geoMapView.refresh();
            }
        });


//        fitChart.setMinValue(0f);
//        fitChart.setMaxValue(100f);
        //findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
//            @SuppressLint("ResourceAsColor")
//            @Override
//            public void onClick(View v) {
//        Resources resources = getResources();

//        Collection<FitChartValue> values = new ArrayList<>();
//                values.add(new FitChartValue(10f, resources.getColor(R.color.chart_value_1)));
//                values.add(new FitChartValue(10f, resources.getColor(R.color.chart_value_2)));
//                values.add(new FitChartValue(10f, resources.getColor(R.color.chart_value_3)));
//                values.add(new FitChartValue(10f, resources.getColor(R.color.chart_value_4)));
//        values.add(new FitChartValue(times, resources.getColor(R.color.chart_value_5)));
//        fitChart.setValues(values);

//        percent.setText(times + "%");
//        percent.setTextSize(23);
//        percent.setTextColor(Color.parseColor("#b0baff"));


    }
}
