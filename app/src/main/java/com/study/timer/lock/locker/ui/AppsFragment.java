package com.study.timer.lock.locker.ui;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.study.timer.lock.R;
import com.study.timer.lock.locker.appselect.AppAdapter;
import com.study.timer.lock.locker.appselect.AppAdapter.OnEventListener;
import com.study.timer.lock.locker.appselect.AppListElement;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

public class AppsFragment extends Fragment implements OnItemClickListener,
		OnEventListener {
    private AppAdapter mAdapter;

	private static DecimalFormat df2 = new DecimalFormat("#.##");

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

        ListView mListView = (ListView) inflater.inflate(R.layout.fragment_applist,
                container, false);
		mAdapter = new AppAdapter(getActivity());
		mAdapter.setOnEventListener(this);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);

		setHasOptionsMenu(true);
		getActivity().setTitle(R.string.fragment_title_apps);
		return mListView;
	}

	private Toast mLockedToast;

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		AppListElement item = (AppListElement) mAdapter.getItem(position);
		if (item.isApp()) {
			mAdapter.toggle(item);
			showToastSingle(item.locked, item.title);

			// Update lock image
			view.findViewById(R.id.applist_item_image).setVisibility(
					item.locked ? View.VISIBLE : View.GONE);

			view.findViewById(R.id.time_display).setVisibility(
					item.locked ? View.GONE : View.VISIBLE);

			// And the menu
			//updateMenuLayout();

			//start timer
			if (item.locked)
				startCountDownTimer(view, position);

		}


	}

	private void showToast(String text) {
		if (mLockedToast != null) {
			mLockedToast.cancel();
		}

		mLockedToast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
		mLockedToast.show();
	}

	private void showToastSingle(boolean locked, String title) {
		showToast(getString(locked ? R.string.apps_toast_locked_single
				: R.string.apps_toast_unlocked_single, title));
	}

	private void showToastAll(boolean locked) {
		showToast(getString(locked ? R.string.apps_toast_locked_all
				: R.string.apps_toast_unlocked_all));
	}

	public void onSearch(String query) {
		Log.d("AppsFragment", "onSearch (query=" + query + ")");
	}

	private Menu mMenu;

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.apps, menu);
		mMenu = menu;
		//updateMenuLayout();

		SearchManager sm = (SearchManager) getActivity().getSystemService(
				Context.SEARCH_SERVICE);
		MenuItem mi = (MenuItem) menu.findItem(R.id.apps_menu_search);
		SearchView sv = (SearchView) MenuItemCompat.getActionView(mi);
		sv.setSearchableInfo(sm.getSearchableInfo(getActivity()
				.getComponentName()));

	}

//	public void updateMenuLayout() {
//		boolean all = mAdapter.areAllAppsLocked();
//		if (mMenu != null && mAdapter.isLoadComplete()) {
//			mMenu.findItem(R.id.apps_menu_lock_all).setVisible(!all);
//			mMenu.findItem(R.id.apps_menu_unlock_all).setVisible(all);
//		}
//	}

//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		switch (item.getItemId()) {
//		case R.id.apps_menu_sort:
//			mAdapter.sort();
//			break;
//		case R.id.apps_menu_lock_all:
//			onLockAllOptions(true);
//			break;
//		case R.id.apps_menu_unlock_all:
//			onLockAllOptions(false);
//			break;
//		case R.id.apps_menu_search:
//			break;
//		}
//		return super.onOptionsItemSelected(item);
//	}

//	private void onLockAllOptions(boolean lockall) {
//		mMenu.findItem(R.id.apps_menu_lock_all).setVisible(!lockall);
//		mMenu.findItem(R.id.apps_menu_unlock_all).setVisible(lockall);
//		mAdapter.prepareUndo();
//		mAdapter.setAllLocked(lockall);
//		showToastAll(lockall);
//	}

	@Override
	public void onLoadComplete() {
		//updateMenuLayout();
	}

	@Override
	public void onDirtyStateChanged(boolean dirty) {
		mMenu.findItem(R.id.apps_menu_sort).setVisible(dirty);
	}

	public void startCountDownTimer(final View view, final int position){
		new CountDownTimer(2*60*60*1000,1000){

			@Override
			public void onTick(long millisUntilFinished) {

				Log.d("Time To End:",""+millisUntilFinished);

				//display the textview and the time
				TextView timeDisplay = (TextView)view.findViewById(R.id.time_display);
				timeDisplay.setVisibility(View.VISIBLE);

				long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);

				long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)-(hours*60);

				long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)-(minutes*60);

				timeDisplay.setText("["+hours+":"+minutes+":"+seconds+" Left]");


			}

			@Override
			public void onFinish() {
				Toast.makeText(getContext(), "Timer Completed", Toast.LENGTH_SHORT).show();
				mAdapter.toggle((AppListElement) mAdapter.getItem(position));

				showToastSingle(((AppListElement) mAdapter.getItem(position)).locked, ((AppListElement)mAdapter.getItem(position)).title);

				// Update lock image
				view.findViewById(R.id.applist_item_image).setVisibility(
						((AppListElement) mAdapter.getItem(position)).locked ? View.VISIBLE : View.GONE);

                //hide the textview
                view.findViewById(R.id.time_display).setVisibility(View.GONE);

				// And the menu
				//updateMenuLayout();
			}


		}.start();

	}

}
