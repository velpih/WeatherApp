package vello.test.nzweatherapp.model;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;

/**
 * Created by V on 17/09/2016.
 */

public class WeatherDataArray {
    private int cnt;
    private RealmList<WeatherData> list = new RealmList<>();

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    public RealmList<WeatherData> getWeather() {
        return list;
    }

    public void setWeather(RealmList<WeatherData> list) {
        this.list = list;
    }
}
