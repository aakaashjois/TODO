package com.biryanistudio.todo;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Aakaash on 19/09/16 at 3:53 PM.
 */

class FragmentPagerAdapter extends FragmentStatePagerAdapter {

    FragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new PendingFragment();
            case 1:
                return new CompletedFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Pending Tasks";
            case 1:
                return "Completed Tasks";
        }
        return super.getPageTitle(position);
    }
}
