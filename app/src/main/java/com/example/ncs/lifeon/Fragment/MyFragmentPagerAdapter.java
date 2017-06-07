package com.example.ncs.lifeon.Fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by PYOJIHYE on 2017-06-06.
 */

public class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {

    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return RegisterGPSFragment.getInstance();
            case 1:
                return RegisterPhoneFragment.getInstance();
            case 2:
                return RegisterTimeFragment.getInstance();
            default:
                break;
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "GPS";
            case 1:
                return "Phone";
            case 2:
                return "Time";
        }
        return "";
    }
}