package kr.soen.wifiapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        TextView titleText = (TextView)findViewById(R.id.title_text);
        titleText.setTypeface(Typeface.createFromAsset(getAssets(), "NanumSquareL.ttf"));

        startLoading();
    }

    private void startLoading() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(LoadingActivity.this, LogInActivity.class);
                startActivity(intent);//login activity 실행
                finish();

            }
        }, 2000);
    }
}
