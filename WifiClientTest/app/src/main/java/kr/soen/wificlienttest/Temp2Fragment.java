package kr.soen.wificlienttest;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

public class Temp2Fragment extends Fragment implements View.OnClickListener{
    public static final String REQUEST_ACCRUE_TEMP = "2";

    public static final int ServerPort = 5000;
    public static final String ServerIP = "14.46.3.105";

    Socket socket;
    public InputStream mmInStream;
    public OutputStream mmOutStream;
    String taskString;

    SharedPreferences pref;
    WifiManager wifiManager;
    Toast logMsg;

    LineChart accruetempChart;
    LineData accruetempData;

    Button refreshBtn;


    public Temp2Fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_temp2, container, false);

        wifiManager = (WifiManager)getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);

        accruetempChart = (LineChart) rootView.findViewById(R.id.accrue_temp_chart);
        accruetempChart.getAxisLeft().setEnabled(false);
        accruetempChart.getAxisRight().setEnabled(false);
        accruetempChart.getAxisLeft().setAxisMaximum(36);
        accruetempChart.getAxisLeft().setAxisMinimum(20);
        accruetempChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        accruetempChart.getDescription().setEnabled(false);
        accruetempChart.setTouchEnabled(false);
        accruetempChart.setDragEnabled(false);
        accruetempChart.setScaleEnabled(false);

        float[] init_hot = {0, 0, 0, 0, 0, 0, 0, 0};
        float[] init_cool = {0, 0, 0, 0, 0, 0, 0, 0};
        setChartData(init_hot, init_cool);

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
            accruetempChart.animateX(2000);
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
                        doCommu(REQUEST_ACCRUE_TEMP);
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

    public void setChartData(float[] hotzone, float[] coolzone)
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
        if (taskString.equals(REQUEST_ACCRUE_TEMP))
        {
            float[] hot_accrue_tempTokens = new float[8];
            float[] cool_accrue_tempTokens = new float[8];

            StringTokenizer str = new StringTokenizer(text, "/");

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

            setChartData(hot_accrue_tempTokens, cool_accrue_tempTokens);
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
