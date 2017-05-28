package kr.soen.wifiapp;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;
import java.util.StringTokenizer;

public class ProfileFragment extends Fragment implements View.OnClickListener{
    public static final String REQUEST_STATE = "3";
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

    int[] state = new int[8];

    Switch lightSwitch, heaterSwitch, humSwitch, motorSwitch;
    ToggleButton lightToggle, heaterToggle, humToggle, motorToggle;

    int lightState, heaterState, humState, motorSideState;
    int lightAutoState, heaterAutoState, humAutoState, motorState;

    public ProfileFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.d(this.getClass().getSimpleName(), "onCreateView()");
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.fragment_profile, container, false);

        TextView titleText = (TextView)layout.findViewById(R.id.title_text) ;
        titleText.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "NanumSquareEB.ttf"));

        ImageButton backBtn = (ImageButton)layout.findViewById(R.id.btn_back);
        backBtn.setOnClickListener(this);

        ///////////////

        TextView accountText = (TextView)layout.findViewById(R.id.account) ;
        accountText.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "NanumSquareL.ttf"));

        ///////////////

        TextView idText = (TextView)layout.findViewById(R.id.id_text) ;
        idText.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "NanumSquareB.ttf"));
        idText.setText(((MainActivity)getActivity()).id);

        ///////////////

        TextView controlText = (TextView)layout.findViewById(R.id.control) ;
        controlText.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "NanumSquareL.ttf"));

        TextView onText = (TextView)layout.findViewById(R.id.text_on);
        onText.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "NanumSquareR.ttf"));

        TextView offText = (TextView)layout.findViewById(R.id.text_off);
        offText.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "NanumSquareR.ttf"));

        TextView lightText = (TextView)layout.findViewById(R.id.light_text);
        lightText.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "NanumSquareB.ttf"));

        lightSwitch = (Switch)layout.findViewById(R.id.light_switch);
        lightSwitch.setOnClickListener(this);
        lightSwitch.setChecked(false);

        lightToggle = (ToggleButton)layout.findViewById(R.id.light_toggle);
        lightToggle.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "NanumSquareR.ttf"));
        lightToggle.setOnClickListener(this);
        lightToggle.setChecked(false);

        TextView heaterText = (TextView)layout.findViewById(R.id.heater_text);
        heaterText.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "NanumSquareB.ttf"));

        heaterSwitch = (Switch)layout.findViewById(R.id.heater_switch);
        heaterSwitch.setOnClickListener(this);
        heaterSwitch.setChecked(false);

        heaterToggle = (ToggleButton)layout.findViewById(R.id.heater_toggle);
        heaterToggle.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "NanumSquareR.ttf"));
        heaterToggle.setOnClickListener(this);
        heaterToggle.setChecked(false);

        TextView humText = (TextView)layout.findViewById(R.id.hum_text);
        humText.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "NanumSquareB.ttf"));

        humSwitch = (Switch)layout.findViewById(R.id.hum_switch);
        humSwitch.setOnClickListener(this);
        humSwitch.setChecked(false);

        humToggle = (ToggleButton)layout.findViewById(R.id.hum_toggle);
        humToggle.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "NanumSquareR.ttf"));
        humToggle.setOnClickListener(this);
        humToggle.setChecked(false);

        TextView motorText = (TextView)layout.findViewById(R.id.motor_text);
        motorText.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "NanumSquareB.ttf"));

        motorSwitch = (Switch)layout.findViewById(R.id.motor_switch);
        motorSwitch.setOnClickListener(this);
        motorSwitch.setChecked(false);

        motorToggle = (ToggleButton)layout.findViewById(R.id.motor_toggle);
        motorToggle.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "NanumSquareR.ttf"));
        motorToggle.setOnClickListener(this);
        motorToggle.setChecked(false);

        return layout;
    }

    @Override
    public void onResume() {
        Log.d(this.getClass().getSimpleName(), "onResume()");
        super.onResume();

        ((MainActivity)getActivity()).doCommu(REQUEST_STATE);

        setValue(((MainActivity)getActivity()).stateData);
        setState();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btn_back:
                ((MainActivity)getActivity()).vp.setCurrentItem(1);
                break;
            case R.id.light_switch:
                if (lightAutoState == 0)
                {
                    if(lightState == 0)
                    {
                        ((MainActivity)getActivity()).doCommuForState(APP_LIGHT_ON);

                        if (((MainActivity)getActivity()).isSuccess.equals("S"))
                        {
                            ((MainActivity)getActivity()).soundPlay(1);
                            lightSwitch.setChecked(true);
                            lightSwitch.setTrackResource(R.drawable.switch_track_on);
                            lightSwitch.setThumbResource(R.drawable.switch_thumb_on);
                            lightState = 1;
                        }
                        break;
                    }
                    else
                    {
                        ((MainActivity)getActivity()).doCommuForState(APP_LIGHT_OF);

                        if (((MainActivity)getActivity()).isSuccess.equals("S"))
                        {
                            lightSwitch.setChecked(false);
                            lightSwitch.setTrackResource(R.drawable.switch_track_off);
                            lightSwitch.setThumbResource(R.drawable.switch_thumb_off);
                            lightState = 0;
                        }
                        break;
                    }
                }
                else
                {
                    lightSwitch.setEnabled(false);
                    lightSwitch.setTrackResource(R.drawable.switch_track_off);
                    lightSwitch.setThumbResource(R.drawable.switch_thumb_off);
                    break;
                }
            case R.id.light_toggle:
                if (lightAutoState == 0)
                {
                    ((MainActivity)getActivity()).doCommuForState(APP_LIGHT_AU);

                    if (((MainActivity)getActivity()).isSuccess.equals("S"))
                    {
                        ((MainActivity)getActivity()).soundPlay(4);
                        lightSwitch.setEnabled(false);
                        lightSwitch.setChecked(false);
                        lightSwitch.setTrackResource(R.drawable.switch_track_off);
                        lightSwitch.setThumbResource(R.drawable.switch_thumb_off);

                        lightToggle.setChecked(true);
                        lightToggle.setTextColor(getResources().getColor(R.color.green));
                        lightToggle.setBackgroundResource(R.drawable.toggle_on);
                        lightAutoState = 1;
                    }
                    break;
                }
                else
                {
                    ((MainActivity)getActivity()).doCommuForState(APP_LIGHT_AU);

                    if (((MainActivity)getActivity()).isSuccess.equals("S"))
                    {
                        lightSwitch.setEnabled(true);
                        if (lightState == 1)
                        {
                            lightSwitch.setChecked(true);
                            lightSwitch.setTrackResource(R.drawable.switch_track_on);
                            lightSwitch.setThumbResource(R.drawable.switch_thumb_on);
                        }

                        lightToggle.setChecked(false);
                        lightToggle.setTextColor(getResources().getColor(R.color.gray_semi));
                        lightToggle.setBackgroundResource(R.drawable.toggle_off);
                        lightAutoState = 0;
                    }
                    break;
                }

            case R.id.heater_switch:
                if (heaterAutoState == 0)
                {
                    if(heaterState == 0)
                    {
                        ((MainActivity)getActivity()).doCommuForState(APP_HITTE_ON);

                        if (((MainActivity)getActivity()).isSuccess.equals("S"))
                        {
                            ((MainActivity)getActivity()).soundPlay(2);
                            heaterSwitch.setChecked(true);
                            heaterSwitch.setTrackResource(R.drawable.switch_track_on);
                            heaterSwitch.setThumbResource(R.drawable.switch_thumb_on);
                            heaterState = 1;
                        }
                        break;
                    }
                    else
                    {
                        ((MainActivity)getActivity()).doCommuForState(APP_HITTE_OF);

                        if (((MainActivity)getActivity()).isSuccess.equals("S"))
                        {
                            heaterSwitch.setChecked(false);
                            heaterSwitch.setTrackResource(R.drawable.switch_track_off);
                            heaterSwitch.setThumbResource(R.drawable.switch_thumb_off);
                            heaterState = 0;
                        }
                        break;
                    }
                }
                else
                {
                    heaterSwitch.setEnabled(false);
                    heaterSwitch.setTrackResource(R.drawable.switch_track_off);
                    heaterSwitch.setThumbResource(R.drawable.switch_thumb_off);
                    break;
                }
            case R.id.heater_toggle:
                if (heaterAutoState == 0)
                {
                    ((MainActivity)getActivity()).doCommuForState(APP_HITTE_AU);

                    if (((MainActivity)getActivity()).isSuccess.equals("S"))
                    {
                        ((MainActivity)getActivity()).soundPlay(4);
                        heaterSwitch.setEnabled(false);
                        heaterSwitch.setChecked(false);
                        heaterSwitch.setTrackResource(R.drawable.switch_track_off);
                        heaterSwitch.setThumbResource(R.drawable.switch_thumb_off);

                        heaterToggle.setChecked(true);
                        heaterToggle.setTextColor(getResources().getColor(R.color.green));
                        heaterToggle.setBackgroundResource(R.drawable.toggle_on);
                        heaterAutoState = 1;
                    }
                    break;
                }
                else
                {
                    ((MainActivity)getActivity()).doCommuForState(APP_HITTE_AU);

                    if (((MainActivity)getActivity()).isSuccess.equals("S"))
                    {
                        heaterSwitch.setEnabled(true);
                        if (heaterState == 1)
                        {
                            heaterSwitch.setChecked(true);
                            heaterSwitch.setTrackResource(R.drawable.switch_track_on);
                            heaterSwitch.setThumbResource(R.drawable.switch_thumb_on);
                        }

                        heaterToggle.setChecked(false);
                        heaterToggle.setTextColor(getResources().getColor(R.color.gray_semi));
                        heaterToggle.setBackgroundResource(R.drawable.toggle_off);
                        heaterAutoState = 0;
                    }
                    break;
                }

            case R.id.hum_switch:
                if (humAutoState == 0)
                {
                    if(humState == 0)
                    {
                        ((MainActivity)getActivity()).doCommuForState(APP_HUMID_ON);

                        if (((MainActivity)getActivity()).isSuccess.equals("S"))
                        {
                            ((MainActivity)getActivity()).soundPlay(3);
                            humSwitch.setChecked(true);
                            humSwitch.setTrackResource(R.drawable.switch_track_on);
                            humSwitch.setThumbResource(R.drawable.switch_thumb_on);
                            humState = 1;
                        }
                        break;
                    }
                    else
                    {
                        ((MainActivity)getActivity()).doCommuForState(APP_HUMID_OF);

                        if (((MainActivity)getActivity()).isSuccess.equals("S"))
                        {
                            humSwitch.setChecked(false);
                            humSwitch.setTrackResource(R.drawable.switch_track_off);
                            humSwitch.setThumbResource(R.drawable.switch_thumb_off);
                            humState = 0;
                        }
                        break;
                    }
                }
                else
                {
                    humSwitch.setEnabled(false);
                    humSwitch.setTrackResource(R.drawable.switch_track_off);
                    humSwitch.setThumbResource(R.drawable.switch_thumb_off);
                    break;
                }
            case R.id.hum_toggle:
                if (humAutoState == 0)
                {
                    ((MainActivity)getActivity()).doCommuForState(APP_HUMID_AU);

                    if (((MainActivity)getActivity()).isSuccess.equals("S"))
                    {
                        ((MainActivity)getActivity()).soundPlay(4);
                        humSwitch.setEnabled(false);
                        humSwitch.setChecked(false);
                        humSwitch.setTrackResource(R.drawable.switch_track_off);
                        humSwitch.setThumbResource(R.drawable.switch_thumb_off);

                        humToggle.setChecked(true);
                        humToggle.setTextColor(getResources().getColor(R.color.green));
                        humToggle.setBackgroundResource(R.drawable.toggle_on);
                        humAutoState = 1;
                    }
                    break;
                }
                else
                {
                    ((MainActivity)getActivity()).doCommuForState(APP_HUMID_AU);

                    if (((MainActivity)getActivity()).isSuccess.equals("S"))
                    {
                        humSwitch.setEnabled(true);
                        if (humState == 1)
                        {
                            humSwitch.setChecked(true);
                            humSwitch.setTrackResource(R.drawable.switch_track_on);
                            humSwitch.setThumbResource(R.drawable.switch_thumb_on);
                        }

                        humToggle.setChecked(false);
                        humToggle.setTextColor(getResources().getColor(R.color.gray_semi));
                        humToggle.setBackgroundResource(R.drawable.toggle_off);
                        humAutoState = 0;
                    }
                    break;
                }

            case R.id.motor_switch:
                if (motorState == 1)
                {
                    if(motorSideState == 0)
                    {
                        ((MainActivity)getActivity()).doCommuForState(APP_MOTOR_RI);

                        if (((MainActivity)getActivity()).isSuccess.equals("S"))
                        {
                            motorSwitch.setChecked(true);
                            motorSwitch.setTrackResource(R.drawable.switch_track_on);
                            motorSwitch.setThumbResource(R.drawable.switch_thumb_on);
                            motorSideState = 1;
                        }
                        break;
                    }
                    else
                    {
                        ((MainActivity)getActivity()).doCommuForState(APP_MOTOR_LE);

                        if (((MainActivity)getActivity()).isSuccess.equals("S"))
                        {
                            motorSwitch.setChecked(false);
                            motorSwitch.setTrackResource(R.drawable.switch_track_off);
                            motorSwitch.setThumbResource(R.drawable.switch_thumb_off);
                            motorSideState = 0;
                        }
                        break;
                    }
                }
                else
                {
                    motorSwitch.setEnabled(false);
                    motorSwitch.setTrackResource(R.drawable.switch_track_off);
                    motorSwitch.setThumbResource(R.drawable.switch_thumb_off);
                    break;
                }
            case R.id.motor_toggle:
                if (motorState == 0)
                {
                    ((MainActivity)getActivity()).doCommuForState(APP_MOTOR_OF);

                    if (((MainActivity)getActivity()).isSuccess.equals("S"))
                    {
                        motorSwitch.setEnabled(true);
                        motorSwitch.setThumbResource(R.drawable.switch_thumb_on);
                        motorSwitch.setTrackResource(R.drawable.switch_track_on);
                        if (motorSideState == 1)
                        {
                            motorSwitch.setChecked(true);
                        }

                        motorToggle.setChecked(true);
                        motorToggle.setTextColor(getResources().getColor(R.color.green));
                        motorToggle.setBackgroundResource(R.drawable.toggle_on);
                        motorState = 1;
                    }
                    break;
                }
                else
                {
                    ((MainActivity)getActivity()).doCommuForState(APP_MOTOR_OF);

                    if (((MainActivity)getActivity()).isSuccess.equals("S"))
                    {
                        motorSwitch.setEnabled(false);
                        motorSwitch.setThumbResource(R.drawable.switch_thumb_off);
                        motorSwitch.setTrackResource(R.drawable.switch_track_off);
                        motorSwitch.setChecked(false);

                        motorToggle.setChecked(false);
                        motorToggle.setTextColor(getResources().getColor(R.color.gray_semi));
                        motorToggle.setBackgroundResource(R.drawable.toggle_off);
                        motorState = 0;
                    }
                    break;
                }
        }
    }

    public void setValue(String valueSet)
    {
        StringTokenizer str = new StringTokenizer(valueSet, "/");

        int countTokens = str.countTokens();

        for (int i = 0; i < countTokens; i++)
        {
            state[i] = Integer.parseInt(str.nextToken());
        }

        lightState = state[0];
        lightAutoState = state[1];
        heaterState = state[2];
        heaterAutoState = state[3];
        humState = state[4];
        humAutoState = state[5];
        motorSideState = state[6];
        motorState = state[7];
    }

    public void setState()
    {
        if (lightAutoState == 0)
        {
            if (lightState == 1)
            {
                lightSwitch.setChecked(true);
                lightSwitch.setTrackResource(R.drawable.switch_track_on);
                lightSwitch.setThumbResource(R.drawable.switch_thumb_on);
            }
            else
            {
                lightSwitch.setChecked(false);
                lightSwitch.setTrackResource(R.drawable.switch_track_off);
                lightSwitch.setThumbResource(R.drawable.switch_thumb_off);
            }

            lightToggle.setChecked(false);
            lightToggle.setTextColor(getResources().getColor(R.color.gray_semi));
            lightToggle.setBackgroundResource(R.drawable.toggle_off);
        }
        else if(lightAutoState == 1)
        {
            lightSwitch.setEnabled(false);
            lightSwitch.setChecked(false);
            lightSwitch.setTrackResource(R.drawable.switch_track_off);
            lightSwitch.setThumbResource(R.drawable.switch_thumb_off);

            lightToggle.setChecked(true);
            lightToggle.setTextColor(getResources().getColor(R.color.green));
            lightToggle.setBackgroundResource(R.drawable.toggle_on);
        }

        if (heaterAutoState == 0)
        {
            if (heaterState == 1)
            {
                heaterSwitch.setChecked(true);
                heaterSwitch.setTrackResource(R.drawable.switch_track_on);
                heaterSwitch.setThumbResource(R.drawable.switch_thumb_on);
            }
            else
            {
                heaterSwitch.setChecked(false);
                heaterSwitch.setTrackResource(R.drawable.switch_track_off);
                heaterSwitch.setThumbResource(R.drawable.switch_thumb_off);
            }

            heaterToggle.setChecked(false);
            heaterToggle.setTextColor(getResources().getColor(R.color.gray_semi));
            heaterToggle.setBackgroundResource(R.drawable.toggle_off);
        }
        else if (heaterAutoState == 1)
        {
            heaterSwitch.setEnabled(false);
            heaterSwitch.setChecked(false);
            heaterSwitch.setTrackResource(R.drawable.switch_track_off);
            heaterSwitch.setThumbResource(R.drawable.switch_thumb_off);

            heaterToggle.setChecked(true);
            heaterToggle.setTextColor(getResources().getColor(R.color.green));
            heaterToggle.setBackgroundResource(R.drawable.toggle_on);
        }

        if (humAutoState == 0)
        {
            if (humState == 1)
            {
                humSwitch.setChecked(true);
                humSwitch.setTrackResource(R.drawable.switch_track_on);
                humSwitch.setThumbResource(R.drawable.switch_thumb_on);
            }
            else
            {
                humSwitch.setChecked(false);
                humSwitch.setTrackResource(R.drawable.switch_track_off);
                humSwitch.setThumbResource(R.drawable.switch_thumb_off);
            }

            humToggle.setChecked(false);
            humToggle.setTextColor(getResources().getColor(R.color.gray_semi));
            humToggle.setBackgroundResource(R.drawable.toggle_off);
        }
        else if (humAutoState == 1)
        {
            humSwitch.setEnabled(false);
            humSwitch.setChecked(false);
            humSwitch.setTrackResource(R.drawable.switch_track_off);
            humSwitch.setThumbResource(R.drawable.switch_thumb_off);

            humToggle.setChecked(true);
            humToggle.setTextColor(getResources().getColor(R.color.green));
            humToggle.setBackgroundResource(R.drawable.toggle_on);
        }

        if (motorState == 0)
        {
            motorSwitch.setEnabled(false);
            motorSwitch.setChecked(false);
            motorSwitch.setTrackResource(R.drawable.switch_track_off);
            motorSwitch.setThumbResource(R.drawable.switch_thumb_off);

            motorToggle.setChecked(false);
            motorToggle.setTextColor(getResources().getColor(R.color.gray_semi));
            motorToggle.setBackgroundResource(R.drawable.toggle_off);
        }
        else if (motorState == 1)
        {
            if (motorSideState == 1)
            {
                motorSwitch.setChecked(true);
                motorSwitch.setThumbResource(R.drawable.switch_thumb_on);
                motorSwitch.setTrackResource(R.drawable.switch_track_on);
            }
            else
            {
                motorSwitch.setChecked(true);
                motorSwitch.setThumbResource(R.drawable.switch_thumb_off);
                motorSwitch.setTrackResource(R.drawable.switch_track_off);
            }

            motorToggle.setChecked(true);
            motorToggle.setTextColor(getResources().getColor(R.color.green));
            motorToggle.setBackgroundResource(R.drawable.toggle_on);
        }
    }
}
