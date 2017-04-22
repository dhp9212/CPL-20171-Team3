package kr.soen.wificlienttest;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Temp2Fragment extends Fragment{
    private static final String ARG_SECTION_NUMBER = "section_number";

    public Temp2Fragment() {
    }

    public static Temp2Fragment newInstance(int sectionNumber) {
        Temp2Fragment fragment = new Temp2Fragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_temp2, container, false);
        return rootView;
    }
}
