package com.biryanistudio.todo.userinterface;

import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.biryanistudio.todo.R;
import com.biryanistudio.todo.adapters.FragmentPagerAdapter;
import com.biryanistudio.todo.database.DbTransactions;
import com.biryanistudio.todo.fragments.BaseFragment;
import com.biryanistudio.todo.fragments.PendingFragment;
import com.biryanistudio.todo.services.CopyListenerService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private ViewPager viewPager;
    private CoordinatorLayout coordinatorLayout;
    private FragmentPagerAdapter fragmentPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(this, CopyListenerService.class));
        initUi();
    }

    private void initUi() {
        coordinatorLayout = findViewById(R.id.activity_list);
        final TabLayout tabs = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.viewPager);
        final FloatingActionButton fab = findViewById(R.id.clear);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            fab.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.done_clear_animation, null));
        else
            fab.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_done_all, null));
        fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(fragmentPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    fab.setImageResource(position == 0 ? R.drawable.clear_done_animation :
                            R.drawable.done_clear_animation);
                    ((AnimatedVectorDrawable) fab.getDrawable()).start();
                } else
                    fab.setImageResource(position == 0 ? R.drawable.ic_done_all :
                            R.drawable.ic_clear_all);
                ((BaseFragment) fragmentPagerAdapter.getItem(position)).updateTasks();
            }
        });
        tabs.setupWithViewPager(viewPager);
        fab.setOnClickListener(this);
        final TextInputEditText taskInput =
                findViewById(R.id.task_input);
        taskInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    DbTransactions.writeTask(
                            MainActivity.this, textView.getText().toString().trim());
                    ((PendingFragment) fragmentPagerAdapter.getItem(0)).updateTasks();
                    textView.setText(null);
                    taskInput.clearFocus();
                    coordinatorLayout.requestFocus();
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(textView.getWindowToken(), 0);
                }
                return true;
            }
        });
    }

    @Override
    public void onClick(View view) {
        final int currentTab = viewPager.getCurrentItem();
        final String action = currentTab == 0 ?
                getString(R.string.complete_all_tasks) :
                getString(R.string.clear_all_tasks);
        final String actionMessage = currentTab == 0 ?
                getString(R.string.complete_all_tasks_message) :
                getString(R.string.clear_all_tasks_message);
        final Snackbar snackbar = UiUtils.createSnackBar(MainActivity.this,
                coordinatorLayout,
                action,
                Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.yes, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((BaseFragment) fragmentPagerAdapter.getItem(currentTab)).clearAllTasks();
                snackbar.dismiss();
                UiUtils.createSnackBar(MainActivity.this,
                        coordinatorLayout,
                        actionMessage,
                        Snackbar.LENGTH_SHORT).show();
            }
        });
        snackbar.show();
    }
}