package com.example.ncs.lifeon.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.example.ncs.lifeon.R;

/**
 * Created by PYOJIHYE on 2017-06-06.
 */

public class RegisterFragment extends Fragment {

    private static ViewPager pager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        if(view !=null){
            view.setEnabled(false);
            view.setSaveFromParentEnabled(false);
        }
        pager = (ViewPager) view.findViewById(R.id.pager);
        pager.setAdapter(new MyFragmentPagerAdapter(getActivity().getSupportFragmentManager()));
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        tabStrip.setViewPager(pager);

        return view;
    }
}