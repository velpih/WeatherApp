package vello.test.nzweatherapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.util.concurrent.TimeUnit;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import vello.test.nzweatherapp.app.WeatherApp;
import vello.test.nzweatherapp.model.WeatherData;
import vello.test.nzweatherapp.network.PullDataService;
import vello.test.nzweatherapp.utils.Tools;

public class WeatherListActivity extends AppCompatActivity {

    private boolean mTwoPane;
    private RealmResults<WeatherData> realmData;
    private SimpleItemRecyclerViewAdapter adapter;
    private RealmResults<WeatherData> adapterData;
    private SwipeRefreshLayout swipeContainer;
    static IntentFilter intentFilter;
    static ResponseReceiver responseReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getResources().getBoolean(R.bool.portrait_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        realmData = WeatherApp.getInstance().getRealm().where(WeatherData.class).findAll();
        adapterData = realmData;
        realmData.removeChangeListeners();
        realmData.addChangeListener(new RealmChangeListener<RealmResults<WeatherData>>() {
            @Override
            public void onChange(RealmResults<WeatherData> element) {
                adapterData = realmData;
                adapter.notifyDataSetChanged();
            }
        });

        setContentView(R.layout.activity_weather_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

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

        View recyclerView = findViewById(R.id.weather_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.weather_detail_container) != null) {
            mTwoPane = true;
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        WeatherApp.getInstance().hasReloadTimeoutExpired();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realmData.removeChangeListeners();
        if (responseReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(responseReceiver);
            responseReceiver = null;
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        adapter = new SimpleItemRecyclerViewAdapter(adapterData);
        recyclerView.setAdapter(adapter);
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private RealmResults<WeatherData> mValues;

        public SimpleItemRecyclerViewAdapter(RealmResults<WeatherData> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.weather_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mContentView.setText(mValues.get(position).getName());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(WeatherDetailFragment.ARG_ITEM_ID, holder.mItem.getId().toString());
                        WeatherDetailFragment fragment = new WeatherDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.weather_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, WeatherDetailActivity.class);
                        intent.putExtra(WeatherDetailFragment.ARG_ITEM_ID, holder.mItem.getId().toString());
                        intent.putExtra(WeatherDetailFragment.ARG_ITEM_TITLE, holder.mItem.getName().toString());

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mContentView;
            public WeatherData mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mContentView = (TextView) view.findViewById(R.id.content);
            }
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
