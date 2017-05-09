package kr.soen.wifiapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

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
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

public class AccrueTempFragment extends Fragment {
    float[] hot_accrue_tempTokens = new float[8];
    float[] cool_accrue_tempTokens = new float[8];

    LineChart accruetempChart;
    LineData accruetempData;

    public AccrueTempFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setValue(((MainActivity)getActivity()).accrueTempData);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.fragment_accrue_temp, container, false);

        accruetempChart = (LineChart) layout.findViewById(R.id.accrue_temp_chart);
        accruetempChart.getAxisLeft().setEnabled(false);
        accruetempChart.getAxisRight().setEnabled(false);
        accruetempChart.getAxisLeft().setAxisMaximum(36);
        accruetempChart.getAxisLeft().setAxisMinimum(20);
        accruetempChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        accruetempChart.getDescription().setEnabled(false);
        accruetempChart.setTouchEnabled(false);
        accruetempChart.setDragEnabled(false);
        accruetempChart.setScaleEnabled(false);

        setGraphData(hot_accrue_tempTokens, cool_accrue_tempTokens);

        return layout;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser)
        {
            accruetempChart.animateX(2000);
        }
    }

    public void setValue(String valueSet)
    {
        StringTokenizer str = new StringTokenizer(valueSet, "/");

        int countTokens = str.countTokens();

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
        coolzoneDataSet.setColor(getResources().getColor(R.color.blue));
        coolzoneDataSet.setValueTextSize(15);
        tempDataSet.add(coolzoneDataSet);

        accruetempData = new LineData(tempDataSet);
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
