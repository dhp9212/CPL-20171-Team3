package kr.soen.wificlienttest;

import android.content.Context;
import android.os.AsyncTask;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Temp1Fragment extends Fragment implements View.OnClickListener{
    public static final String REQUEST_CURRENT_TEMP = "1";

    public static final int ServerPort = 5000;
    public static final String ServerIP = "14.46.3.105";

    Socket socket;
    public InputStream mmInStream;
    public OutputStream mmOutStream;
    String taskString;

    SharedPreferences pref;
    WifiManager wifiManager;
    Toast logMsg;

    BarChart currenttempChart;
    BarData currenttempData;

    Button refreshBtn;

    boolean isfirst = true;


    public Temp1Fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_temp1, container, false);

        wifiManager = (WifiManager)getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);

        currenttempChart = (BarChart) rootView.findViewById(R.id.current_temp_chart);

        currenttempChart.getAxisLeft().setEnabled(false);
        currenttempChart.getAxisRight().setEnabled(false);
        currenttempChart.getAxisLeft().setAxisMaximum(36);
        currenttempChart.getAxisLeft().setAxisMinimum(20);
        currenttempChart.getDescription().setEnabled(false);
        currenttempChart.setTouchEnabled(false);
        currenttempChart.setDragEnabled(false);
        currenttempChart.setScaleEnabled(false);
        currenttempChart.getLegend().setEnabled(false);

        setChartData(0, 0);

        //새로고침 button 설정
        refreshBtn = (Button)rootView.findViewById(R.id.refresh_btn);
        refreshBtn.setOnClickListener(this);

        return rootView;
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
                if (wifiManager.isWifiEnabled())
                {
                    if(!pref.getString("id", "").equals(""))
                    {
                        doCommu(REQUEST_CURRENT_TEMP);
                        break;
                    }
                    else
                    {
                        logMessege("계정이 등록되지 않았습니다");
                        break;
                    }
                }

                logMessege("WiFi가 활성화되지 않았습니다.");
                break;
        }
    }

    public void setChartData(float hotzone, float coolzone)
    {
        ArrayList<BarEntry> currenttempDataEntry = new ArrayList<>();
        currenttempDataEntry.add(new BarEntry(0, hotzone));
        currenttempDataEntry.add(new BarEntry(1, coolzone));

        BarDataSet currenttempDataSet = new BarDataSet(currenttempDataEntry, "온도(℃)");
        currenttempDataSet.setColors(new int[] {getResources().getColor(R.color.colorAccent), getResources().getColor(R.color.blue)});
        currenttempDataSet.setValueTextSize(15);

        currenttempData = new BarData(currenttempDataSet);
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
        currenttempChart.animateY(2000);
    }

    public void doCommu(String msg) {
        new CommuTask().execute(msg);//통신 쓰레드 시작
    }

    private class CommuTask extends AsyncTask<String, String, Object> {
        @Override
        protected String doInBackground(String... params) {
            try {
                taskString = params[0];

                socket = new Socket(ServerIP, ServerPort);
                mmInStream = socket.getInputStream();
                mmOutStream = socket.getOutputStream();

                mmOutStream.write(taskString.getBytes("UTF-8"));
                mmOutStream.flush();

                byte[] inbuff = new byte[1024];
                clearArray(inbuff);//buffer clear
                int len;
                len = mmInStream.read(inbuff);

                return new String(inbuff, 0, len, "UTF-8");
            } catch (Throwable t) {
                return t.toString();
            }
        }

        @Override
        protected void onPostExecute(Object result) {
            if (result instanceof Exception) {
                logMessege(result.toString());
            } else {
                doSetResultText(result.toString());
            }
        }
    }

    public void doSetResultText(String text) {
        if (taskString.equals(REQUEST_CURRENT_TEMP))
        {
            float[] current_tempTokens = new float[2];

            StringTokenizer str = new StringTokenizer(text, "/");

            int countTokens = str.countTokens();

            for (int i = 0; i < countTokens; i++)
            {
                current_tempTokens[i] = Float.parseFloat(str.nextToken());
            }

            setChartData(current_tempTokens[0], current_tempTokens[1]);
        }
    }

    public void clearArray(byte[] buff) {
        for (int i = 0; i < buff.length; i++)
        {
            buff[i] = 0;
        }
    }

    public void logMessege(String log) {
        logMsg = Toast.makeText(getActivity(), log, Toast.LENGTH_SHORT);
        logMsg.show();
    }
}
