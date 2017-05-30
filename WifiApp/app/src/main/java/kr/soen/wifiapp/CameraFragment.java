package kr.soen.wifiapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CameraFragment extends Fragment implements View.OnClickListener{

    WebView mWebView;
    EditText alertAddress;

    AlertDialog dialog;

    boolean isFirst = true;
    String address = null;

    public CameraFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null) {
            isFirst = savedInstanceState.getBoolean("isFirst");
            address = savedInstanceState.getString("address");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean("isFirst", isFirst);
        savedInstanceState.putString("address", address);
    }

    @Override
    @SuppressLint( "SetJavaScriptEnabled" )
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.fragment_camera, container, false);

        ImageButton backBtn = (ImageButton)layout.findViewById(R.id.btn_back);
        backBtn.setOnClickListener(this);

        TextView titleText = (TextView)layout.findViewById(R.id.title_text) ;
        titleText.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "NanumSquareEB.ttf"));

        /////////////

        mWebView = (WebView)layout.findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(address);
        mWebView.setWebViewClient(new WebViewClientClass());

        ////////////

        LayoutInflater inflater1 = (LayoutInflater)getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater1.inflate(R.layout.alert_layout, null);

        TextView alertTitle = (TextView)view.findViewById(R.id.alert_title);
        alertTitle.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "NanumSquareL.ttf"));

        TextView alertMsg = (TextView)view.findViewById(R.id.alert_msg);
        alertMsg.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "NanumSquareR.ttf"));

        alertAddress = (EditText) view.findViewById(R.id.address);
        alertAddress.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "NanumSquareR.ttf"));
        alertAddress.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    alertAddress.setHint("");
                }
                return false;
            }
        });
        alertAddress.setImeOptions(EditorInfo.IME_ACTION_DONE);
        alertAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_DONE)
                {
                    address = alertAddress.getText().toString();
                    mWebView.loadUrl(address);
                    isFirst = false;
                    dialog.dismiss();
                    return true;
                }
                return false;
            }
        });

        Button alertBtn = (Button)view.findViewById(R.id.ok_btn);
        alertBtn.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "NanumSquareR.ttf"));
        alertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                address = alertAddress.getText().toString();
                mWebView.loadUrl(address);
                isFirst = false;
                dialog.dismiss();
            }
        });

        AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        ad.setView(view);

        dialog = ad.create();

        return layout;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser)
        {
            if(isFirst)
            {
                dialog.show();
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btn_back:
                ((MainActivity) getActivity()).vp.setCurrentItem(1);
                break;
        }
    }

    private class WebViewClientClass extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            view.loadUrl(url);
            return true;
        }
    }
}
