package vello.test.nzweatherapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.Toast;

import vello.test.nzweatherapp.app.WeatherApp;
import vello.test.nzweatherapp.network.PullDataService;

/**
 * An activity representing a single Weather detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link WeatherListActivity}.
 */
public class WeatherDetailActivity extends AppCompatActivity {
    private SwipeRefreshLayout swipeContainer;
    static IntentFilter intentFilter;
    static ResponseReceiver responseReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getResources().getBoolean(R.bool.portrait_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        setContentView(R.layout.activity_weather_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        String title = getIntent().getStringExtra(WeatherDetailFragment.ARG_ITEM_TITLE);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                WeatherApp.getInstance().downloadData(true);
            }
        });

        intentFilter = new IntentFilter(PullDataService.BROADCAST_COMPLETED);
        responseReceiver = new ResponseReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(responseReceiver, intentFilter);

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(WeatherDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(WeatherDetailFragment.ARG_ITEM_ID));
            WeatherDetailFragment fragment = new WeatherDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.weather_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        WeatherApp.getInstance().hasReloadTimeoutExpired();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, WeatherListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (responseReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(responseReceiver);
            responseReceiver = null;
        }
    }

    private class ResponseReceiver extends BroadcastReceiver {

        private ResponseReceiver() {}

        @Override
        public void onReceive(Context context, Intent intent) {
            swipeContainer.setRefreshing(false);
        }
    }
}
