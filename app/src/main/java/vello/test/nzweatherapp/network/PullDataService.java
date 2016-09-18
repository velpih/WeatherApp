package vello.test.nzweatherapp.network;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.joda.time.DateTime;

import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Response;
import vello.test.nzweatherapp.R;
import vello.test.nzweatherapp.app.WeatherApp;
import vello.test.nzweatherapp.model.Weather;
import vello.test.nzweatherapp.model.WeatherData;
import vello.test.nzweatherapp.model.WeatherDataArray;
import vello.test.nzweatherapp.utils.Tools;

/**
 * Created by V on 17/09/2016.
 */

public class PullDataService extends IntentService {
    private static final String TAG = "PullDataService";
    public static final String BROADCAST_COMPLETED = "broadcast_completed";

    public PullDataService() {
        super(TAG);
    }

    public PullDataService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        RealmResults<WeatherData> _weatherData = WeatherApp.getInstance().getRealm().where(WeatherData.class).findAll();

        String queryUnits = getResources().getString(R.string.query_units);
        String queryAppId = getResources().getString(R.string.query_app_id);
        String cityIdList;

        // Reuse city id codes from cache instead of using default ones.
        if (_weatherData.size() > 0) {
            int size = _weatherData.size();
            String[] idArray = new String[size];
            for (int i = 0 ; i < size; i++) {
                idArray[i] = "" + _weatherData.get(i).getId();
            }
            cityIdList = TextUtils.join(",", idArray);
        }
        else {
            cityIdList = getResources().getString(R.string.query_city_ids);
        }

        Call<WeatherDataArray> call = null;
        call = WeatherApp.getInstance().getEndPointService().getWeather(cityIdList, queryUnits, queryAppId);

        Log.d(TAG, "Call getWeather()");
        call.enqueue(new CancelableCallback<WeatherDataArray>() {
            @Override
            public void onResponse(Response<WeatherDataArray> response) {
                sendIntentCompleted();
                if (response.isSuccessful()) {
                    Log.d(TAG, "Callback successful");
                    updateRealmData(response);
                    Toast.makeText(getBaseContext(), R.string.data_refreshed, Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.d(TAG, "Callback failed");
                    Toast.makeText(getBaseContext(), R.string.transfer_failed, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                sendIntentCompleted();
                Log.d(TAG, "Callback failed, " + t.getMessage());
                if (!Tools.isNetworkAvailable(getBaseContext())) {
                }
                else if (t.getMessage().equals("timeout"))
                    Toast.makeText(getBaseContext(), R.string.request_timeout, Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getBaseContext(), R.string.transfer_failed, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendIntentCompleted() {
        Intent localIntent = new Intent(BROADCAST_COMPLETED);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    private void updateRealmData(Response<WeatherDataArray> response) {
        long millisNow = new DateTime().getMillis();
        WeatherApp.getInstance().getSharedPreferences().edit().putLong("LAST_RETRIEVED_MILLIS", millisNow).apply();

        // Set primary key for sub data objects
        for (WeatherData weatherData : response.body().getWeather()) {
            for (Weather weather : weatherData.getWeather()) {
                weather.set_primaryKey("" + weatherData.getId() + weather.getId());
            }
            if (weatherData.getClouds() != null)
                weatherData.getClouds().set_primaryKey("" + weatherData.getId());
            if (weatherData.getCoord() != null)
                weatherData.getCoord().set_primaryKey("" + weatherData.getId());
            if (weatherData.getMain() != null)
                weatherData.getMain().set_primaryKey("" + weatherData.getId());
            if (weatherData.getRain() != null)
                weatherData.getRain().set_primaryKey("" + weatherData.getId());
            if (weatherData.getSys() != null)
                weatherData.getSys().set_primaryKey("" + weatherData.getId());
            if (weatherData.getWind() != null)
                weatherData.getWind().set_primaryKey("" + weatherData.getId());
        }

        WeatherApp.getInstance().getRealm().beginTransaction();
        WeatherApp.getInstance().getRealm().copyToRealmOrUpdate(response.body().getWeather());
        WeatherApp.getInstance().getRealm().commitTransaction();
    }
}
