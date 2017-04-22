package kr.soen.wificlienttest;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
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

public class MainActivity extends AppCompatActivity {
    SharedPreferences pref;
    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;

    BackPressCloseHandler backPressCloseHandler;

    public WifiManager wifiManager;

    IntentFilter filter;
    AlertDialog.Builder dialog;
    WRhandler handler;

    Toast logMsg;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        //mViewPager.setOffscreenPageLimit(3);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        backPressCloseHandler = new BackPressCloseHandler(this);//뒤로가기 handler

        pref = getSharedPreferences("pref", MODE_PRIVATE);
        if (pref.getBoolean("isfirst", true))
        {
            logMessege("환경설정에서 계정을 등록하세요.");
        }

        wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);

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

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new Temp1Fragment();
                case 1:
                    return new Temp2Fragment();
                case 2:
                    return new HumFragment();
                case 3:
                    return new ControlFragment();
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
