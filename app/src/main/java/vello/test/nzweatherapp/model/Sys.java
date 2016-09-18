package vello.test.nzweatherapp.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by V on 17/09/2016.
 */

public class Sys extends RealmObject {

    private Integer type;
    private Integer id;
    private Double message;
    private String country;
    private Long sunrise;
    private Long sunset;
    @PrimaryKey
    private String _primaryKey;

    public String get_primaryKey() {
        return _primaryKey;
    }

    public void set_primaryKey(String _primaryKey) {
        this._primaryKey = _primaryKey;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getMessage() {
        return message;
    }

    public void setMessage(Double message) {
        this.message = message;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Long getSunrise() {
        return sunrise * 1000L;
    }

    public void setSunrise(Long sunrise) {
        this.sunrise = sunrise;
    }

    public Long getSunset() {
        return sunset * 1000L;
    }

    public void setSunset(Long sunset) {
        this.sunset = sunset;
    }
}
