package kr.soen.wificlienttest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import android.content.DialogInterface;

public class LoadingActivity extends AppCompatActivity {
    SharedPreferences pref;

    public WifiManager wifiManager;

    Toast logMsg;
    AlertDialog.Builder dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        pref = getSharedPreferences("pref", MODE_PRIVATE);

        if (wifiManager.isWifiEnabled())
        {
            startLoading();//로딩화면 2초 동안 출력
        }
        else
        {
            dialog = new AlertDialog.Builder(this);
            dialog.setTitle("WiFi가 활성화되지 않았습니다.");
            dialog.setMessage("WiFi를 활성화 하시겠습니까?");

            // OK 버튼 이벤트
            dialog.setPositiveButton("예", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // 사용자에게 wifi 활성화 요청
                    wifiManager.setWifiEnabled(true);

                    startLoading();//로딩화면 2초 동안 출력
                }
            });

            // Cancel 버튼 이벤트
            dialog.setNegativeButton("아니요",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    logMessege("WiFi 활성화가 취소되었습니다.");
                    dialog.cancel();
                    finish();
                }
            });
            dialog.show();
        }
    }

    private void startLoading() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                boolean isfirst = pref.getBoolean("isfirst", true);
                if(isfirst)
                {
                    Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                    startActivity(intent);//main activity 실행
                    finish();
                }
                else
                {
                    boolean islock = pref.getBoolean("islock", false);
                    if (islock)
                    {
                        Intent intent = new Intent(LoadingActivity.this, LoginActivity.class);
                        startActivity(intent);//login activity 실행
                        finish();
                    }
                    else
                    {
                        Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                        startActivity(intent);//main activity 실행
                        finish();
                    }
                }

            }
        }, 2000);
    }

    public void logMessege(String log) {
        logMsg = Toast.makeText(this, log, Toast.LENGTH_SHORT);
        logMsg.show();
    }
}
