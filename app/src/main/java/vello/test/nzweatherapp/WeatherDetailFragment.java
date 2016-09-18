package vello.test.nzweatherapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import io.realm.RealmChangeListener;
import io.realm.RealmModel;
import vello.test.nzweatherapp.app.WeatherApp;
import vello.test.nzweatherapp.model.WeatherData;

public class WeatherDetailFragment extends Fragment {

    public static DateTimeFormatter dtfTime = DateTimeFormat.forPattern("h.mmaa");
    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_ITEM_TITLE = "item_title";


    private View rootView;
    private WeatherData weatherData;

    public WeatherDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            String argItemId = getArguments().getString(ARG_ITEM_ID);
            weatherData = WeatherApp.getInstance().getRealm().where(WeatherData.class).equalTo("id", Integer.valueOf(argItemId)).findFirst();

            Activity activity = this.getActivity();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.weather_detail, container, false);
            setTextFieldData(rootView, weatherData);
        }
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        weatherData.removeChangeListeners();
        weatherData.addChangeListener(new RealmChangeListener<RealmModel>() {
            @Override
            public void onChange(RealmModel element) {
                setTextFieldData(rootView, weatherData);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        weatherData.removeChangeListeners();
    }

    private void setTextFieldData(View rootView, WeatherData data) {
        if (data != null) {
            ((TextView) rootView.findViewById(R.id.weather_city)).setText(data.getName());
            ((TextView) rootView.findViewById(R.id.weather_country)).setText(data.getSys().getCountry());
            ((TextView) rootView.findViewById(R.id.weather_degrees)).setText("" + Math.round(data.getMain().getTemp()) + "°");
            ((TextView) rootView.findViewById(R.id.weather_max_min))
                    .setText(getString(R.string.min, "" + Math.round(data.getMain().getTempMin())) + "° - " +
                            getString(R.string.max, "" + Math.round(data.getMain().getTempMax())) + "°");
            ((TextView) rootView.findViewById(R.id.weather_description)).setText(data.getWeather().get(0).getDescription());
            ((TextView) rootView.findViewById(R.id.weather_sunrise)).setText(dtfTime.print(new LocalDateTime(data.getSys().getSunrise())).toLowerCase());
            ((TextView) rootView.findViewById(R.id.weather_sunset)).setText(dtfTime.print(new LocalDateTime(data.getSys().getSunset())).toLowerCase());
            ((TextView) rootView.findViewById(R.id.weather_pressure)).setText("" + data.getMain().getPressure());
            ((TextView) rootView.findViewById(R.id.weather_humidity)).setText("" + data.getMain().getHumidity());
        }
    }
}
