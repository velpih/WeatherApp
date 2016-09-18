package vello.test.nzweatherapp.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import vello.test.nzweatherapp.model.WeatherDataArray;

/**
 * Created by V on 17/09/2016.
 */

public interface EndPointService {

    @GET("group")
    Call<WeatherDataArray> getWeather(@Query("id") String cityIds, @Query("units") String units, @Query("appid") String appId);

}
