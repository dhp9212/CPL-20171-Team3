package kr.soen.wifiapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{
    public static final int ServerPort = 5000;
    public static final String ServerIP = "54.71.172.224";
    //public static final String ServerIP = "14.46.3.23";

    public static final String REQUEST_SIGNUP = "4";
    public static final String COLSE = "6";

    Socket socket;
    public InputStream mmInStream = null;
    public OutputStream mmOutStream = null;
    String taskString;
    boolean isConnect = false;

    String isSuccess;
    Object result;

    Toast logMsg;

    EditText idText, passwordText, passwordCheckText;
    ImageView checkImage;
    Button signupBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        idText = (EditText) findViewById(R.id.id);
        idText.setTypeface(Typeface.createFromAsset(getAssets(), "NanumSquareR.ttf"));
        idText.addTextChangedListener(textWatcher_id);
        idText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    idText.setHint("");
                    passwordText.setHint("비밀번호를 입력하세요.");
                    passwordCheckText.setHint("비밀번호 확인");
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
                    idText.setHint("아이디를 입력하세요.");
                    passwordCheckText.setHint("비밀번호 확인");
                }
                return false;
            }
        });

        passwordCheckText = (EditText) findViewById(R.id.password_check) ;
        passwordCheckText.setTypeface(Typeface.createFromAsset(getAssets(), "NanumSquareR.ttf"));
        passwordCheckText.setTransformationMethod(new MyPasswordTransformationMethod());
        passwordCheckText.addTextChangedListener(textWatcher_pw_check);
        passwordCheckText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    passwordCheckText.setHint("");
                    idText.setHint("아이디를 입력하세요.");
                }
                return false;
            }
        });

        checkImage = (ImageView)findViewById(R.id.check_image);

        signupBtn = (Button) findViewById(R.id.signup_btn);
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
            case R.id.signup_btn:
                String id = idText.getText().toString();
                String password = passwordText.getText().toString();

                String data = REQUEST_SIGNUP + "/" + id + "/" + password;
                doCommu(data);

                if (isSuccess.equals("S"))
                {
                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    intent.putExtra("id", id);
                    startActivity(intent);//main activity 실행
                    finish();
                }
                break;
        }
    }

    TextWatcher textWatcher_id = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable edit) {
            String s = edit.toString();
            if (passwordText.getText().toString().length() > 0 && passwordCheckText.getText().toString().length() > 0 && s.length() > 0) {
                signupBtn.setEnabled(true);
                signupBtn.setTextColor(getResources().getColor(R.color.white));
                signupBtn.setBackgroundResource(R.drawable.btn_background_enabled);
            }
            else {
                signupBtn.setEnabled(false);
                signupBtn.setTextColor(getResources().getColor(R.color.color3));
                signupBtn.setBackgroundResource(R.drawable.btn_background_unabled);
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
            String password = passwordText.getText().toString();
            if (s.length() > 0) {
                passwordCheckText.setEnabled(true);
                if (idText.getText().toString().length() > 0 && passwordCheckText.getText().toString().length() > 0)
                {
                    if (passwordCheckText.getText().toString().equals(password))
                    {
                        checkImage.setImageResource(R.drawable.icon_check_true);
                        signupBtn.setEnabled(true);
                        signupBtn.setTextColor(getResources().getColor(R.color.white));
                        signupBtn.setBackgroundResource(R.drawable.btn_background_enabled);
                    }
                    else
                    {
                        checkImage.setImageResource(R.drawable.icon_check_false);
                        signupBtn.setEnabled(false);
                        signupBtn.setTextColor(getResources().getColor(R.color.color3));
                        signupBtn.setBackgroundResource(R.drawable.btn_background_unabled);
                    }
                }
            }
            else {
                signupBtn.setEnabled(false);
                signupBtn.setTextColor(getResources().getColor(R.color.color3));
                signupBtn.setBackgroundResource(R.drawable.btn_background_unabled);
            }
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
    };

    TextWatcher textWatcher_pw_check = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable edit) {
            String s = edit.toString();
            String password = passwordText.getText().toString();
            if (idText.getText().toString().length() > 0 && password.length() > 0 && s.length() > 0) {
                if (passwordCheckText.getText().toString().equals(password))
                {
                    checkImage.setImageResource(R.drawable.icon_check_true);
                    signupBtn.setEnabled(true);
                    signupBtn.setTextColor(getResources().getColor(R.color.white));
                    signupBtn.setBackgroundResource(R.drawable.btn_background_enabled);
                }
                else
                {
                    checkImage.setImageResource(R.drawable.icon_check_false);
                    signupBtn.setEnabled(false);
                    signupBtn.setTextColor(getResources().getColor(R.color.color3));
                    signupBtn.setBackgroundResource(R.drawable.btn_background_unabled);
                }
            }
            else {
                if (passwordCheckText.getText().toString().equals(password))
                {
                    checkImage.setImageResource(R.drawable.icon_check_true);
                }
                else
                {
                    checkImage.setImageResource(R.drawable.icon_check_false);
                }
                signupBtn.setEnabled(false);
                signupBtn.setTextColor(getResources().getColor(R.color.color3));
                signupBtn.setBackgroundResource(R.drawable.btn_background_unabled);
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
                    logMessege("회원가입 실패");
                }
                else if(isSuccess.equals("F_id"))
                {
                    logMessege("중복된 아이디입니다.");
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
                    Log.d("SOCKET", "서버 연결 요청 _ SignUpActivity 소켓 생성");
                    isConnect = true;
                }

                if(mmInStream == null) {
                    mmInStream = socket.getInputStream();
                    mmOutStream = socket.getOutputStream();
                    Log.d("SOCKET","stream open _ SignUpActivity");
                    if(mmInStream == null){
                        Log.d("SOCKET","서버 연결 안됨 _ SignUpActivity");
                    }else{
                        Log.d("SOCKET","서버 연결 성공 _ SignUpActivity");
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
                Log.e("CLOSE SOCKET","error");
                Log.e("CLOSE SOCKET",t.toString());
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
