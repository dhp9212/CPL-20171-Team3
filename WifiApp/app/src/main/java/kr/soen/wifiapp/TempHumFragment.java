package kr.soen.wifiapp;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.triggertrap.seekarc.SeekArc;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class TempHumFragment extends Fragment {
    public static final String REQUEST_CURRENT_TEMP_HUM = "1";

    SharedPreferences pref;

    float[] current_temp = new float[2];
    int[] hum = new int[2];

    TextView chartTitle;
    BarChart currenttempChart;
    BarData currenttempData;

    TextView seekTitle1, seekTitle2;
    SeekArc currenthum;
    SeekArc avghum;
    TextView currenthumProgress;
    TextView avghumProgress;
    ValueAnimator animator1;
    ValueAnimator animator2;
    int currenthumValue = 0;
    int avghumValue = 0;

    boolean isfirst = true;

    public TempHumFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.fragment_temp_hum, container, false);

        chartTitle = (TextView)layout.findViewById(R.id.bar_chart_title) ;
        chartTitle.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "NanumBarunpenR.ttf"));

        currenttempChart = (BarChart) layout.findViewById(R.id.current_temp_chart);
        currenttempChart.getAxisLeft().setEnabled(false);
        currenttempChart.getAxisRight().setEnabled(false);
        currenttempChart.getAxisLeft().setAxisMaximum(36);
        currenttempChart.getAxisLeft().setAxisMinimum(20);
        currenttempChart.getXAxis().setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "NanumBarunpenR.ttf"));
        currenttempChart.getXAxis().setTextSize(16);
        currenttempChart.getXAxis().setTextColor(getResources().getColor(R.color.white));
        currenttempChart.getDescription().setEnabled(false);
        currenttempChart.setTouchEnabled(false);
        currenttempChart.setDragEnabled(false);
        currenttempChart.setScaleEnabled(false);
        currenttempChart.getLegend().setEnabled(false);


        seekTitle1 = (TextView)layout.findViewById(R.id.seek_title1);
        seekTitle1.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "NanumBarunpenR.ttf"));

        currenthum = (SeekArc)layout.findViewById(R.id.currenthumseekArc);
        currenthumProgress = (TextView)layout.findViewById(R.id.currenthumseekArcProgress);
        currenthumProgress.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "NanumBarunpenR.ttf"));
        currenthum.setProgressWidth(15);
        currenthum.setArcWidth(15);

        seekTitle2 = (TextView)layout.findViewById(R.id.seek_title2);
        seekTitle2.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "NanumBarunpenR.ttf"));

        avghum = (SeekArc)layout.findViewById(R.id.avghumseekArc);
        avghumProgress = (TextView)layout.findViewById(R.id.avghumseekArcProgress);
        avghumProgress.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "NanumBarunpenR.ttf"));
        avghum.setProgressWidth(15);
        avghum.setArcWidth(15);

        return layout;
    }

    @Override
    public void onResume() {
        Log.d(this.getClass().getSimpleName(), "onResume()");
        super.onResume();

        if(!pref.getString("id", "").equals(""))
        {
            Log.d("SOCKET", "서버 연결 요청 _ TempHumFragment");
            ((MainActivity)getActivity()).doCommu(REQUEST_CURRENT_TEMP_HUM);
        }

        setValue(((MainActivity)getActivity()).currentTempHumData);
        setGraphData(current_temp[0], current_temp[1], hum[0], hum[1]);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser)
        {
            if(isfirst)
            {
                isfirst = false;
            }
            else {
                currenttempChart.animateY(2000);
            }
        }
    }

    public void setValue(String valueSet)
    {
        StringTokenizer str = new StringTokenizer(valueSet, "/");

        int countTokens = str.countTokens();

        for (int i = 0; i < countTokens; i++)
        {
            if(i < 2)
            {
                current_temp[i] = Float.parseFloat(str.nextToken());
            }
            else
            {
                hum[i - 2] = Integer.parseInt(str.nextToken());
            }
        }
    }

    public void setGraphData(float hotzone, float coolzone, int chValue, int ahValue)
    {
        ArrayList<BarEntry> currenttempDataEntry = new ArrayList<>();
        currenttempDataEntry.add(new BarEntry(0, hotzone));
        currenttempDataEntry.add(new BarEntry(1, coolzone));

        BarDataSet currenttempDataSet = new BarDataSet(currenttempDataEntry, "온도(℃)");
        currenttempDataSet.setColors(new int[] {getResources().getColor(R.color.colorAccent), getResources().getColor(R.color.blue)});
        currenttempDataSet.setValueTextSize(15);

        currenttempData = new BarData(currenttempDataSet);
        currenttempData.setValueTextColor(getResources().getColor(R.color.white));
        currenttempData.setValueTypeface(Typeface.createFromAsset(getActivity().getAssets(), "NanumBarunpenR.ttf"));
        currenttempData.setBarWidth(0.6f);
        currenttempChart.setData(currenttempData);

        final String[] xAxis = new String[] {"Hot Zone", "Cool Zone"};

        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xAxis[(int) value];
            }
        };

        currenttempChart.getXAxis().setValueFormatter(formatter);
        currenttempChart.getXAxis().setGranularity(1);

        currenthumValue = chValue;
        animator1 = ValueAnimator.ofInt(0, currenthumValue);
        animator1.setDuration(2000);
        animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int progress = (Integer)valueAnimator.getAnimatedValue();
                currenthum.setProgress(progress);
                currenthumProgress.setText(Integer.toString(progress) + "%");
            }
        });

        avghumValue = ahValue;
        animator2 = ValueAnimator.ofInt(0, avghumValue);
        animator2.setDuration(2000);
        animator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int progress = (Integer)valueAnimator.getAnimatedValue();
                avghum.setProgress(progress);
                avghumProgress.setText(Integer.toString(progress) + "%");
            }
        });


        currenttempChart.animateY(2000);
        animator1.start();
        animator2.start();
    }


}
