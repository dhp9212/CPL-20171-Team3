package kr.soen.wifiapp;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    public static final int ServerPort = 5000;
    public static final String ServerIP = "54.71.172.224";
    //public static final String ServerIP = "14.46.3.23";

    public static final String REQUEST_CURRENT_TEMP_HUM = "1";
    public static final String REQUEST_ACCRUE_TEMP = "2";
    public static final String REQUEST_STATE = "3";
    public static final String COLSE = "6";

    Socket socket = null;
    public InputStream mmInStream = null;
    public OutputStream mmOutStream = null;
    String taskString;
    boolean isConnect = false;

    String isSuccess;
    Object result_s;

    ViewPager vp;
    BackPressCloseHandler backPressCloseHandler;

    Toast logMsg;

    Object result;

    String currentTempHumData = "0/0/0/0";
    String accrueTempData = "0/0/0/0/0/0/0/0/0/0/0/0/0/0/0/0";
    String stateData = "0/0/0/0/0/0/0/0";

    String id;

    SoundPool sound;
    int soundIdUvOn, soundIdHeatOn, soundIdHumOn, soundIdAutoOn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        backPressCloseHandler = new BackPressCloseHandler(this);//뒤로가기 handler

        vp = (ViewPager)findViewById(R.id.vp);
        vp.setAdapter(new pagerAdapter(getSupportFragmentManager()));
        vp.setCurrentItem(1);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            sound = new SoundPool.Builder().setAudioAttributes(audioAttributes).setMaxStreams(8).build();
        }
        else {
            sound = new SoundPool(8, AudioManager.STREAM_NOTIFICATION, 0);
        }

        soundIdUvOn = sound.load(this, R.raw.voice_uv_on, 1);
        soundIdHeatOn = sound.load(this, R.raw.voice_heater_on, 1);
        soundIdHumOn = sound.load(this, R.raw.voice_hum_on, 1);
        soundIdAutoOn = sound.load(this, R.raw.voice_auto_on, 1);
    }

    @Override
    protected void onStop() {
        super.onStop();

        soundIdUvOn = 0;
        soundIdHeatOn = 0;
        soundIdHumOn = 0;
        soundIdAutoOn = 0;

        sound.release();
        sound = null;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        if (isConnect)
        {
            doClose();
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
                    return new ProfileFragment();
                case 1:
                    return new TempHumFragment();
                case 2:
                    return new CameraFragment();
            }
            return null;
        }

        @Override
        public int getCount()
        {
            return 3;
        }
    }

    public void doCommu(String msg)
    {
        try {
            String data = msg + "/" + id;
            result = new CommuTask().execute(data).get();

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

    public void doCommuForState(String msg)
    {
        try {
            result_s = new CommuTask().execute(msg).get();

            if (result_s instanceof Exception)
            {
                logMessege(result_s.toString());
            }
            else
            {
                isSuccess = result_s.toString();
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
            } catch (IOException i) {
                finish();
                return i.toString();
            }
            catch (Throwable t) {
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

    public void soundPlay(int flag)
    {
        switch (flag)
        {
            case 1:
                sound.play(soundIdUvOn, 1f, 1f, 0, 0, 1f);
                break;
            case 2:
                sound.play(soundIdHeatOn, 1f, 1f, 0, 0, 1f);
                break;
            case 3:
                sound.play(soundIdHumOn, 1f, 1f, 0, 0, 1f);
                break;
            case 4:
                sound.play(soundIdAutoOn, 1f, 1f, 0, 0, 1f);
                break;
        }
    }

    public void clearArray(byte[] buff) {
        for (int i = 0; i < buff.length; i++)
        {
            buff[i] = 0;
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
}
