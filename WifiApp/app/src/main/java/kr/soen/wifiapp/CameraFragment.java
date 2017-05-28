package kr.soen.wifiapp;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CameraFragment extends Fragment implements View.OnClickListener{

    public CameraFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        WebView mWebView = (WebView)layout.findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl("http://google.com");
        mWebView.setWebViewClient(new WebViewClientClass());

        return layout;
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
