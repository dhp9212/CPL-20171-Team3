package kr.soen.wificlienttest;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


public class ControlFragment extends Fragment implements View.OnClickListener{
    private static final String IS_WIFI_ON = "is_wifi_on?";

    Button controlBtn;

    Toast logMsg;

    boolean iswifion;

    public static final String REQUEST_CONTROL = "2";


    public ControlFragment() {
    }

    public static ControlFragment newInstance(boolean wifistate) {
        ControlFragment fragment = new ControlFragment();
        Bundle args = new Bundle();
        args.putBoolean(IS_WIFI_ON, wifistate);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_control, container, false);

        //제어 button 설정
        controlBtn = (Button)rootView.findViewById(R.id.control_btn);
        controlBtn.setOnClickListener(this);

        iswifion = getArguments().getBoolean(IS_WIFI_ON);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.control_btn:
                if (iswifion)
                {
                    //((MainActivity)getActivity()).doCommu(REQUEST_CONTROL);
                    break;
                }

                logMessege("WiFi가 활성화되지 않았습니다.");
                break;
        }
    }

    public void logMessege(String log) {
        logMsg = Toast.makeText(getActivity(), log, Toast.LENGTH_SHORT);
        logMsg.show();
    }

    public void wifiState(boolean wifistate)
    {
        iswifion = wifistate;
    }
}