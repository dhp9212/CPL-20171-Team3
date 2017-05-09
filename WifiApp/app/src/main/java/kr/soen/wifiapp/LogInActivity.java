package kr.soen.wifiapp;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.jungly.gridpasswordview.GridPasswordView;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener{
    GridPasswordView pswview;
    Toast logMsg;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        pref = getSharedPreferences("pref", MODE_PRIVATE);
        pswview = (GridPasswordView)findViewById(R.id.pswView);
        Button okBtn = (Button)findViewById(R.id.ok_button);
        okBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.ok_button:
                if (pref.getString("password", "").equals(pswview.getPassWord()))
                {
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(intent);//main activity 실행
                    finish();
                    break;
                }

                logMessege("비밀번호를 잘못 입력하셨습니다.");
                break;
        }
    }

    public void logMessege(String log) {
        logMsg = Toast.makeText(this, log, Toast.LENGTH_SHORT);
        logMsg.show();
    }
}