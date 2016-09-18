package vello.test.nzweatherapp.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by V on 17/09/2016.
 */

public class Wind extends RealmObject {

    private Double speed;
    private Double deg;
    @PrimaryKey
    private String _primaryKey;

    public String get_primaryKey() {
        return _primaryKey;
    }

    public void set_primaryKey(String _primaryKey) {
        this._primaryKey = _primaryKey;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Double getDeg() {
        return deg;
    }

    public void setDeg(Double deg) {
        this.deg = deg;
    }
}
