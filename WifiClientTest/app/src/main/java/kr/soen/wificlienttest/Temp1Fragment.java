package kr.soen.wificlienttest;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;


public class Temp1Fragment extends Fragment implements View.OnClickListener{

    private static final String IS_WIFI_ON = "is_wifi_on?";

    Button refreshBtn;

    BarChart currenttempChart;
    BarData currenttempData;

    Toast logMsg;

    boolean iswifion = false;
    boolean isfirst = true;

    public static final String REQUEST_CURRENT_TEMP = "2";


    public Temp1Fragment() {
    }

    public static Temp1Fragment newInstance(boolean wifistate) {
        Temp1Fragment fragment = new Temp1Fragment();
        Bundle args = new Bundle();
        args.putBoolean(IS_WIFI_ON, wifistate);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_temp1, container, false);

        currenttempChart = (BarChart) rootView.findViewById(R.id.current_temp_chart);

        currenttempChart.getAxisLeft().setEnabled(false);
        currenttempChart.getAxisRight().setEnabled(false);
        currenttempChart.getAxisLeft().setAxisMaxValue(36);
        currenttempChart.getAxisLeft().setAxisMinValue(20);
        currenttempChart.setDescription("");
        currenttempChart.setTouchEnabled(false);
        currenttempChart.setDragEnabled(false);
        currenttempChart.setScaleEnabled(false);

        setChartData(0, 0);

        //새로고침 button 설정
        refreshBtn = (Button)rootView.findViewById(R.id.refresh_btn);
        refreshBtn.setOnClickListener(this);

        iswifion = getArguments().getBoolean(IS_WIFI_ON);
        return rootView;
    }

    public void setChartData(float hotzone, float coolzone)
    {
        ArrayList<BarEntry> tempvalueSet = new ArrayList<>();
        BarEntry h_temp = new BarEntry(hotzone, 0);
        tempvalueSet.add(h_temp);
        BarEntry c_temp = new BarEntry(coolzone, 1);
        tempvalueSet.add(c_temp);

        BarDataSet tempSet = new BarDataSet(tempvalueSet, "온도(℃)");
        tempSet.setColors(new int[] {getResources().getColor(R.color.colorAccent), getResources().getColor(R.color.colorPrimary)});
        tempSet.setValueTextSize(15);

        ArrayList<String> xAxis = new ArrayList<>();
        xAxis.add("Hot Zone");
        xAxis.add("Cool Zone");

        currenttempData = new BarData(xAxis, tempSet);

        currenttempChart.setData(currenttempData);
        currenttempChart.animateY(2000);
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

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.refresh_btn:
                if (iswifion)
                {
                    ((MainActivity)getActivity()).doCommu(REQUEST_CURRENT_TEMP);
                    break;
                }

                setChartData(30.0f, 24.0f);
                logMessege("WiFi가 활성화되지 않았습니다.");
                break;
        }
    }

    public void logMessege(String log) {
        logMsg = Toast.makeText(getActivity(), log, Toast.LENGTH_SHORT);
        logMsg.show();
    }

    public void wifiState(boolean wifistate)
    {
        iswifion = wifistate;
    }
}
