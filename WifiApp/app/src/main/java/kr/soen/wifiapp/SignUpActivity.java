package kr.soen.wifiapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{
    public static final int ServerPort = 5000;
    public static final String ServerIP = "54.71.172.224";
    //public static final String ServerIP = "14.46.3.96";

    public static final String REQUEST_SIGNUP = "4";
    public static final String COLSE = "5";
    public static final int RESULT_ID_AND_PASSWORD = 2;

    Socket socket;
    public InputStream mmInStream;
    public OutputStream mmOutStream;
    String taskString;

    TextView textView1, textView2, textView3;

    EditText idText, passwordText;
    Button signupBtn;

    Toast logMsg;

    String id;
    String password;
    String data;

    Object result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        textView1 = (TextView)findViewById(R.id.id_text) ;
        textView1.setTypeface(Typeface.createFromAsset(getAssets(), "NanumBarunpenR.ttf"));

        textView2 = (TextView)findViewById(R.id.password_text) ;
        textView2.setTypeface(Typeface.createFromAsset(getAssets(), "NanumBarunpenR.ttf"));

        idText = (EditText)findViewById(R.id.id);
        idText.setTypeface(Typeface.createFromAsset(getAssets(), "NanumBarunpenR.ttf"));
        passwordText = (EditText)findViewById(R.id.password);
        passwordText.setTypeface(Typeface.createFromAsset(getAssets(), "NanumBarunpenR.ttf"));

        textView3 = (TextView)findViewById(R.id.extra_text) ;
        textView3.setTypeface(Typeface.createFromAsset(getAssets(), "NanumBarunpenR.ttf"));

        signupBtn = (Button) findViewById(R.id.signup_btn);
        signupBtn.setTypeface(Typeface.createFromAsset(getAssets(), "NanumBarunpenR.ttf"));
        signupBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.signup_btn:
                id = idText.getText().toString();
                password = passwordText.getText().toString();

                if (id.equals("") && password.equals("")) {
                    logMessege("ID와 PASSWORD를 입력하세요.");
                    break;
                } else if (id.equals("")) {
                    logMessege("ID를 입력하세요.");
                    break;
                } else if (password.equals("")) {
                    logMessege("PASSWORD를 입력하세요.");
                    break;
                } else {
                    Log.d("SOCKET", "등록 시도");
                    data = REQUEST_SIGNUP + "/" + id + "/" + password;
                    doCommu(data);
                    Log.i("SOCKET", data);

                    Intent data = new Intent();
                    data.putExtra("id", idText.getText().toString());
                    data.putExtra("password", passwordText.getText().toString());
                    setResult(RESULT_ID_AND_PASSWORD, data);
                    finish();
                    break;
                }
        }
    }

    public void doCommu(String msg)
    {
        try {
            result = new CommuTask().execute(msg).get();
            doClose();

            if (result instanceof Exception)
            {
                logMessege(result.toString());
            }
            else
            {
                if(result.toString().equals("S"))
                {
                    logMessege("계정 등록 성공");
                }
                else
                {
                    logMessege("계정 등록 실패");
                }
            }
        } catch (Exception e)
        {
            logMessege(e.toString());
        }
    }

    public void doClose()
    {
        new CloseTask().execute(COLSE);
    }

    private class CommuTask extends AsyncTask<String, String, Object> {
        @Override
        protected String doInBackground(String... params) {

            Log.d("SOCKET","실행되어야함");

            try {
                taskString = params[0];

                socket = new Socket(ServerIP, ServerPort);

                mmInStream = socket.getInputStream();
                mmOutStream = socket.getOutputStream();

                if(mmInStream == null){
                    Log.d("SOCKET","서버 연결 안됨 _ SIGN");
                }else{
                    Log.d("SOCKET","서버 연결 성공 _ SIGN");
                }

                mmOutStream.write(taskString.getBytes("UTF-8"));
                mmOutStream.flush();

                byte[] inbuff = new byte[1024];
                clearArray(inbuff);//buffer clear
                int len;
                len = mmInStream.read(inbuff);

                return new String(inbuff, 0, len, "UTF-8");
            } catch (Throwable t) {
                Log.e("SOCKET","error");
                Log.e("SOCKET",t.toString());
                return t.toString();
            }
        }

        @Override
        protected void onPostExecute(Object result) { }
    }

    private class CloseTask extends AsyncTask<String, String, Object> {
        @Override
        protected String doInBackground(String... params) {
            try {
                taskString = params[0];

                mmOutStream.write(taskString.getBytes("UTF-8"));
                mmOutStream.flush();

                mmInStream.close();
                mmInStream = null;
                mmOutStream.close();
                mmOutStream = null;
                socket.close();
                socket = null;

                String result = "S";

                return result;
            } catch (Throwable t) {
                Log.e("CLOSE SOCKET","error");
                Log.e("CLOSE SOCKET",t.toString());
                return t.toString();
            }
        }

        @Override
        protected void onPostExecute(Object result) {
            if (result instanceof Exception)
            {
                logMessege(result.toString());
            }
        }
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
