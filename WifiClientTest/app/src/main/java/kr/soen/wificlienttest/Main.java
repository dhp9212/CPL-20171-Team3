package kr.soen.wificlienttest;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import android.net.wifi.WifiManager;

import java.net.InetAddress;
import java.net.Socket;

public class Main extends AppCompatActivity implements OnClickListener{
    public WifiManager wifiManager;

    public InputStream mmInStream;
    public OutputStream mmOutStream;

    public DataInputStream dis;
    public DataOutputStream dos;

    BackPressCloseHandler backPressCloseHandler;
    ActionBar title;
    RelativeLayout background;
    TextView btState;
    TextView tempText1;
    TextView tempText2;
    Button currenttempBtn;
    Button controlBtn;
    Toast logMsg;

    boolean isConnect = false;
    String taskString;
    IntentFilter filter;
    AlertDialog.Builder dialog;
    WRhandler handler;

    public static final String REQUEST_DATA = "1";
    public static final String REQUEST_CONTROL = "2";
    public static final String COLSE = "3";

    public static final int ServerPort = 5000;
    public static final String ServerIP = "118.41.247.153";

    Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        backPressCloseHandler = new BackPressCloseHandler(this);//뒤로가기 handler

        background = (RelativeLayout) findViewById(R.id.activity_main);//background layout 초가화

        //title bar 초기화
        title = getSupportActionBar();
        title.setBackgroundDrawable(new ColorDrawable(0xff4c8eff));

        //현재 블루투스 상태 표시 text 초기화
        btState = (TextView)findViewById(R.id.wifistate);

        //현재 온도 표시 text 초기화
        tempText1 = (TextView) findViewById(R.id.temptext1);
        tempText1.setTypeface(Typeface.createFromAsset(getAssets(), "NanumBarunGothicUltraLight.ttf"));

        //온도 단위 표시 text 설정(이후 변화 없음)
        tempText2 = (TextView) findViewById(R.id.temptext2);
        tempText2.setTypeface(Typeface.createFromAsset(getAssets(), "NanumBarunGothicUltraLight.ttf"));

        //현재 온도 표시 button 설정
        currenttempBtn = (Button)findViewById(R.id.currenttemp_btn);
        currenttempBtn.setOnClickListener(this);

        //제어 button 설정
        controlBtn = (Button)findViewById(R.id.control_btn);
        controlBtn.setOnClickListener(this);

        wifiManager = (WifiManager)getSystemService(WIFI_SERVICE);

        filter = new IntentFilter();
        filter.addAction(wifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(WifiRecv, filter);

        if (wifiManager.isWifiEnabled())
        {
            logMessege("WiFi가 이미 활성화되었습니다.");

            btState.setText("WiFi가 활성화되었습니다.");
        }
        else
        {
            btState.setText("WiFi가 활성화되지 않았습니다.");

            dialog = new AlertDialog.Builder(Main.this);
            dialog.setTitle("WiFi가 활성화되지 않았습니다.");
            dialog.setMessage("WiFi를 활성화 하시겠습니까?");

            // OK 버튼 이벤트
            dialog.setPositiveButton("예", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // 사용자에게 블루투스 활성화 요청
                    wifiManager.setWifiEnabled(true);
                }
            });

            // Cancel 버튼 이벤트
            dialog.setNegativeButton("아니요",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    logMessege("WiFi 활성화가 취소되었습니다.");
                    dialog.cancel();
                }
            });
            dialog.show();
        }
    }

    public void doCommu(String msg) {
        new CommuTask().execute(msg);//통신 쓰레드 시작
    }

    public void doClose() {
        new CloseTask().execute();//종료쓰레드 시작
    }

    BroadcastReceiver WifiRecv = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            handler = new WRhandler();

            String action = intent.getAction();

            if(action.equals(wifiManager.WIFI_STATE_CHANGED_ACTION))
            {
                if (wifiManager.getWifiState() == wifiManager.WIFI_STATE_DISABLED)
                {
                    handler.sendEmptyMessage(1);

                }
                else if (wifiManager.getWifiState() == wifiManager.WIFI_STATE_ENABLED)
                {
                    handler.sendEmptyMessage(2);
                }
            }


        }
    };

    class WRhandler extends Handler
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);

            switch (msg.what)
            {
                case 1:
                    logMessege("WiFi가 비활성화 되었습니다.");
                    btState.setText("WiFi가 활성화되지 않았습니다.");
                    dialog.show();
                    break;

                case 2:
                    logMessege("WiFi가 활성화되었습니다.");
                    btState.setText("WiFi가 활성화되었습니다.");
                    break;
            }
        }
    }

    private class CommuTask extends AsyncTask<String, String, Object> {
        @Override
        protected String doInBackground(String... params) {
            try {
                if(!isConnect) {
                    //InetAddress serverAddr = InetAddress.getByName(ServerIP);
                    socket = new Socket(ServerIP, ServerPort);
                    isConnect =true;
                }

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
        if(taskString.equals(REQUEST_DATA)) {
            tempText1.setText(text);
        }
        else if(taskString.equals(REQUEST_CONTROL))
        {
            logMessege(text);
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
            } else {
                isConnect = false;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.currenttemp_btn:
                if (wifiManager.isWifiEnabled())
                {
                    doCommu(REQUEST_DATA);
                    break;
                }

                logMessege("WiFi가 활성화되지 않았습니다.");
                break;
            case R.id.control_btn:
                if (wifiManager.isWifiEnabled())
                {
                    doCommu(REQUEST_CONTROL);
                    break;
                }

                logMessege("WiFi가 활성화되지 않았습니다.");
                break;
        }
    }

    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }

    public void logMessege(String log) {
        logMsg = Toast.makeText(this, log, Toast.LENGTH_SHORT);
        logMsg.show();
    }

    public void clearArray(byte[] buff) {
        for (int i = 0; i < buff.length; i++)
        {
            buff[i] = 0;
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        if(isConnect) {
            doClose();
        }

        unregisterReceiver(WifiRecv);
    }
}
