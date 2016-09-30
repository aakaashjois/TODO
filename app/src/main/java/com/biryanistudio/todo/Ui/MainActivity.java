package com.biryanistudio.todo.ui;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.biryanistudio.todo.R;
import com.biryanistudio.todo.fragments.BaseFragment;
import com.biryanistudio.todo.fragments.CompletedFragment;
import com.biryanistudio.todo.utils.CopyListenerService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

	public ViewPager viewPager;
	private CoordinatorLayout coordinatorLayout;
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
		coordinatorLayout = ( CoordinatorLayout ) findViewById(R.id.activity_list);
		final TabLayout tabs = ( TabLayout ) findViewById(R.id.tabs);
		viewPager = ( ViewPager ) findViewById(R.id.viewPager);
		final FloatingActionButton fab = ( FloatingActionButton ) findViewById(R.id.clear);
		fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager(), this);
		viewPager.setAdapter(fragmentPagerAdapter);
		tabs.setupWithViewPager(viewPager);
		fab.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		final int currentTab = viewPager.getCurrentItem();
		final String action = currentTab == 0 ?
				getString(R.string.complete_all_todos) : getString(R.string.clear_all_todos);
		final Snackbar snackbar = createSnackBar(action, Snackbar.LENGTH_LONG);
		snackbar.setAction("Yes", new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				snackbar.dismiss();
				createSnackBar("Cleared", Snackbar.LENGTH_SHORT).show();
				if ( currentTab == 0 ) {
					(( BaseFragment ) fragmentPagerAdapter.getItem(currentTab)).clearAllTasks();
				}
				(( BaseFragment ) fragmentPagerAdapter.getItem(currentTab)).clearAllTasks();
			}
		});
		snackbar.show();
		//TODO: Check if snackbar animations work.
	}

	public void updateCompletedFragment() {
		CompletedFragment fragment = ( CompletedFragment ) fragmentPagerAdapter.getItem(1);
		fragment.updateTasks();
	}

	private Snackbar createSnackBar(final String action, final int snackbarLength) {
		Snackbar snackbar = Snackbar.make(coordinatorLayout, action, snackbarLength);
		View snackbarView = snackbar.getView();
		snackbarView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
		TextView textView = ( TextView ) snackbarView
				.findViewById(android.support.design.R.id.snackbar_text);
		textView.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
		TextView actionView = ( TextView ) snackbarView
				.findViewById(android.support.design.R.id.snackbar_action);
		actionView.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
		actionView.setTypeface(Typeface.create("casual", Typeface.BOLD));
		return snackbar;
	}

	public interface ITasksUpdated {
		//TODO: What does this function do? It is never used.
		void clearAllTasks();
	}
}
