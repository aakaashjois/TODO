package com.biryanistudio.todo.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.biryanistudio.todo.R;
import com.biryanistudio.todo.fragments.BaseFragment;
import com.biryanistudio.todo.fragments.CompletedFragment;
import com.biryanistudio.todo.fragments.PendingFragment;

import java.util.ArrayList;

public class FragmentPagerAdapter extends FragmentStatePagerAdapter {

    final private Context context;
    final private PendingFragment pendingFragment;
    final private CompletedFragment completedFragment;
    private static final int TAB_COUNT = 2;

    public FragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
        pendingFragment = new PendingFragment();
        completedFragment = new CompletedFragment();
    }

    @Override
    public Fragment getItem(int position) {
        return position == 0 ? pendingFragment : position == 1 ? completedFragment : null;
    }

    @Override
    public int getCount() {
        return TAB_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return position == 0 ? context.getString(R.string.pending_tab_title) :
                position == 1 ? context.getString(R.string.completed_tab_title) :
                        super.getPageTitle(position);
    }
}