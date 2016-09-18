package vello.test.nzweatherapp.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import vello.test.nzweatherapp.R;
import vello.test.nzweatherapp.network.EndPointService;
import vello.test.nzweatherapp.network.PullDataService;
import vello.test.nzweatherapp.network.ServiceGenerator;
import vello.test.nzweatherapp.utils.Tools;

/**
 * Created by V on 17/09/2016.
 */

public class WeatherApp extends Application {

    private static WeatherApp appInstance;
    private static EndPointService endPointService;
    private SharedPreferences sharedPreferences;
    private Realm realm;

    @Override
    public void onCreate() {
        super.onCreate();
        appInstance = this;
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
        sharedPreferences = getSharedPreferences("APP", Context.MODE_PRIVATE);

        // Notify user if no network connectivity when app is opened
        if (!Tools.isNetworkAvailable(getBaseContext())) {
            Toast.makeText(this, R.string.no_network, Toast.LENGTH_SHORT).show();
        }
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public EndPointService getEndPointService() {
        if (endPointService == null)
            endPointService = new ServiceGenerator().createService(this, EndPointService.class);
        return endPointService;
    }

    public Realm getRealm() {
        try {
            if (realm == null || realm.isClosed())
                realm = Realm.getDefaultInstance();
        }
        catch (IllegalStateException ex) {
            realm = Realm.getDefaultInstance();
        }
        return realm;
    }

    // Check for reload timeout
    public boolean hasReloadTimeoutExpired() {
        long lastRetrievedMillis = sharedPreferences.getLong("LAST_RETRIEVED_MILLIS", 0);
        long resultMinutes = TimeUnit.MILLISECONDS.toMinutes(new DateTime().minus(lastRetrievedMillis).getMillis());
        long reloadMinutes = getResources().getInteger(R.integer.reload_timeout_minutes);

        if (resultMinutes > reloadMinutes || resultMinutes < 0) {
            downloadData(false);
            return true;
        }
        else {
            return false;
        }
    }

    public void downloadData(boolean checkNetworkStatus) {
        if (checkNetworkStatus) {
            if (!Tools.isNetworkAvailable(getBaseContext())) {
                Toast.makeText(this, R.string.no_network, Toast.LENGTH_SHORT).show();
            }
        }
        Intent serviceIntent = new Intent(this, PullDataService.class);
        this.startService(serviceIntent);
    }

    public static WeatherApp getInstance() {
        return appInstance;
    }
}
