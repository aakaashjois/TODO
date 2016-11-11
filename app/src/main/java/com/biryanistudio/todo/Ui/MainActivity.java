package com.biryanistudio.todo.ui;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.biryanistudio.todo.R;
import com.biryanistudio.todo.db.DbTransactions;
import com.biryanistudio.todo.fragments.BaseFragment;
import com.biryanistudio.todo.fragments.CompletedFragment;
import com.biryanistudio.todo.fragments.PendingFragment;
import com.biryanistudio.todo.utils.CopyListenerService;
import com.facebook.FacebookSdk;
import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSettings;
import com.facebook.ads.NativeAd;
import com.facebook.appevents.AppEventsLogger;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewPager viewPager;
    private CoordinatorLayout coordinatorLayout;
    private FragmentPagerAdapter fragmentPagerAdapter;
    private NativeAd nativeAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUi();
        Intent service = new Intent(this, CopyListenerService.class);
        startService(service);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication());
        showNativeAd();
    }

    private void showNativeAd() {
        nativeAd = new NativeAd(this, "1226886224071788_1226888620738215");
        FrameLayout adChoicesHolder = (FrameLayout) findViewById(R.id.ad_layout_container);
        AdChoicesView adChoicesView = new AdChoicesView(getApplicationContext(), nativeAd, true);
        adChoicesHolder.addView(adChoicesView);
        nativeAd.setAdListener(new AdListener() {

            @Override
            public void onError(Ad ad, AdError error) {

            }

            @Override
            public void onAdLoaded(Ad ad) {
                if (ad != nativeAd) return;

                FrameLayout adLayoutContainer = (FrameLayout) findViewById(R.id.ad_layout_container);
                LinearLayout adView = (LinearLayout) LayoutInflater.from(MainActivity.this)
                        .inflate(R.layout.ad_layout, adLayoutContainer, false);
                adLayoutContainer.addView(adView);

                TextView adTitle = (TextView) findViewById(R.id.ad_title);
                ImageView adIcon = (ImageView) findViewById(R.id.ad_icon);
                Button adCta = (Button) findViewById(R.id.ad_cta);

                adTitle.setText(nativeAd.getAdTitle());
                adCta.setText(nativeAd.getAdCallToAction());
                NativeAd.downloadAndDisplayImage(nativeAd.getAdIcon(), adIcon);

                nativeAd.registerViewForInteraction(adView);
            }

            @Override
            public void onAdClicked(Ad ad) {

            }
        });
        AdSettings.addTestDevice("0c65328ace843e643fe067f97e4b2a33");
        nativeAd.loadAd();
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
                        fab.setImageResource(R.drawable.ic_clear_task);
                        break;
                    case 1:
                        fab.setImageResource(R.drawable.ic_remove_task);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        tabs.setupWithViewPager(viewPager);
        fab.setOnClickListener(this);
        final TextInputLayout taskInputLayout =
                (TextInputLayout) findViewById(R.id.task_input_layout);
        final TextInputEditText taskInput =
                (TextInputEditText) findViewById(R.id.task_input);
        taskInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 140) {
                    taskInputLayout.setErrorEnabled(true);
                    taskInputLayout.setError("Exceeded limit");
                    taskInput.setPaintFlags(
                            taskInput.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    taskInput.setAlpha(0.7f);
                } else {
                    taskInputLayout.setError("");
                    taskInputLayout.setErrorEnabled(false);
                    taskInput.setPaintFlags(0);
                    taskInput.setAlpha(1f);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        taskInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE)
                    if (taskInputLayout.isErrorEnabled())
                        if (textView.getText().toString().trim().equals(""))
                            textView.setError("Enter a longer //TODO");
                        else
                            textView.setError("Enter a shorter //TODO");
                    else {
                        DbTransactions.writeTask(MainActivity.this, textView.getText().toString().trim());
                        TasksAdapter tasksAdapter = ((BaseFragment) fragmentPagerAdapter.getItem(viewPager.getCurrentItem())).getRecyclerAdapter();
                        tasksAdapter.notifyItemInserted(tasksAdapter.getItemCount());
                        textView.setText(null);
                        textView.clearFocus();
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
                getString(R.string.complete_all_todos) : getString(R.string.clear_all_todos);
        final String actionMessage = currentTab == 0 ?
                getString(R.string.complete_all_todos_message) : getString(R.string.clear_all_todos_message);
        final Snackbar snackbar = createSnackBar(action, Snackbar.LENGTH_LONG);
        snackbar.setAction("Yes", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentTab == 0)
                    ((BaseFragment) fragmentPagerAdapter.getItem(currentTab)).clearAllTasks();
                else
                    ((BaseFragment) fragmentPagerAdapter.getItem(currentTab)).clearAllTasks();
                snackbar.dismiss();
                createSnackBar(actionMessage, Snackbar.LENGTH_SHORT).show();
            }
        });
        snackbar.show();
    }

    public void updateCompletedFragment() {
        CompletedFragment fragment = (CompletedFragment) fragmentPagerAdapter.getItem(1);
        fragment.updateTasks();
    }

    public void updatePendingFragment() {
        PendingFragment fragment = (PendingFragment) fragmentPagerAdapter.getItem(0);
        fragment.updateTasks();
    }

    private Snackbar createSnackBar(final String action, final int snackbarLength) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, action, snackbarLength);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        TextView textView = (TextView) snackbarView
                .findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        TextView actionView = (TextView) snackbarView
                .findViewById(android.support.design.R.id.snackbar_action);
        actionView.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        actionView.setTypeface(Typeface.create("casual", Typeface.BOLD));
        return snackbar;
    }
}
