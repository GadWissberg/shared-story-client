package com.gadarts.neverendingstory;

import android.app.Application;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import okhttp3.OkHttpClient;

public class PolyTaleApplication extends Application {
    public static final String HOST = "http://192.168.1.136:5000/";
    private OkHttpClient client = new OkHttpClient();

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPrefsCookiePersistor persistor = new SharedPrefsCookiePersistor(getApplicationContext());
        ClearableCookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(), persistor);
        client = new OkHttpClient.Builder().cookieJar(cookieJar).build();
    }

    public OkHttpClient getClient() {
        return client;
    }
}
