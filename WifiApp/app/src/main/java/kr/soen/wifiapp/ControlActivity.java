package kr.soen.wifiapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.StringTokenizer;

public class ControlActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int ServerPort = 5000;
    public static final String ServerIP = "54.71.172.224";
    //public static final String ServerIP = "14.46.3.96";

    public static final String REQUEST_STATE = "3";
    public static final String COLSE = "5";

    public static final String APP_LIGHT_ON = "10";
    public static final String APP_LIGHT_OF = "11";
    public static final String APP_LIGHT_AU = "12";

    public static final String APP_HITTE_ON = "20";
    public static final String APP_HITTE_OF = "21";
    public static final String APP_HITTE_AU = "22";

    public static final String APP_HUMID_ON = "30";
    public static final String APP_HUMID_OF = "31";
    public static final String APP_HUMID_AU = "32";

    public static final String APP_MOTOR_LE = "40";
    public static final String APP_MOTOR_RI = "41";
    public static final String APP_MOTOR_OF = "42";

    Socket socket;
    public InputStream mmInStream = null;
    public OutputStream mmOutStream = null;
    String taskString;
    boolean isConnect = false;
    String isSuccess;

    Object result_s;

    String stateData = "0/0/0/0/0/0/0/0";

    SharedPreferences pref;
    Toast logMsg;

    Switch motorSwitch;

    int uvState, heaterState, humState, motorSideState;
    int uvAutoState, heaterAutoState, humAutoState, motorState;

    SoundPool sound;
    int soundIdUvOn, soundIdHeatOn, soundIdHumOn, soundIdAutoOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(this.getClass().getSimpleName(), "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        Intent intent = getIntent();
        stateData = intent.getStringExtra("state");

        pref = getSharedPreferences("pref", Context.MODE_PRIVATE);

        setValue(stateData);


        TextView title = (TextView) findViewById(R.id.text3);
        title.setTypeface(Typeface.createFromAsset(getAssets(), "NanumBarunpenR.ttf"));


        TextView uvText = (TextView) findViewById(R.id.text_uv);
        uvText.setTypeface(Typeface.createFromAsset(getAssets(), "NanumBarunpenR.ttf"));

        ToggleButton uvToggle = (ToggleButton) findViewById(R.id.uv_toggle);
        uvToggle.setTypeface(Typeface.createFromAsset(getAssets(), "NanumBarunpenR.ttf"));
        uvToggle.setOnClickListener(this);
        if(uvState == 1)
        {
            uvToggle.setChecked(true);
        }

        ToggleButton uvAutoToggle = (ToggleButton) findViewById(R.id.uv_auto_toggle);
        uvAutoToggle.setTypeface(Typeface.createFromAsset(getAssets(), "NanumBarunpenR.ttf"));
        uvAutoToggle.setOnClickListener(this);
        if(uvAutoState == 1)
        {
            uvAutoToggle.setChecked(true);
        }


        TextView heaterText = (TextView) findViewById(R.id.text_heater);
        heaterText.setTypeface(Typeface.createFromAsset(getAssets(), "NanumBarunpenR.ttf"));

        ToggleButton heaterToggle = (ToggleButton) findViewById(R.id.heat_toggle);
        heaterToggle.setTypeface(Typeface.createFromAsset(getAssets(), "NanumBarunpenR.ttf"));
        heaterToggle.setOnClickListener(this);
        if (heaterState == 1)
        {
            heaterToggle.setChecked(true);
        }

        ToggleButton heaterAutoToggle = (ToggleButton) findViewById(R.id.heat_auto_toggle);
        heaterAutoToggle.setTypeface(Typeface.createFromAsset(getAssets(), "NanumBarunpenR.ttf"));
        heaterAutoToggle.setOnClickListener(this);
        if (heaterAutoState == 1)
        {
            heaterAutoToggle.setChecked(true);
        }


        TextView humText = (TextView) findViewById(R.id.text_hum);
        humText.setTypeface(Typeface.createFromAsset(getAssets(), "NanumBarunpenR.ttf"));

        ToggleButton humToggle = (ToggleButton) findViewById(R.id.hum_toggle);
        humToggle.setTypeface(Typeface.createFromAsset(getAssets(), "NanumBarunpenR.ttf"));
        humToggle.setOnClickListener(this);
        if (humState == 1)
        {
            humToggle.setChecked(true);
        }

        ToggleButton humAutoToggle = (ToggleButton) findViewById(R.id.hum_auto_toggle);
        humAutoToggle.setTypeface(Typeface.createFromAsset(getAssets(), "NanumBarunpenR.ttf"));
        humAutoToggle.setOnClickListener(this);
        if (humAutoState == 1)
        {
            humAutoToggle.setChecked(true);
        }


        TextView motorText = (TextView) findViewById(R.id.text_motor);
        motorText.setTypeface(Typeface.createFromAsset(getAssets(), "NanumBarunpenR.ttf"));

        TextView leftText = (TextView) findViewById(R.id.text_L);
        leftText.setTypeface(Typeface.createFromAsset(getAssets(), "NanumBarunpenR.ttf"));

        TextView rightText = (TextView) findViewById(R.id.text_R);
        rightText.setTypeface(Typeface.createFromAsset(getAssets(), "NanumBarunpenR.ttf"));

        motorSwitch = (Switch) findViewById(R.id.motor_side_switch);
        motorSwitch.setOnClickListener(this);
        if (motorSideState == 1)
        {
            motorSwitch.setChecked(true);
        }

        ToggleButton motorToggle = (ToggleButton) findViewById(R.id.motor_toggle);
        motorToggle.setTypeface(Typeface.createFromAsset(getAssets(), "NanumBarunpenR.ttf"));
        motorToggle.setOnClickListener(this);
        if (motorState == 1)
        {
            motorToggle.setChecked(true);
            motorSwitch.setThumbResource(R.drawable.switch_thumb_on);
            motorSwitch.setTrackResource(R.drawable.switch_track_on);
        }
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
        Log.d(this.getClass().getSimpleName(), "onDestroy()");
        super.onDestroy();

        if (isConnect)
        {
            doClose();
        }

    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.uv_toggle:
                if(!pref.getString("id", "").equals(""))
                {
                    if (uvState == 0)
                    {
                        Log.d("CONTROL","LIGHT_ON 누름");
                        doCommuForState(APP_LIGHT_ON);

                        if(isSuccess.equals("S")) {
                            Log.d("CONTROL","S를 받음");
                            sound.play(soundIdUvOn, 1f, 1f, 0, 0, 1f);
                            ((ToggleButton)v).setTextColor(getResources().getColor(R.color.green));
                            ((ToggleButton)v).setChecked(true);
                            uvState = 1;
                            break;
                        }
                        break;
                    }
                    else
                    {
                        Log.d("CONTROL","LIGHT_OFF 누름");
                        doCommuForState(APP_LIGHT_OF);

                        if(isSuccess.equals("S")) {
                            Log.d("CONTROL","S를 받음");
                            ((ToggleButton)v).setTextColor(getResources().getColor(R.color.gray_semi));
                            ((ToggleButton)v).setChecked(false);
                            uvState = 0;
                            break;
                        }
                        break;
                    }
                }
                else
                {
                    logMessege("계정이 등록되지 않았습니다");
                    break;
                }
            case R.id.uv_auto_toggle:
                if(!pref.getString("id", "").equals(""))
                {
                    if (uvAutoState == 0)
                    {
                        Log.d("CONTROL","LIGHT_AU 누름");
                        doCommuForState(APP_LIGHT_AU);

                        if (isSuccess.equals("S")) {
                            Log.d("CONTROL","S를 받음");
                            sound.play(soundIdAutoOn, 1f, 1f, 0, 0, 1f);
                            ((ToggleButton)v).setTextColor(getResources().getColor(R.color.green));
                            ((ToggleButton)v).setChecked(true);
                            uvAutoState = 1;
                            break;
                        }
                        break;
                    }
                    else
                    {
                        Log.d("CONTROL","LIGHT_AU 누름");
                        doCommuForState(APP_LIGHT_AU);

                        if (isSuccess.equals("S")) {
                            Log.d("CONTROL","S를 받음");
                            ((ToggleButton)v).setTextColor(getResources().getColor(R.color.gray_semi));
                            ((ToggleButton)v).setChecked(false);
                            uvAutoState = 0;
                            break;
                        }
                        break;
                    }
                }
                else
                {
                    logMessege("계정이 등록되지 않았습니다");
                    break;
                }


            case R.id.heat_toggle:
                if(!pref.getString("id", "").equals(""))
                {
                    if (heaterState == 0)
                    {
                        Log.d("CONTROL","HITTE_ON 누름");
                        doCommuForState(APP_HITTE_ON);

                        if(isSuccess.equals("S")) {
                            Log.d("CONTROL", "S를 받음");
                            sound.play(soundIdHeatOn, 1f, 1f, 0, 0, 1f);
                            ((ToggleButton) v).setTextColor(getResources().getColor(R.color.green));
                            ((ToggleButton) v).setChecked(true);
                            heaterState = 1;
                            break;
                        }
                        break;
                    }
                    else
                    {
                        Log.d("CONTROL","HITTE_OF 누름");
                        doCommuForState(APP_HITTE_OF);

                        if(isSuccess.equals("S")) {
                            Log.d("CONTROL", "S를 받음");
                            ((ToggleButton) v).setTextColor(getResources().getColor(R.color.gray_semi));
                            ((ToggleButton) v).setChecked(false);
                            heaterState = 0;
                            break;
                        }
                        break;
                    }
                }
                else
                {
                    logMessege("계정이 등록되지 않았습니다");
                    break;
                }
            case R.id.heat_auto_toggle:
                if(!pref.getString("id", "").equals(""))
                {
                    if (heaterAutoState == 0)
                    {
                        Log.d("CONTROL","HITTE_AU 누름");
                        doCommuForState(APP_HITTE_AU);

                        if(isSuccess.equals("S")) {
                            Log.d("CONTROL", "S를 받음");
                            sound.play(soundIdAutoOn, 1f, 1f, 0, 0, 1f);
                            ((ToggleButton) v).setTextColor(getResources().getColor(R.color.green));
                            ((ToggleButton) v).setChecked(true);
                            heaterAutoState = 1;
                            break;
                        }
                        break;
                    }
                    else
                    {
                        Log.d("CONTROL","HITTE_AU 누름");
                        doCommuForState(APP_HITTE_AU);

                        if(isSuccess.equals("S")) {
                            Log.d("CONTROL", "S를 받음");
                            ((ToggleButton) v).setTextColor(getResources().getColor(R.color.gray_semi));
                            ((ToggleButton) v).setChecked(false);
                            heaterAutoState = 0;
                            break;
                        }
                        break;
                    }
                }
                else
                {
                    logMessege("계정이 등록되지 않았습니다");
                    break;
                }


            case R.id.hum_toggle:
                if(!pref.getString("id", "").equals(""))
                {
                    if (humState == 0)
                    {
                        Log.d("CONTROL","HUMID_ON 누름");
                        doCommuForState(APP_HUMID_ON);

                        if(isSuccess.equals("S")) {
                            Log.d("CONTROL", "S를 받음");
                            sound.play(soundIdHumOn, 1f, 1f, 0, 0, 1f);
                            ((ToggleButton) v).setTextColor(getResources().getColor(R.color.green));
                            ((ToggleButton) v).setChecked(true);
                            humState = 1;
                            break;
                        }
                        break;
                    }
                    else
                    {
                        Log.d("CONTROL","HUMID_OF 누름");
                        doCommuForState(APP_HUMID_OF);

                        if(isSuccess.equals("S")) {
                            Log.d("CONTROL", "S를 받음");
                            ((ToggleButton) v).setTextColor(getResources().getColor(R.color.gray_semi));
                            ((ToggleButton) v).setChecked(false);
                            humState = 0;
                            break;
                        }
                        break;
                    }
                }
                else
                {
                    logMessege("계정이 등록되지 않았습니다");
                    break;
                }
            case R.id.hum_auto_toggle:
                if(!pref.getString("id", "").equals(""))
                {
                    if (humAutoState == 0)
                    {
                        Log.d("CONTROL","HUMID_AU 누름");
                        doCommuForState(APP_HUMID_AU);

                        if(isSuccess.equals("S")) {
                            Log.d("CONTROL", "S를 받음");
                            sound.play(soundIdAutoOn, 1f, 1f, 0, 0, 1f);
                            ((ToggleButton) v).setTextColor(getResources().getColor(R.color.green));
                            ((ToggleButton) v).setChecked(true);
                            humAutoState = 1;
                            break;
                        }
                        break;
                    }
                    else
                    {
                        Log.d("CONTROL","HUMID_AU 누름");
                        doCommuForState(APP_HUMID_AU);

                        if(isSuccess.equals("S")) {
                            Log.d("CONTROL", "S를 받음");
                            ((ToggleButton) v).setTextColor(getResources().getColor(R.color.gray_semi));
                            ((ToggleButton) v).setChecked(false);
                            humAutoState = 0;
                            break;
                        }
                        break;
                    }
                }
                else
                {
                    logMessege("계정이 등록되지 않았습니다");
                    break;
                }


            case R.id.motor_side_switch:
                if(!pref.getString("id", "").equals(""))
                {
                    if (motorSideState == 0)
                    {
                        Log.d("CONTROL","MOTOR_RI 누름");
                        doCommuForState(APP_MOTOR_RI);

                        if(isSuccess.equals("S")) {
                            Log.d("CONTROL", "S를 받음");
                            motorSwitch.setChecked(true);
                            motorSideState = 1;
                            break;
                        }
                        break;
                    }
                    else
                    {
                        Log.d("CONTROL","MOTOR_LE 누름");
                        doCommuForState(APP_MOTOR_LE);

                        if(isSuccess.equals("S")) {
                            Log.d("CONTROL", "S를 받음");
                            motorSwitch.setChecked(false);
                            motorSideState = 0;
                            break;
                        }
                        break;
                    }
                }
                else
                {
                    logMessege("계정이 등록되지 않았습니다");
                    break;
                }
            case R.id.motor_toggle:
                if(!pref.getString("id", "").equals(""))
                {
                    if (motorState == 0)
                    {
                        Log.d("CONTROL","MOTOR_OF 누름");
                        doCommuForState(APP_MOTOR_OF);

                        if(isSuccess.equals("S")) {
                            Log.d("CONTROL", "S를 받음");
                            ((ToggleButton) v).setTextColor(getResources().getColor(R.color.green));
                            ((ToggleButton) v).setChecked(true);
                            motorSwitch.setThumbResource(R.drawable.switch_thumb_on);
                            motorSwitch.setTrackResource(R.drawable.switch_track_on);
                            motorState = 1;
                            break;
                        }
                        break;
                    }
                    else
                    {
                        Log.d("CONTROL","MOTOR_OF 누름");
                        doCommuForState(APP_MOTOR_OF);

                        if(isSuccess.equals("S")) {
                            Log.d("CONTROL", "S를 받음");
                            ((ToggleButton) v).setTextColor(getResources().getColor(R.color.gray_semi));
                            ((ToggleButton) v).setChecked(false);
                            motorSwitch.setThumbResource(R.drawable.switch_thumb_off);
                            motorSwitch.setTrackResource(R.drawable.switch_track_off);
                            motorState = 0;
                            break;
                        }
                        break;
                    }
                }
                else
                {
                    logMessege("계정이 등록되지 않았습니다");
                    break;
                }
        }
    }

    public void setValue(String valueSet)
    {
        int[] state = new int[8];

        StringTokenizer str = new StringTokenizer(valueSet, "/");
        Log.d("STRING : CON",valueSet);

        int countTokens = str.countTokens();
        for (int i = 0; i < countTokens; i++)
        {
            state[i] = Integer.parseInt(str.nextToken());
        }

        uvState = state[0];
        uvAutoState = state[1];
        heaterState = state[2];
        heaterAutoState = state[3];
        humState = state[4];
        humAutoState = state[5];
        motorSideState = state[6];
        motorState = state[7];
    }

    public void doCommuForState(String msg)
    {
        try {
            result_s = new CommuTask().execute(msg).get();

            if (result_s instanceof Exception)
            {
                logMessege(result_s.toString());
                isSuccess = "F";
            }
            else
            {
                isSuccess = result_s.toString();
            }
        } catch (Exception e)
        {
            logMessege(e.toString());
            isSuccess = "F";
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
                    Log.d("SOCKET", "서버 연결 요청 _ ControlActivity 소켓 생성");
                    isConnect = true;
                }

                if(mmInStream == null) {
                    mmInStream = socket.getInputStream();
                    mmOutStream = socket.getOutputStream();
                    Log.d("SOCKET","stream open _ ControlActivity");
                    if(mmInStream == null){
                        Log.d("SOCKET","서버 연결 안됨 _ ControlActivity");
                    }else{
                        Log.d("SOCKET","서버 연결 성공 _ ControlActivity");
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
                mmOutStream.close();
                socket.close();

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

    public void logMessege(String log) {
        logMsg = Toast.makeText(this, log, Toast.LENGTH_LONG);
        logMsg.show();
    }
}
