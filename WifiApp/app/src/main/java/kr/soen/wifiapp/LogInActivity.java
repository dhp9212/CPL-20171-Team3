package kr.soen.wifiapp;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener{
    public static final int ServerPort = 5000;
    public static final String ServerIP = "54.71.172.224";
    //public static final String ServerIP = "14.46.3.23";

    public static final String REQUEST_LOGIN = "5";
    public static final String COLSE = "6";

    Socket socket;
    public InputStream mmInStream = null;
    public OutputStream mmOutStream = null;
    String taskString;
    boolean isConnect = false;

    String isSuccess;
    Object result;

    Toast logMsg;

    EditText idText, passwordText;
    Button logBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        idText = (EditText) findViewById(R.id.id);
        idText.setTypeface(Typeface.createFromAsset(getAssets(), "NanumSquareR.ttf"));
        idText.addTextChangedListener(textWatcher_id);
        idText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    idText.setHint("");
                    passwordText.setHint("비밀번호");
                }
                return false;
            }
        });

        passwordText = (EditText) findViewById(R.id.password) ;
        passwordText.setTypeface(Typeface.createFromAsset(getAssets(), "NanumSquareR.ttf"));
        passwordText.setTransformationMethod(new MyPasswordTransformationMethod());
        passwordText.addTextChangedListener(textWatcher_pw);
        passwordText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    passwordText.setHint("");
                    idText.setHint("아이디");
                }
                return false;
            }
        });
        passwordText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        passwordText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_DONE)
                {
                    String id = idText.getText().toString();
                    String password = passwordText.getText().toString();

                    if (id.equals("") && password.equals("")) {
                        logMessege("아이디와 비밀번호를 입력하세요.");
                    } else if (id.equals("")) {
                        logMessege("아이디를 입력하세요.");
                    } else if (password.equals("")) {
                        logMessege("비밀번호를 입력하세요.");
                    } else {
                        String data = REQUEST_LOGIN + "/" + id + "/" + password;
                        doCommu(data);

                        if (isSuccess.equals("S"))
                        {
                            Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                            intent.putExtra("id", id);
                            startActivity(intent);//main activity 실행
                            finish();
                        }
                    }
                    return true;
                }
                return false;
            }
        });

        logBtn = (Button)findViewById(R.id.login_btn);
        logBtn.setTypeface(Typeface.createFromAsset(getAssets(), "NanumSquareR.ttf"));
        logBtn.setOnClickListener(this);

        TextView text1 = (TextView)findViewById(R.id.text1);
        text1.setTypeface(Typeface.createFromAsset(getAssets(), "NanumSquareR.ttf"));

        Button signupBtn = (Button)findViewById(R.id.signup);
        signupBtn.setTypeface(Typeface.createFromAsset(getAssets(), "NanumSquareR.ttf"));
        signupBtn.setOnClickListener(this);
    }

    @Override
    public void onDestroy()
    {
        Log.d(this.getClass().getSimpleName(), "onDestroy()");
        super.onDestroy();

        if (isConnect)
        {
            doClose();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.login_btn:
                String id = idText.getText().toString();
                String password = passwordText.getText().toString();

                String data = REQUEST_LOGIN + "/" + id + "/" + password;
                doCommu(data);

                if (isSuccess.equals("S"))
                {
                    Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                    intent.putExtra("id", id);
                    startActivity(intent);//main activity 실행
                    finish();
                }
                break;
            case R.id.signup:
                Intent intent = new Intent(LogInActivity.this, SignUpActivity.class);
                startActivity(intent);//signup activity 실행
                finish();
                break;
        }
    }

    TextWatcher textWatcher_id = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable edit) {
            String s = edit.toString();
            if (passwordText.getText().toString().length() > 0 && s.length() > 0) {
                logBtn.setEnabled(true);
                logBtn.setTextColor(getResources().getColor(R.color.white));
                logBtn.setBackgroundResource(R.drawable.btn_background_enabled);
            }
            else {
                logBtn.setEnabled(false);
                logBtn.setTextColor(getResources().getColor(R.color.color3));
                logBtn.setBackgroundResource(R.drawable.btn_background_unabled);
            }
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
    };

    TextWatcher textWatcher_pw = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable edit) {
            String s = edit.toString();
            if (idText.getText().toString().length() > 0 && s.length() > 0) {
                logBtn.setEnabled(true);
                logBtn.setTextColor(getResources().getColor(R.color.white));
                logBtn.setBackgroundResource(R.drawable.btn_background_enabled);
            }
            else {
                logBtn.setEnabled(false);
                logBtn.setTextColor(getResources().getColor(R.color.color3));
                logBtn.setBackgroundResource(R.drawable.btn_background_unabled);
            }
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
    };

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
                isSuccess = result.toString();

                if(isSuccess.equals("F"))
                {
                    logMessege("로그인 실패");
                }
                else if(isSuccess.equals("F_login"))
                {
                    logMessege("아이디/비밀번호가 틀렸습니다.");
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
            try {
                taskString = params[0];

                if (!isConnect)
                {
                    socket = new Socket(ServerIP, ServerPort);
                    Log.d("SOCKET", "서버 연결 요청 _ LogInActivity 소켓 생성");
                    isConnect = true;
                }

                if(mmInStream == null) {
                    mmInStream = socket.getInputStream();
                    mmOutStream = socket.getOutputStream();
                    Log.d("SOCKET","stream open _ LogInActivity");
                    if(mmInStream == null){
                        Log.d("SOCKET","서버 연결 안됨 _ LogInActivity");
                    }else{
                        Log.d("SOCKET","서버 연결 성공 _ LogInActivity");
                    }
                }

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

                isConnect = false;

                String result = "S";

                return result;
            } catch (Throwable t) {
                return t.toString();
            }
        }

        @Override
        protected void onPostExecute(Object result) {}
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
