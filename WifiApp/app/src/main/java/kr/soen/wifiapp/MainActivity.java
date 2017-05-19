package kr.soen.wifiapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    public static final int ServerPort = 5000;
    public static final String ServerIP = "54.71.172.224";
    //public static final String ServerIP = "14.46.3.96";

    public static final String REQUEST_CURRENT_TEMP_HUM = "1";
    public static final String REQUEST_ACCRUE_TEMP = "2";
    public static final String REQUEST_STATE = "3";
    public static final String COLSE = "5";

    Socket socket = null;
    public InputStream mmInStream = null;
    public OutputStream mmOutStream = null;
    String taskString;
    boolean isConnect = false;

    ViewPager vp;
    LinearLayout ll;
    SharedPreferences pref;
    BackPressCloseHandler backPressCloseHandler;

    Toast logMsg;

    Object result;

    String currentTempHumData = "0/0/0/0";
    String accrueTempData = "0/0/0/0/0/0/0/0/0/0/0/0/0/0/0/0";
    String stateData = "0/0/0/0/0/0/0/0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = getSharedPreferences("pref", MODE_PRIVATE);
        if (pref.getBoolean("isfirst", true))
        {
            logMessege("환경설정에서 계정을 등록하세요.");
        }

        vp = (ViewPager)findViewById(R.id.vp);
        ll = (LinearLayout)findViewById(R.id.ll);

        TextView tab1 = (TextView)findViewById(R.id.tab_1);
        TextView tab2 = (TextView)findViewById(R.id.tab_2);

        vp.setAdapter(new pagerAdapter(getSupportFragmentManager()));
        vp.setCurrentItem(0);

        tab1.setOnClickListener(this);
        tab1.setTag(0);
        tab2.setOnClickListener(this);
        tab2.setTag(1);

        tab1.setSelected(true);

        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position)
            {
                int i = 0;
                while(i < 2)
                {
                    if(position == i)
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

        ImageButton controlBtn = (ImageButton)findViewById(R.id.control_btn);
        controlBtn.setOnClickListener(this);

        ImageButton userBtn = (ImageButton)findViewById(R.id.user_btn);
        userBtn.setOnClickListener(this);

        backPressCloseHandler = new BackPressCloseHandler(this);//뒤로가기 handler
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
                    case REQUEST_STATE:
                        stateData = result.toString();
                        break;
                }

            }
        } catch (Exception e)
        {
            logMessege(e.toString());
        }
    }

    public void doClose()
    {
        new CloseTask().execute(COLSE);
    }

    private class CommuTask extends AsyncTask<String, String, Object> {
        @Override
        protected String doInBackground(String... params) {
            try {
                taskString = params[0];

                if (!isConnect)
                {
                    socket = new Socket(ServerIP, ServerPort);
                    Log.d("SOCKET", "서버 연결 요청 _ MAIN 소켓 생성");
                    isConnect = true;
                }

                if(mmInStream == null) {
                    mmInStream = socket.getInputStream();
                    mmOutStream = socket.getOutputStream();
                    Log.d("SOCKET","stream open _ MAIN");
                    if(mmInStream == null){
                        Log.d("SOCKET","서버 연결 안됨 _ MAIN");
                    }else{
                        Log.d("SOCKET","서버 연결 성공 _ MAIN");
                    }
                }

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

    private class CloseTask extends AsyncTask<String, String, Object> {
        @Override
        protected String doInBackground(String... params) {
            try {
                taskString = params[0];

                mmOutStream.write(taskString.getBytes("UTF-8"));
                mmOutStream.flush();

                mmInStream.close();
                mmInStream = null;
                mmOutStream.close();
                mmOutStream = null;
                socket.close();
                socket = null;

                isConnect = false;

                String result = "S";

                return result;
            } catch (Throwable t) {
                return t.toString();
            }
        }

        @Override
        protected void onPostExecute(Object result) {}
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
        if(v.getId() == R.id.user_btn)
        {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
        }
        else if(v.getId() == R.id.control_btn)
        {
            if (!pref.getString("id", "").equals(""))
            {
                Log.d("SOCKET", "서버 연결 요청 _ Main");
                doCommu(REQUEST_STATE);
                doClose();
            }

            Intent intent = new Intent(MainActivity.this, ControlActivity.class);
            intent.putExtra("state", stateData);
            startActivity(intent);
        }
        else
        {
            int tag = (int)v.getTag();

            int i = 0;
            while(i < 2)
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
            }
            return null;
        }

        @Override
        public int getCount()
        {
            return 2;
        }
    }

    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }

    public void logMessege(String log) {
        logMsg = Toast.makeText(this, log, Toast.LENGTH_LONG);
        logMsg.show();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        if (isConnect)
        {
            doClose();
        }

        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isfirst", false);
        editor.commit();
    }
}
