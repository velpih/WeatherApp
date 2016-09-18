package vello.test.nzweatherapp.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by V on 17/09/2016.
 */

public class Rain extends RealmObject {

    private Integer _3h;
    @PrimaryKey
    private String _primaryKey;

    public String get_primaryKey() {
        return _primaryKey;
    }

    public void set_primaryKey(String _primaryKey) {
        this._primaryKey = _primaryKey;
    }

    public Integer get_3h() {
        return _3h;
    }

    public void set_3h(Integer _3h) {
        this._3h = _3h;
    }
}
