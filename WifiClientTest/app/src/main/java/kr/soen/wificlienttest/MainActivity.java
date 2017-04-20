package kr.soen.wificlienttest;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.Toast;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    BackPressCloseHandler backPressCloseHandler;

    public WifiManager wifiManager;

    IntentFilter filter;
    AlertDialog.Builder dialog;
    WRhandler handler;

    Toast logMsg;

    boolean isConnect = false;
    String taskString;

    public static final int ServerPort = 5000;
    public static final String ServerIP = "118.41.247.153";

    Socket socket;

    public InputStream mmInStream;
    public OutputStream mmOutStream;

    public static final String REQUEST_All_DATA = "1";
    public static final String REQUEST_CURRENT_TEMP = "2";
    public static final String REQUEST_ACCRUE_TEMP = "3";
    public static final String REQUEST_HUM = "4";
    public static final String REQUEST_CONTROL = "5";
    public static final String COLSE = "6";

    Temp1Fragment temp1Fragment;
    HumFragment humFragment;
    ControlFragment controlFragment;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        backPressCloseHandler = new BackPressCloseHandler(this);//뒤로가기 handler

        wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        filter = new IntentFilter();
        filter.addAction(wifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(WifiRecv, filter);

        if (wifiManager.isWifiEnabled())
        {
            //doCommu(REQUEST_All_DATA);
        }
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
                    dialog.show();
                    temp1Fragment.wifiState(wifiManager.isWifiEnabled());
                    controlFragment.wifiState(wifiManager.isWifiEnabled());
                    break;

                case 2:
                    logMessege("WiFi가 활성화되었습니다.");
                    temp1Fragment.wifiState(wifiManager.isWifiEnabled());
                    controlFragment.wifiState(wifiManager.isWifiEnabled());
                    break;
            }
        }
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
                if(!isConnect) {
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
        if(taskString.equals(REQUEST_All_DATA))
        {

        }
        else if (taskString.equals(REQUEST_CURRENT_TEMP))
        {
            float[] current_tempTokens = new float[2];

            StringTokenizer str = new StringTokenizer(text, "/");

            int countTokens = str.countTokens();

            for (int i = 0; i < countTokens; i++)
            {
                current_tempTokens[i] = Float.parseFloat(str.nextToken());
            }

            temp1Fragment.setChartData(current_tempTokens[0], current_tempTokens[1]);
        }
        else if (taskString.equals(REQUEST_ACCRUE_TEMP))
        {

        }
        else if (taskString.equals(REQUEST_HUM))
        {
            String[] dataTokens = new String[2];

            StringTokenizer str = new StringTokenizer(text, "/");

            int countTokens = str.countTokens();

            for (int i = 0; i < countTokens; i++)
            {
                dataTokens[i] = str.nextToken();
            }

            humFragment.setTextView(dataTokens[0], dataTokens[1]);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return Temp1Fragment.newInstance(wifiManager.isWifiEnabled());
                case 1:
                    return Temp2Fragment.newInstance(position + 1);
                case 2:
                    return HumFragment.newInstance(position + 1);
                case 3:
                    return ControlFragment.newInstance(wifiManager.isWifiEnabled());
            }
            return null;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "현재 온도";
                case 1:
                    return "누적 온도";
                case 2:
                    return "습도";
                case 3:
                    return "제어";
            }
            return null;
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
