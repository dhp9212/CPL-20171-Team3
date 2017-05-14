package kr.soen.wifiapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.StringTokenizer;

public class ControlFragment extends Fragment implements View.OnClickListener{
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

    SharedPreferences pref;
    WifiManager wifiManager;
    Toast logMsg;

    ImageButton lightBtn, hitterBtn, humBtn, motorBtn;
    ImageButton lightBtnAuto, hitterBtnAuto, humBtnAuto, motorBtnPower;

    int lightState, hitterState, humState, motorState;
    int lightAutoState, hitterAutoState, humAutoState, motorPowerState;

    int[] Data = new int[8];

    String result;

    public ControlFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null) {
            Data = savedInstanceState.getIntArray("savedData");

            lightState = Data[0];
            lightAutoState = Data[1];
            hitterState = Data[2];
            hitterAutoState = Data[3];
            humState = Data[4];
            humAutoState = Data[5];
            motorState = Data[6];
            motorPowerState = Data[7];
        }
        else
        {
            setValue(((MainActivity)getActivity()).stateData);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        int[] savedData = new int[8];
        savedData[0] = lightState;
        savedData[1] = lightAutoState;
        savedData[2] = hitterState;
        savedData[3] = hitterAutoState;
        savedData[4] = humState;
        savedData[5] = humAutoState;
        savedData[6] = motorState;
        savedData[7] = motorPowerState;

        savedInstanceState.putIntArray("savedData", savedData);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.fragment_control, container, false);

        wifiManager = (WifiManager)getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);

        lightBtn = (ImageButton)layout.findViewById(R.id.light_btn);
        lightBtn.setOnClickListener(this);
        if(lightState == 1)
        {
            //source need to add
            //lightBtn.setImageDrawable(R.drawable.);
        }
        lightBtnAuto = (ImageButton)layout.findViewById(R.id.light_btn_auto);
        lightBtnAuto.setOnClickListener(this);
        if(lightAutoState == 1)
        {
            //source need to add
            //lightBtnAuto.setImageDrawable(R.drawable);
        }


        hitterBtn = (ImageButton)layout.findViewById(R.id.hitter_btn);
        hitterBtn.setOnClickListener(this);
        if(hitterState == 1)
        {
            hitterBtn.setImageResource(R.drawable.heat_button_state);
        }
        hitterBtnAuto = (ImageButton)layout.findViewById(R.id.hitter_btn_auto);
        hitterBtnAuto.setOnClickListener(this);
        if(hitterAutoState == 1)
        {
            //source to add
            //hitterAutoState.setImageResource(R.drawable.)
        }

        humBtn = (ImageButton)layout.findViewById(R.id.hum_btn);
        humBtn.setOnClickListener(this);
        if(humState == 1)
        {
            humBtn.setImageResource(R.drawable.hum_button_state);
        }
        humBtnAuto = (ImageButton)layout.findViewById(R.id.hum_btn_auto);
        humBtnAuto.setOnClickListener(this);
        if(humAutoState == 1)
        {
            //source to add
            //humAutoState.setImageResource(R.drawable)
        }

        motorBtn = (ImageButton)layout.findViewById(R.id.motor_btn);
        motorBtn.setOnClickListener(this);
        if(motorState == 1)
        {
            //source to add
        }
        motorBtnPower = (ImageButton)layout.findViewById(R.id.motor_btn_power);
        motorBtnPower.setOnClickListener(this);
        if(motorPowerState == 1)
        {
            //source to add
        }

        return layout;
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

        state[0] = lightState;
        state[1] = lightAutoState;
        state[2] = hitterState;
        state[3] = hitterAutoState;
        state[4] = humState;
        state[5] = humAutoState;
        state[6] = motorState;
        state[7] = motorPowerState;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.light_btn:
                if (wifiManager.isWifiEnabled())
                {
                    if(!pref.getString("id", "").equals(""))
                    {
                        if(lightState == 0)
                        {
                            ((MainActivity)getActivity()).doCommuForState(APP_LIGHT_ON);
                            result = ((MainActivity)getActivity()).issuccess;

                            if (result.equals("S"))
                            {
                                //light on
                                //lightBtn.setImageResource(R.drawable)
                                lightState = 1;
                            }
                        }
                        else
                        {
                            ((MainActivity)getActivity()).doCommuForState(APP_LIGHT_OF);
                            result = ((MainActivity)getActivity()).issuccess;

                            if (result.equals("S"))
                            {
                                //off
                                //lightBtn.setImageResource(R.drawable)
                                lightState = 0;
                            }
                        }
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

            case R.id.light_btn_auto:
                if (wifiManager.isWifiEnabled())
                {
                    if(!pref.getString("id", "").equals(""))
                    {
                        if(lightAutoState == 0)
                        {
                            ((MainActivity)getActivity()).doCommuForState(APP_LIGHT_AU);
                            result = ((MainActivity)getActivity()).issuccess;

                            if (result.equals("S"))
                            {
                                //source to add auto
                                //lightBtnAuto.setTextColor(getResources().getColor(R.color.colorAccent));
                                lightAutoState = 1;
                            }
                        }
                        else
                        {
                            ((MainActivity)getActivity()).doCommuForState(APP_LIGHT_AU);
                            result = ((MainActivity)getActivity()).issuccess;

                            if (result.equals("S"))
                            {
                                //source to add
                                //lightBtnAuto.setTextColor(getResources().getColor(R.color.gray));
                                lightAutoState = 0;
                            }
                        }
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

            case R.id.hitter_btn:
                if (wifiManager.isWifiEnabled())
                {
                    if(!pref.getString("id", "").equals(""))
                    {
                        if(hitterState == 0)
                        {
                            ((MainActivity)getActivity()).doCommuForState(APP_HITTE_ON);
                            result = ((MainActivity)getActivity()).issuccess;
                            Log.d("CONTROL","HIT_ON 누름");
                            if (result.equals("S"))
                            {
                                Log.d("CONTROL","S를 받음");
                                hitterBtn.setImageResource(R.drawable.heat_button_state);
                                hitterState = 1;
                            }
                        }
                        else
                        {
                            ((MainActivity)getActivity()).doCommuForState(APP_HITTE_OF);
                            result = ((MainActivity)getActivity()).issuccess;

                            if (result.equals("S"))
                            {
                                hitterBtn.setImageResource(R.drawable.heat_off_button_state);
                                hitterState = 0;
                            }
                        }
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

            case R.id.hitter_btn_auto:
                if (wifiManager.isWifiEnabled())
                {
                    if(!pref.getString("id", "").equals(""))
                    {
                        if(hitterAutoState == 0)
                        {
                            ((MainActivity)getActivity()).doCommuForState(APP_HITTE_AU);
                            result = ((MainActivity)getActivity()).issuccess;

                            if (result.equals("S"))
                            {
                                //auto - on
                                //hitterBtnAuto.setTextColor(getResources().getColor(R.color.colorAccent));
                                hitterAutoState = 1;
                            }
                        }
                        else
                        {
                            ((MainActivity)getActivity()).doCommuForState(APP_HITTE_AU);
                            result = ((MainActivity)getActivity()).issuccess;

                            if (result.equals("S"))
                            {
                                //auto - off
                                //hitterBtnAuto.setTextColor(getResources().getColor(R.color.gray));
                                hitterAutoState = 0;
                            }
                        }
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

            case R.id.hum_btn:
                if (wifiManager.isWifiEnabled())
                {
                    if(!pref.getString("id", "").equals(""))
                    {
                        if(humState == 0)
                        {
                            ((MainActivity)getActivity()).doCommuForState(APP_HUMID_ON);
                            result = ((MainActivity)getActivity()).issuccess;

                            if (result.equals("S"))
                            {
                                humBtn.setImageResource(R.drawable.hum_button_state);
                                humState = 1;
                            }
                        }
                        else
                        {
                            ((MainActivity)getActivity()).doCommuForState(APP_HUMID_OF);
                            result = ((MainActivity)getActivity()).issuccess;

                            if (result.equals("S"))
                            {
                                humBtn.setImageResource(R.drawable.hum_off_button_state);
                                humState = 0;
                            }
                        }
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

            case R.id.hum_btn_auto:
                if (wifiManager.isWifiEnabled())
                {
                    if(!pref.getString("id", "").equals(""))
                    {
                        if(humAutoState == 0)
                        {
                            ((MainActivity)getActivity()).doCommuForState(APP_HUMID_AU);
                            result = ((MainActivity)getActivity()).issuccess;

                            if (result.equals("S"))
                            {
                                //auto - on
                                //humBtnAuto.setTextColor(getResources().getColor(R.color.colorAccent));
                                humAutoState = 1;
                            }
                        }
                        else
                        {
                            ((MainActivity)getActivity()).doCommuForState(APP_HUMID_AU);
                            result = ((MainActivity)getActivity()).issuccess;

                            if (result.equals("S"))
                            {
                                //auto off
                                //humBtnAuto.setTextColor(getResources().getColor(R.color.gray));
                                humAutoState = 0;
                            }
                        }
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

            case R.id.motor_btn:
                if (wifiManager.isWifiEnabled())
                {
                    if(!pref.getString("id", "").equals(""))
                    {
                        if(motorState == 0)
                        {
                            ((MainActivity)getActivity()).doCommuForState(APP_MOTOR_LE);
                            result = ((MainActivity)getActivity()).issuccess;

                            if (result.equals("S"))
                            {
                                //motor resource to add
                                //motorBtn.setText("LEFT");
                                motorState = 1;
                            }
                        }
                        else
                        {
                            ((MainActivity)getActivity()).doCommuForState(APP_MOTOR_RI);
                            result = ((MainActivity)getActivity()).issuccess;

                            if (result.equals("S"))
                            {
                                //source need to add
                                //motorBtn.setText("RIGHT");
                                motorState = 0;
                            }
                        }
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
            case R.id.motor_btn_power:
                if (wifiManager.isWifiEnabled())
                {
                    if(!pref.getString("id", "").equals(""))
                    {
                        if(motorPowerState == 0)
                        {
                            ((MainActivity)getActivity()).doCommuForState(APP_MOTOR_OF);
                            result = ((MainActivity)getActivity()).issuccess;

                            if (result.equals("S"))
                            {
                                //motor source
                                //motorBtnPower.setText("ON");
                                //motorBtnPower.setTextColor(getResources().getColor(R.color.colorAccent));
                                motorPowerState = 1;
                            }
                        }
                        else
                        {
                            ((MainActivity)getActivity()).doCommuForState(APP_MOTOR_OF);
                            result = ((MainActivity)getActivity()).issuccess;

                            if (result.equals("S"))
                            {
                                //motor source to addd
                                //motorBtnPower.setText("OFF");
                               // motorBtnPower.setTextColor(getResources().getColor(R.color.gray));
                                motorPowerState = 0;
                            }
                        }
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

    public void logMessege(String log) {
        logMsg = Toast.makeText(getActivity(), log, Toast.LENGTH_SHORT);
        logMsg.show();
    }
}
