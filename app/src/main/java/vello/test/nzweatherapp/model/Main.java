package vello.test.nzweatherapp.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by V on 17/09/2016.
 */

public class Main extends RealmObject {

    private Double temp;
    private Double pressure;
    private Double humidity;
    private Double temp_min;
    private Double temp_max;
    @PrimaryKey
    private String _primaryKey;

    public String get_primaryKey() {
        return _primaryKey;
    }

    public void set_primaryKey(String _primaryKey) {
        this._primaryKey = _primaryKey;
    }

    public Double getTemp() {
        return temp;
    }

    public void setTemp(Double temp) {
        this.temp = temp;
    }

    public Double getPressure() {
        return pressure;
    }

    public void setPressure(Double pressure) {
        this.pressure = pressure;
    }

    public Double getHumidity() {
        return humidity;
    }

    public void setHumidity(Double humidity) {
        this.humidity = humidity;
    }

    public Double getTempMin() {
        return temp_min;
    }

    public void setTempMin(Double tempMin) {
        this.temp_min = tempMin;
    }

    public Double getTempMax() {
        return temp_max;
    }

    public void setTempMax(Double tempMax) {
        this.temp_max = tempMax;
    }
}
