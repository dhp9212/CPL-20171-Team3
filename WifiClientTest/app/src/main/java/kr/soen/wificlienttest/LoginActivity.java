package kr.soen.wificlienttest;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.jungly.gridpasswordview.GridPasswordView;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    Button signupBtn;
    GridPasswordView pswview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pswview = (GridPasswordView)findViewById(R.id.pswView);
        signupBtn = (Button)findViewById(R.id.signup_button);
        signupBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.signup_button) {
            SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
            if (pref.getString("password", "").equals(pswview.getPassWord()))
            {
                //다음엑티비티
            }

        }
    }
}
