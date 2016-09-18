package vello.test.nzweatherapp.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by V on 17/09/2016.
 */

public class Coord extends RealmObject {

    private Double lon;
    private Double lat;
    @PrimaryKey
    private String _primaryKey;

    public String get_primaryKey() {
        return _primaryKey;
    }

    public void set_primaryKey(String _primaryKey) {
        this._primaryKey = _primaryKey;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }
}
