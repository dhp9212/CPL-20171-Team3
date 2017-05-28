package kr.soen.wifiapp;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

public class TempHumFragment extends Fragment implements View.OnClickListener{
    public static final String REQUEST_CURRENT_TEMP_HUM = "1";
    public static final String REQUEST_ACCRUE_TEMP = "2";

    public static final int TEMP_HUM = 1;
    public static final int HOURLY_TEMP = 2;

    float[] current_temp = new float[2];
    int[] hum = new int[2];

    float[] hot_accrue_tempTokens = new float[8];
    float[] cool_accrue_tempTokens = new float[8];

    TextView todayText;
    TextView hotzoneTemp, coolzoneTemp, currentHum, avgHum;
    LineChart accruetempChart;

    boolean isFirst = true;

    public TempHumFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.fragment_temp_hum, container, false);

        ImageButton profileBtn = (ImageButton)layout.findViewById(R.id.btn_profile);
        profileBtn.setOnClickListener(this);

        TextView titleText = (TextView)layout.findViewById(R.id.title_text) ;
        titleText.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "NanumSquareEB.ttf"));

        ImageButton cameraBtn = (ImageButton)layout.findViewById(R.id.btn_camera);
        cameraBtn.setOnClickListener(this);

        ///////////////

        todayText = (TextView)layout.findViewById(R.id.today) ;
        todayText.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "CODE Light.otf"));
        setToday();

        ///////////////

        TextView hotzoneTitle = (TextView)layout.findViewById(R.id.hotzone_title);
        hotzoneTitle.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "NanumSquareB.ttf"));

        hotzoneTemp = (TextView)layout.findViewById(R.id.hotzone_text);
        hotzoneTemp.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "NanumSquareB.ttf"));

        TextView hotzoneUnit = (TextView)layout.findViewById(R.id.hotzone_unit);
        hotzoneUnit.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "NanumSquareB.ttf"));


        TextView coolzoneTitle = (TextView)layout.findViewById(R.id.coolzone_title);
        coolzoneTitle.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "NanumSquareB.ttf"));

        coolzoneTemp = (TextView)layout.findViewById(R.id.coolzone_text);
        coolzoneTemp.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "NanumSquareB.ttf"));

        TextView coolzoneUnit = (TextView)layout.findViewById(R.id.coolzone_unit);
        coolzoneUnit.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "NanumSquareB.ttf"));


        TextView currenthumTitle = (TextView)layout.findViewById(R.id.currenthum_title);
        currenthumTitle.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "NanumSquareB.ttf"));

        currentHum = (TextView)layout.findViewById(R.id.currenthum_text);
        currentHum.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "NanumSquareB.ttf"));

        TextView currenthumUnit = (TextView)layout.findViewById(R.id.currenthum_unit);
        currenthumUnit.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "NanumSquareB.ttf"));


        TextView avghumTitle = (TextView)layout.findViewById(R.id.avghum_title);
        avghumTitle.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "NanumSquareB.ttf"));

        avgHum = (TextView)layout.findViewById(R.id.avghum_text);
        avgHum.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "NanumSquareB.ttf"));

        TextView avghumUnit = (TextView)layout.findViewById(R.id.avghum_unit);
        avghumUnit.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "NanumSquareB.ttf"));

        ///////////////

        TextView linechartTitle = (TextView)layout.findViewById(R.id.line_chart_tile);
        linechartTitle.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "NanumSquareEB.ttf"));

        accruetempChart = (LineChart) layout.findViewById(R.id.accrue_temp_chart);
        accruetempChart.getAxisLeft().setEnabled(false);
        accruetempChart.getAxisRight().setEnabled(false);
        accruetempChart.getAxisLeft().setAxisMaximum(36);
        accruetempChart.getAxisLeft().setAxisMinimum(20);
        accruetempChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        accruetempChart.getXAxis().setTextSize(11);
        accruetempChart.getXAxis().setTextColor(getResources().getColor(R.color.white));
        accruetempChart.getXAxis().setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "NanumSquareR.ttf"));
        accruetempChart.getDescription().setEnabled(false);
        accruetempChart.setTouchEnabled(false);
        accruetempChart.setDragEnabled(false);
        accruetempChart.setScaleEnabled(false);
        accruetempChart.getLegend().setEnabled(false);

        return layout;
    }

    @Override
    public void onResume() {
        Log.d(this.getClass().getSimpleName(), "onResume()");
        super.onResume();

        ((MainActivity)getActivity()).doCommu(REQUEST_CURRENT_TEMP_HUM);
        ((MainActivity)getActivity()).doCommu(REQUEST_ACCRUE_TEMP);

        setValue(((MainActivity)getActivity()).currentTempHumData, TEMP_HUM);
        setValue(((MainActivity)getActivity()).accrueTempData, HOURLY_TEMP);
        setData(current_temp[0], current_temp[1], hum[0], hum[1]);
        setGraphData(hot_accrue_tempTokens, cool_accrue_tempTokens);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser)
        {
            if(!isFirst)
            {
                ((MainActivity)getActivity()).doCommu(REQUEST_CURRENT_TEMP_HUM);
                setData(current_temp[0], current_temp[1], hum[0], hum[1]);
                accruetempChart.animateX(2000);
            }
            isFirst = false;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btn_profile:
                ((MainActivity)getActivity()).vp.setCurrentItem(0);
                break;
            case R.id.btn_camera:
                ((MainActivity)getActivity()).vp.setCurrentItem(2);
                break;
        }
    }

    public void setToday()
    {
        long now = System.currentTimeMillis();
        Date today = new Date(now);
        SimpleDateFormat dateFormat = new  SimpleDateFormat("yyyy.MM.dd", java.util.Locale.getDefault());
        String todayStr = dateFormat.format(today);

        Calendar cal = Calendar.getInstance();
        String dayStr = null;
        switch (cal.get(Calendar.DAY_OF_WEEK))
        {
            case 1:
                dayStr = "SUN.";
                break;
            case 2:
                dayStr = "MON.";
                break;
            case 3:
                dayStr = "TUE.";
                break;
            case 4:
                dayStr = "WED.";
                break;
            case 5:
                dayStr = "THU.";
                break;
            case 6:
                dayStr = "FRI.";
                break;
            case 7:
                dayStr = "SAT.";
                break;
        }

        todayText.setText(todayStr + " " + dayStr);
    }

    public void setValue(String valueSet, int state)
    {
        StringTokenizer str = new StringTokenizer(valueSet, "/");

        int countTokens = str.countTokens();

        if(state == TEMP_HUM)
        {
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
        else if(state == HOURLY_TEMP)
        {
            for (int i = 0; i < countTokens; i++)
            {
                if (i < 8)
                {
                    hot_accrue_tempTokens[i] = Float.parseFloat(str.nextToken());
                }
                else
                {
                    cool_accrue_tempTokens[i - 8] = Float.parseFloat(str.nextToken());
                }
            }
        }
    }

    public void setData(float hotzone, float coolzone, int currenthum, int avghum)
    {
        hotzoneTemp.setText(Float.toString(hotzone));
        coolzoneTemp.setText(Float.toString(coolzone));
        currentHum.setText(Integer.toString(currenthum));
        avgHum.setText(Integer.toString(avghum));
    }

    public void setGraphData(float[] hotzone, float[] coolzone)
    {
        List<ILineDataSet> tempDataSet = new ArrayList<>();

        ArrayList<Entry> hotzoneDataEntry = new ArrayList<>();
        for (int i = 0; i < 8; i++)
        {
            hotzoneDataEntry.add(new Entry(i, hotzone[i]));
        }
        LineDataSet hotzoneDataSet = new LineDataSet(hotzoneDataEntry, "HotZone");
        hotzoneDataSet.setColor(getResources().getColor(R.color.colorAccent));
        hotzoneDataSet.setValueTextSize(15);
        tempDataSet.add(hotzoneDataSet);

        ArrayList<Entry> coolzoneDataEntry = new ArrayList<>();
        for (int i = 0; i < 8; i++)
        {
            coolzoneDataEntry.add(new Entry(i, coolzone[i]));
        }
        LineDataSet coolzoneDataSet = new LineDataSet(coolzoneDataEntry, "CoolZone");
        coolzoneDataSet.setColor(getResources().getColor(R.color.colorPrimary));
        coolzoneDataSet.setValueTextSize(15);
        tempDataSet.add(coolzoneDataSet);

        LineData accruetempData = new LineData(tempDataSet);
        accruetempData.setValueTextColor(getResources().getColor(R.color.white));
        accruetempData.setValueTypeface(Typeface.createFromAsset(getActivity().getAssets(), "NanumSquareR.ttf"));
        accruetempChart.setData(accruetempData);

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("HH");
        int formatHour = Integer.parseInt(sdfNow.format(date));

        final String[] xAxis = new String[8];
        for (int i = 0; i < 8; i++)
        {
            if(formatHour - ((7 - i)*3) < 0)
            {
                xAxis[i] = "어제 " + Integer.toString(24 + formatHour - ((7 - i)*3)) + "시";
            }
            else
            {
                xAxis[i] = "오늘 " + Integer.toString(formatHour - ((7 - i)*3)) + "시";
            }
        }

        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xAxis[(int) value];
            }
        };

        accruetempChart.getXAxis().setValueFormatter(formatter);
        accruetempChart.getXAxis().removeAllLimitLines();

        accruetempChart.animateX(2000);
    }
}
