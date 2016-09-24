package com.biryanistudio.todo.Ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.biryanistudio.todo.Fragments.BaseFragment;
import com.biryanistudio.todo.Fragments.CompletedFragment;
import com.biryanistudio.todo.R;
import com.biryanistudio.todo.Utils.CopyListenerService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private CoordinatorLayout coordinatorLayout;
    public ViewPager viewPager;
    private FragmentPagerAdapter fragmentPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUi();
        Intent service = new Intent(this, CopyListenerService.class);
        startService(service);
    }

    private void initUi() {
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.activity_list);
        final TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        final FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.clear);
        fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(fragmentPagerAdapter);
        tabs.setupWithViewPager(viewPager);
        floatingActionButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        final int currentTab = viewPager.getCurrentItem();
        final String action = currentTab == 0 ? "Completed all TODOs?" : "Clear all TODOs?";
        Snackbar snackbar = Snackbar.make(coordinatorLayout, action, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        snackbar.setActionTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        snackbar.setAction("Clear", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Cleared", Toast.LENGTH_SHORT).show();
                if (currentTab == 0) {
                    ((BaseFragment) fragmentPagerAdapter.getItem(currentTab)).clearAllTasks();
                }
                ((BaseFragment) fragmentPagerAdapter.getItem(currentTab)).clearAllTasks();
            }
        });
        snackbar.show();
    }

    public void updateCompletedFragment() {
        CompletedFragment fragment = (CompletedFragment) fragmentPagerAdapter.getItem(1);
        fragment.updateTasks();
    }

    public interface ITasksUpdated {
        void clearAllTasks();
    }
}
