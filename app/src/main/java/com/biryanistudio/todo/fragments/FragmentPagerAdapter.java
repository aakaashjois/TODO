package com.biryanistudio.todo.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.biryanistudio.todo.R;

public class FragmentPagerAdapter extends FragmentStatePagerAdapter {

    final private Context context;
    final private PendingFragment pendingFragment;
    final private CompletedFragment completedFragment;
    private final int tabCount;

    public FragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
        pendingFragment = new PendingFragment();
        completedFragment = new CompletedFragment();
        tabCount = 2;
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
        return tabCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getString(R.string.pending_tab_title);
            case 1:
                return context.getString(R.string.completed_tab_title);
        }
        return super.getPageTitle(position);
    }
}