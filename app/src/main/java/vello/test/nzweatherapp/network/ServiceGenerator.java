package vello.test.nzweatherapp.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.danlew.android.joda.JodaTimeAndroid;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import vello.test.nzweatherapp.BuildConfig;
import vello.test.nzweatherapp.R;
import vello.test.nzweatherapp.app.WeatherApp;

/**
 * Created by V on 17/09/2016.
 */
public class ServiceGenerator {
    private static String TAG = "ServiceGenerator";
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    private static SharedPreferences prefs = WeatherApp.getInstance().getSharedPreferences();
    private static Gson gson = new GsonBuilder()
            .create();

    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(WeatherApp.getInstance().getBaseContext().getString(R.string.api_base_url))
            .addConverterFactory(GsonConverterFactory.create(gson));

    public <S> S createService(final Context context, final Class<S> serviceClass) {
        JodaTimeAndroid.init(context);
        if (httpClient.networkInterceptors().isEmpty()) {
            if (BuildConfig.BUILD_TYPE.equals("debug")) {
                Log.d(TAG, "Adding StehthoInterceptor to networkInterceptors");
                httpClient.addNetworkInterceptor(new StethoInterceptor());
            }
        }
        if (httpClient.interceptors().isEmpty()) {
            Log.d(TAG, "Adding Interceptor");
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    Request.Builder requestBuilder = original.newBuilder();

                    Request request = requestBuilder.build();
                    Log.d(TAG, "Performing request " + request.url().toString());
                    Response response = chain.proceed(request);
                    Log.d(TAG, "Response code:" + response.code() + " " + response.request().url().toString());

                    return response;
                }
            });
        }
        httpClient.connectTimeout(60, TimeUnit.SECONDS);
        httpClient.readTimeout(60, TimeUnit.SECONDS);
        Retrofit retrofit = builder.client(httpClient.build()).build();
        return retrofit.create(serviceClass);
    }

}
