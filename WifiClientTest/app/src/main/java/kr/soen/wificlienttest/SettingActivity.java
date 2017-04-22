package kr.soen.wificlienttest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener{
    public static final int REQUEST_ID_AND_PASSWORD = 1;
    public static final int RESULT_ID_AND_PASSWORD = 2;

    SharedPreferences pref;
    WifiManager wifiManager;

    Button idBtn;
    Button lockBtn;
    TextView idText;
    TextView lockText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        pref = getSharedPreferences("pref", MODE_PRIVATE);

        idBtn = (Button)findViewById(R.id.id_btn);
        idBtn.setOnClickListener(this);
        idText = (TextView)findViewById(R.id.id_text) ;

        lockBtn = (Button)findViewById(R.id.lock_btn);
        lockBtn.setOnClickListener(this);
        lockText = (TextView)findViewById(R.id.lock_text) ;

        String userid = pref.getString("id", "");
        if(!userid.equals(""))
        {
            idText.setText(userid);
            if (pref.getBoolean("islock", false))
            {
                String on = "ON";
                lockText.setText(on);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.id_btn:
                if (pref.getString("id", "").equals(""))
                {
                    Intent intent = new Intent(SettingActivity.this, SignupActivity.class);
                    startActivityForResult(intent, REQUEST_ID_AND_PASSWORD);
                    break;
                }

                break;
            case R.id.lock_btn:
                if (!pref.getString("id", "").equals(""))
                {
                    if (pref.getBoolean("islock", false))
                    {
                        String off = "OFF";
                        lockText.setText(off);

                        SharedPreferences.Editor editor = pref.edit();
                        editor.putBoolean("islock", false);
                        editor.apply();
                        break;
                    }
                    else
                    {
                        String on = "ON";
                        lockText.setText(on);

                        SharedPreferences.Editor editor = pref.edit();
                        editor.putBoolean("islock", true);
                        editor.apply();
                        break;
                    }
                }

                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_ID_AND_PASSWORD)
        {
            if(resultCode == RESULT_ID_AND_PASSWORD)
            {
                final String id = data.getStringExtra("id");
                String password = data.getStringExtra("password");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        idText.setText(id);
                    }
                });

                SharedPreferences.Editor editor = pref.edit();
                editor.putString("id", id);
                editor.putString("password", password);
                editor.apply();
            }
        }
    }
}
