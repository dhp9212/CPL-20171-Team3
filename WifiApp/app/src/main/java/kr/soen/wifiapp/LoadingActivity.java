package kr.soen.wifiapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class LoadingActivity extends AppCompatActivity {
    SharedPreferences pref;

    Toast logMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        pref = getSharedPreferences("pref", MODE_PRIVATE);

        startLoading();
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
                        Intent intent = new Intent(LoadingActivity.this, LogInActivity.class);
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
