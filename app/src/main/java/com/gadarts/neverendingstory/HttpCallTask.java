package com.gadarts.neverendingstory;

import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpCallTask extends AsyncTask<String, Void, String> {
    private final OkHttpClient client;
    private final OnSuccessRequest onSuccess;
    private final Gson gson;
    private final String url;

    public HttpCallTask(OkHttpClient client, Gson gson, String url, OnSuccessRequest onSuccess) {
        this.client = client;
        this.gson = gson;
        this.url = url;
        this.onSuccess = onSuccess;
    }

    @Override
    protected String doInBackground(String... strings) {
        Request request = new Request.Builder().url(url).build();
        String result = null;
        try (Response response = client.newCall(request).execute()) {
            result = Objects.requireNonNull(response.body()).string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
        Optional.ofNullable(response).ifPresent(json -> onSuccess.run(response, gson));
    }
}
