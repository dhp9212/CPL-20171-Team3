package kr.soen.wificlienttest;

import android.animation.ValueAnimator;
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
import android.widget.TextView;
import android.widget.Toast;

import com.triggertrap.seekarc.SeekArc;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.StringTokenizer;

public class HumFragment extends Fragment implements View.OnClickListener{
    public static final String REQUEST_HUM = "3";

    public static final int ServerPort = 5000;
    public static final String ServerIP = "14.46.3.105";

    Socket socket;
    public InputStream mmInStream;
    public OutputStream mmOutStream;
    String taskString;

    SharedPreferences pref;
    WifiManager wifiManager;
    Toast logMsg;

    SeekArc currenthum;
    SeekArc avghum;
    TextView currenthumProgress;
    TextView avghumProgress;

    ValueAnimator animator1;
    ValueAnimator animator2;

    Button refreshBtn;

    int currenthumValue = 0;
    int avghumValue = 0;


    public HumFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_hum, container, false);

        wifiManager = (WifiManager)getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);

        currenthum = (SeekArc)rootView.findViewById(R.id.currenthumseekArc);
        currenthumProgress = (TextView)rootView.findViewById(R.id.currenthumseekArcProgress);
        currenthum.setProgressWidth(30);
        currenthum.setArcWidth(30);

        avghum = (SeekArc)rootView.findViewById(R.id.avghumseekArc);
        avghumProgress = (TextView)rootView.findViewById(R.id.avghumseekArcProgress);
        avghum.setProgressWidth(30);
        avghum.setArcWidth(30);

        setChartData(0, 0);

        //새로고침 button 설정
        refreshBtn = (Button)rootView.findViewById(R.id.refresh_btn);
        refreshBtn.setOnClickListener(this);

        return rootView;
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
                        doCommu(REQUEST_HUM);
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

    public void setChartData(int chValue, int ahValue)
    {
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
        animator1.start();

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
        animator2.start();
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
        if (taskString.equals(REQUEST_HUM))
        {
            int[] humTokens = new int[2];

            StringTokenizer str = new StringTokenizer(text, "/");

            int countTokens = str.countTokens();

            for (int i = 0; i < countTokens; i++)
            {
                humTokens[i] = Integer.parseInt(str.nextToken());
            }

            setChartData(humTokens[0], humTokens[1]);
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