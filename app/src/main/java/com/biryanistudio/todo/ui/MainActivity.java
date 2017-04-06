package com.biryanistudio.todo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.biryanistudio.todo.R;
import com.biryanistudio.todo.adapters.FragmentPagerAdapter;
import com.biryanistudio.todo.db.DbTransactions;
import com.biryanistudio.todo.fragments.BaseFragment;
import com.biryanistudio.todo.fragments.PendingFragment;
import com.biryanistudio.todo.services.CopyListenerService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

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
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.activity_list);
        final TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.clear);
        fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(fragmentPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position,
                                       float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        fab.setImageResource(R.drawable.done_clear_animation);
                        break;
                    case 1:
                        fab.setImageResource(R.drawable.clear_done_animation);
                        break;
                }
                /*
                FIXME: Get the animation to work
                Drawable drawable = fab.getDrawable();
                if (drawable instanceof Animatable)
                    ((Animatable) drawable).start();
                 */
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        tabs.setupWithViewPager(viewPager);
        fab.setOnClickListener(this);
        final TextInputEditText taskInput =
                (TextInputEditText) findViewById(R.id.task_input);
        taskInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    Log.v("Testing EditText", textView.getText().toString().trim());
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
                getString(R.string.complete_all_todos) :
                getString(R.string.clear_all_todos);
        final String actionMessage = currentTab == 0 ?
                getString(R.string.complete_all_todos_message) :
                getString(R.string.clear_all_todos_message);
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