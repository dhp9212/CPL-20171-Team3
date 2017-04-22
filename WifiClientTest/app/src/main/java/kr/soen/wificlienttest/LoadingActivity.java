package kr.soen.wificlienttest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.StringTokenizer;

public class LoadingActivity extends AppCompatActivity {
    SharedPreferences pref;

    public WifiManager wifiManager;
    AlertDialog.Builder dialog;

    Toast logMsg;

    public static final int ServerPort = 5000;
    public static final String ServerIP = "118.41.247.153";

    public static final String REQUEST_All_DATA_NOT_LOCK = "1";
    public static final String REQUEST_All_DATA_LOCK = "2";
    public static final String COLSE = "6";

    Socket socket;
    String taskString;
    public InputStream mmInStream;
    public OutputStream mmOutStream;

    Intent intent1;
    Intent intent2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        try {
            socket = new Socket(ServerIP, ServerPort);
        } catch (IOException e)
        {
            logMessege(e.toString());
        }

        wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (wifiManager.isWifiEnabled())
        {
            pref = getSharedPreferences("pref", MODE_PRIVATE);

            startLoading();//로딩화면 2초 동안 출력
        }
        else
        {
            dialog = new AlertDialog.Builder(this);
            dialog.setTitle("WiFi가 활성화되지 않았습니다.");
            dialog.setMessage("WiFi를 활성화 하시겠습니까?");

            // OK 버튼 이벤트
            dialog.setPositiveButton("예", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // 사용자에게 블루투스 활성화 요청
                    wifiManager.setWifiEnabled(true);

                    pref = getSharedPreferences("pref", MODE_PRIVATE);

                    startLoading();//로딩화면 2초 동안 출력
                }
            });

            // Cancel 버튼 이벤트
            dialog.setNegativeButton("아니요",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    logMessege("WiFi 활성화가 취소되었습니다.");
                    dialog.cancel();
                    finish();
                }
            });
            dialog.show();
        }
    }

    private void startLoading() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                boolean isfirst = pref.getBoolean("isfirst", true);
                if(isfirst)
                {
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(intent);//main activity 실행
                    finish();
                }
                else
                {
                    boolean islock = pref.getBoolean("islock", false);
                    if (islock)
                    {
                        intent1 = new Intent(getBaseContext(), LoginActivity.class);
                        intent1.putExtra("commu tool", new SSSerial(socket, mmInStream, mmOutStream));
                        doCommu(REQUEST_All_DATA_LOCK);
                        startActivity(intent1);//login activity 실행
                        finish();
                    }
                    else
                    {
                        intent2 = new Intent(getBaseContext(), MainActivity.class);
                        intent2.putExtra("commu tool", new SSSerial(socket, mmInStream, mmOutStream));
                        doCommu(REQUEST_All_DATA_NOT_LOCK);
                        startActivity(intent2);//main activity 실행
                        finish();
                    }
                }

            }
        }, 2000);
    }

    public void doCommu(String msg) {
        new CommuTask().execute(msg);//통신 쓰레드 시작
    }

    public void doClose() {
        new CloseTask().execute();//종료쓰레드 시작
    }

    private class CommuTask extends AsyncTask<String, String, Object> {
        @Override
        protected String doInBackground(String... params) {
            try {
                mmOutStream = socket.getOutputStream();
                mmInStream = socket.getInputStream();

                taskString = params[0];

                mmOutStream.write(taskString.getBytes("UTF-8"));
                mmOutStream.flush();

                byte[] inbuff = new byte[1024];
                clearArray(inbuff);//buffer clear
                int len;
                len = mmInStream.read(inbuff);

                return new String(inbuff, 0, len, "UTF-8");
            } catch (Throwable t) {
                doClose();
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
        if(taskString.equals(REQUEST_All_DATA_LOCK))
        {
            float[] dataTokens = new float[20];

            StringTokenizer str = new StringTokenizer(text, "/");

            int countTokens = str.countTokens();

            for (int i = 0; i < countTokens; i++)
            {
                dataTokens[i] = Float.parseFloat(str.nextToken());
            }

            intent1.putExtra("ALL_DATA", dataTokens);
        }
        else if (taskString.equals(REQUEST_All_DATA_NOT_LOCK))
        {
            float[] dataTokens = new float[20];

            StringTokenizer str = new StringTokenizer(text, "/");

            int countTokens = str.countTokens();

            for (int i = 0; i < countTokens; i++)
            {
                dataTokens[i] = Float.parseFloat(str.nextToken());
            }

            intent2.putExtra("ALL_DATA", dataTokens);
        }
    }

    private class CloseTask extends AsyncTask<Void, Void, Object> {
        @Override
        protected Object doInBackground(Void... params) {
            try {
                mmOutStream.write(COLSE.getBytes("UTF-8"));
                mmOutStream.flush();

                try{mmOutStream.close();}catch(Throwable t){/*ignore*/}
                try{mmInStream.close();}catch(Throwable t){/*ignore*/}
                socket.close();
            } catch (Throwable t) {
                return t;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            if (result instanceof Throwable) {
                logMessege(result.toString());
            }
        }
    }

    public void clearArray(byte[] buff) {
        for (int i = 0; i < buff.length; i++)
        {
            buff[i] = 0;
        }
    }

    public void logMessege(String log) {
        logMsg = Toast.makeText(this, log, Toast.LENGTH_SHORT);
        logMsg.show();
    }
}
