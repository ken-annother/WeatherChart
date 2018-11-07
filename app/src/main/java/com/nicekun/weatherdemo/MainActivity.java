package com.nicekun.weatherdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.nicekun.weatherchartlib.PollutionTendChart;
import com.nicekun.weatherchartlib.PollutionTendChartData;
import com.nicekun.weatherchartlib.PollutuinIndicatorPlate;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private PollutionTendChart mPollutionTendChart;
    private PollutuinIndicatorPlate mPollutuinIndicatorPlate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initView() {
        mPollutionTendChart = (PollutionTendChart) findViewById(R.id.poluutionchart);
        mPollutuinIndicatorPlate = (PollutuinIndicatorPlate) findViewById(R.id.pullution_indicator_plate);
    }

    private void initData() {
        Integer[] aqiData = {
                94, 75, 38, 38, 48, 72, 43, 224, 47, 47, 378, 80, 77, 78, 100, null, 127, 109, 104, 86, 55, 58, 58, 65,
                94, 75, 38, 38, 48, 72, 43, 224, 47, 47, 378, 80, 77, 78, 100, null, 127, 109, 104, 86, 55, 58, 58, 65
        };

        Calendar instance = Calendar.getInstance();
        instance.set(Calendar.DAY_OF_MONTH, 8);
        Date time = instance.getTime();

        PollutionTendChartData pollutionTendChartData = new PollutionTendChartData.PollutionTendChartDataBuilder(aqiData, PollutionTendChartData.DataType.DAY)
        .setForcastFirstPointDate(time)
        .build();

        mPollutionTendChart.setData(pollutionTendChartData);

        mPollutuinIndicatorPlate.setAQI(310);
    }
}
