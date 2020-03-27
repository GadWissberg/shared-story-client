package com.gadarts.neverendingstory;

import android.os.AsyncTask;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpCallTask extends AsyncTask<String, Void, String> {
    private static final OkHttpClient client = new OkHttpClient();
    private final RequestTypes type;
    private final OnRequestResult onSuccess;
    private final String url;
    private final OnRequestResult onFailure;
    private HashMap<String, String> parameters;

    public HttpCallTask(String url, RequestTypes type, OnRequestResult onSuccess) {
        this(url, type, onSuccess, null);
    }

    public HttpCallTask(String url,
                        RequestTypes type,
                        OnRequestResult onSuccess,
                        OnRequestResult onFailure) {
        this.url = url;
        this.type = type;
        this.onSuccess = onSuccess;
        this.onFailure = onFailure;
    }

    public void setParameters(HashMap<String, String> parameters) {
        this.parameters = parameters;
    }

    @Override
    protected String doInBackground(String... strings) {
        Request request = createRequest();
        String result = null;
        try (Response response = client.newCall(request).execute()) {
            result = Objects.requireNonNull(response.body()).string();
        } catch (IOException e) {
            onFailure.run(null);
            e.printStackTrace();
        }
        return result;
    }

    @NotNull
    private Request createRequest() {
        Request request;
        if (type == RequestTypes.GET) request = new Request.Builder().get().url(url).build();
        else {
            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            parameters.entrySet().forEach(p -> builder.addFormDataPart(p.getKey(), p.getValue()));
            request = new Request.Builder().post(builder.build()).url(url).build();
        }
        return request;
    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
        Optional<String> optional = Optional.ofNullable(response);
        if (optional.isPresent()) {
            try {
                ServerResponse inflatedResponse = new ServerResponse(response);
                if (inflatedResponse.isSuccess()) {
                    onSuccess.run(inflatedResponse);
                } else {
                    onFailure.run(inflatedResponse);
                }
            } catch (ServerResponse.ResponseInflationFailureException e) {
                e.printStackTrace();
                Optional.ofNullable(onFailure).ifPresent(runnable -> runnable.run(null));
            }
        } else Optional.ofNullable(onFailure).ifPresent(runnable -> runnable.run(null));
    }


    public enum RequestTypes {GET, POST}
}
