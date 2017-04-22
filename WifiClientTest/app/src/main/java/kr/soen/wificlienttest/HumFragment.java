package kr.soen.wificlienttest;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class HumFragment extends Fragment{
    private static final String ARG_SECTION_NUMBER = "section_number";

    TextView humText;
    TextView humUnit;

    public HumFragment() {
    }

    public static HumFragment newInstance(int sectionNumber) {
        HumFragment fragment = new HumFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_hum, container, false);

        //현재 습도 표시 text 초기화
        humText = (TextView) rootView.findViewById(R.id.humtext);
        humText.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "NanumBarunGothicUltraLight.ttf"));

        //습도 단위 표시 text 설정(이후 변화 없음)
        humUnit = (TextView) rootView.findViewById(R.id.humunit);
        humUnit.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "NanumBarunGothicUltraLight.ttf"));

        return rootView;
    }

    public void setTextView(String current_hum, String avg_hum)
    {
        humText.setText(current_hum);
    }
}