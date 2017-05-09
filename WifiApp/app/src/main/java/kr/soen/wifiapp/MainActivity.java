package kr.soen.wifiapp;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    public static final int ServerPort = 5000;
    public static final String ServerIP = "54.71.172.224 ";
    //public static final String ServerIP = "118.41.247.141";

    public static final String REQUEST_CURRENT_TEMP_HUM = "1";
    public static final String REQUEST_ACCRUE_TEMP = "2";
    public static final String REQUEST_CONTROL = "3";

    Socket socket;
    public InputStream mmInStream;
    public OutputStream mmOutStream;
    String taskString;

    ViewPager vp;
    LinearLayout ll;
    SharedPreferences pref;
    BackPressCloseHandler backPressCloseHandler;

    WifiManager wifiManager;
    IntentFilter filter;
    WRhandler handler;
    AlertDialog.Builder dialog;
    Toast logMsg;

    Object result;

    String currentTempHumData = "0/0/0/0";
    String accrueTempData = "0/0/0/0/0/0/0/0/0/0/0/0/0/0/0/0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        pref = getSharedPreferences("pref", MODE_PRIVATE);
        if (pref.getBoolean("isfirst", true))
        {
            logMessege("환경설정에서 계정을 등록하세요.");
        }

        if(!pref.getString("id", "").equals(""))
        {
            doCommu(REQUEST_ACCRUE_TEMP);
        }

        vp = (ViewPager)findViewById(R.id.vp);
        ll = (LinearLayout)findViewById(R.id.ll);

        TextView currenttemphumTab = (TextView)findViewById(R.id.tab_current_temp_hum);
        TextView accruetempTab = (TextView)findViewById(R.id.tab_accrue_temp);
        TextView controlTab = (TextView)findViewById(R.id.tab_control);

        vp.setAdapter(new pagerAdapter(getSupportFragmentManager()));
        vp.setCurrentItem(0);

        currenttemphumTab.setOnClickListener(this);
        currenttemphumTab.setTag(0);
        accruetempTab.setOnClickListener(this);
        accruetempTab.setTag(1);
        controlTab.setOnClickListener(this);
        controlTab.setTag(2);

        currenttemphumTab.setSelected(true);

        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position)
            {
                int i = 0;
                while(i<3)
                {
                    if(position==i)
                    {
                        ll.findViewWithTag(i).setSelected(true);
                    }
                    else
                    {
                        ll.findViewWithTag(i).setSelected(false);
                    }
                    i++;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        backPressCloseHandler = new BackPressCloseHandler(this);//뒤로가기 handler

        filter = new IntentFilter();
        filter.addAction(wifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(WifiRecv, filter);
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
                    break;

                case 2:
                    logMessege("WiFi가 활성화되었습니다.");
                    break;
            }
        }
    }

    public void doCommu(String msg)
    {
        try {
            result = new CommuTask().execute(msg).get();

            if (result instanceof Exception)
            {
                logMessege(result.toString());
            }
            else
            {
                switch (msg)
                {
                    case REQUEST_CURRENT_TEMP_HUM:
                        currentTempHumData = result.toString();
                        break;
                    case REQUEST_ACCRUE_TEMP:
                        accrueTempData = result.toString();
                        break;
                    case REQUEST_CONTROL:
                        logMessege(result.toString());
                        break;
                }

            }
        } catch (Exception e)
        {
            logMessege(e.toString());
        }
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
        protected void onPostExecute(Object result) { }
    }

    public void clearArray(byte[] buff) {
        for (int i = 0; i < buff.length; i++)
        {
            buff[i] = 0;
        }
    }

    @Override
    public void onClick(View v)
    {
        int tag = (int)v.getTag();

        int i = 0;
        while(i < 3)
        {
            if(tag == i)
            {
                ll.findViewWithTag(i).setSelected(true);
            }
            else
            {
                ll.findViewWithTag(i).setSelected(false);
            }
            i++;
        }

        vp.setCurrentItem(tag);
    }


    private class pagerAdapter extends FragmentStatePagerAdapter
    {
        public pagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            switch (position)
            {
                case 0:
                    return new TempHumFragment();
                case 1:
                    return new AccrueTempFragment();
                case 2:
                    return new ControlFragment();
            }
            return null;
        }

        @Override
        public int getCount()
        {
            return 3;
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

        switch (id)
        {
            case R.id.action_settings:
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }

    public void logMessege(String log) {
        logMsg = Toast.makeText(this, log, Toast.LENGTH_SHORT);
        logMsg.show();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isfirst", false);
        editor.commit();

        unregisterReceiver(WifiRecv);
    }
}
