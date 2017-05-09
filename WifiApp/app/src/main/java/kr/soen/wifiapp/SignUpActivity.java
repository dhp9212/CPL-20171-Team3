package kr.soen.wifiapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{
    public static final int ServerPort = 5000;
    public static final String ServerIP = "54.71.172.224 ";
    //public static final String ServerIP = "118.41.247.141";

    public static final String REQUEST_SIGNUP = "4";
    public static final int RESULT_ID_AND_PASSWORD = 2;

    Socket socket;
    public InputStream mmInStream;
    public OutputStream mmOutStream;
    String taskString;

    EditText idText, passwordText;
    Button signupBtn;

    WifiManager wifiManager;
    Toast logMsg;

    String id;
    String password;
    String data;

    Object result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        idText = (EditText)findViewById(R.id.id);
        passwordText = (EditText)findViewById(R.id.password);

        signupBtn = (Button) findViewById(R.id.signup_btn);
        signupBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.signup_btn:
                if (wifiManager.isWifiEnabled())
                {
                    id = idText.getText().toString();
                    password = passwordText.getText().toString();
                    if(id.equals("") && password.equals(""))
                    {
                        logMessege("ID와 PASSWORD를 입력하세요.");
                        break;
                    }
                    else if(id.equals(""))
                    {
                        logMessege("ID를 입력하세요.");
                        break;
                    }
                    else if (password.equals(""))
                    {
                        logMessege("PASSWORD를 입력하세요.");
                        break;
                    }
                    else
                    {
                        data = REQUEST_SIGNUP + "/" + id + "/" + password;
                        doCommu(data);

                        Intent data = new Intent();
                        data.putExtra("id", idText.getText().toString());
                        data.putExtra("password", passwordText.getText().toString());
                        setResult(RESULT_ID_AND_PASSWORD, data);
                        finish();
                        break;
                    }
                }

                logMessege("WiFi가 활성화되지 않았습니다.");
                break;
        }
    }

    public void doCommu(String msg)
    {
        try {
            result = new CommuTask().execute(msg).get();

            if (result instanceof Exception)
            {
                logMessege(result.toString());
            }
            else
            {
                logMessege(result.toString());
                logMessege("앱을 다시 시작해주세요.");

            }
        } catch (Exception e)
        {
            logMessege(e.toString());
        }
    }

    private class CommuTask extends AsyncTask<String, String, Object> {
        @Override
        protected String doInBackground(String... params) {
            try {
                taskString = params[0];

                socket = new Socket(ServerIP, ServerPort);
                mmInStream = socket.getInputStream();
                mmOutStream = socket.getOutputStream();

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

    public void clearArray(byte[] buff) {
        for (int i = 0; i < buff.length; i++)
        {
            buff[i] = 0;
        }
    }

    public void logMessege(String log) {
        logMsg = Toast.makeText(this, log, Toast.LENGTH_SHORT);
        logMsg.show();
    }
}
