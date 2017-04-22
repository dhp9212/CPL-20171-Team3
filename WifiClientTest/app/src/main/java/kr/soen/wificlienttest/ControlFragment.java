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

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ControlFragment extends Fragment implements View.OnClickListener{
    public static final String REQUEST_CONTROL = "4";

    public static final int ServerPort = 5000;
    public static final String ServerIP = "14.46.3.105";

    Socket socket;
    public InputStream mmInStream;
    public OutputStream mmOutStream;
    String taskString;

    SharedPreferences pref;
    WifiManager wifiManager;
    Toast logMsg;

    Button controlBtn;

    public ControlFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_control, container, false);

        wifiManager = (WifiManager)getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);

        //제어 button 설정
        controlBtn = (Button)rootView.findViewById(R.id.control_btn);
        controlBtn.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.control_btn:
                if (wifiManager.isWifiEnabled())
                {
                    if(!pref.getString("id", "").equals(""))
                    {
                        doCommu(REQUEST_CONTROL);
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
        if (taskString.equals(REQUEST_CONTROL))
        {
            logMessege(text);
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