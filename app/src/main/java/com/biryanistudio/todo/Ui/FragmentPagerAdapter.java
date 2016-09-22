package com.biryanistudio.todo.Ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.biryanistudio.todo.Completed.CompletedFragment;
import com.biryanistudio.todo.Pending.PendingFragment;

class FragmentPagerAdapter extends FragmentStatePagerAdapter {

    final private PendingFragment pendingFragment;
    final private CompletedFragment completedFragment;

    FragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        pendingFragment = new PendingFragment();
        completedFragment = new CompletedFragment();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return pendingFragment;
            case 1:
                return completedFragment;
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
