package com.gadarts.neverendingstory;

import android.app.Application;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.gadarts.neverendingstory.services.DataInflater;

import lombok.Getter;
import okhttp3.OkHttpClient;

@Getter
public class OurTaleApplication extends Application {
    public static final String HOST = "http://GAD-LT-PRS:5000/";
    private OkHttpClient client = new OkHttpClient();
    private DataInflater dataInflater = new DataInflater();

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPrefsCookiePersistor persistor = new SharedPrefsCookiePersistor(getApplicationContext());
        ClearableCookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(), persistor);
        client = new OkHttpClient.Builder().cookieJar(cookieJar).build();
    }

}
