package com.gadarts.neverendingstory.http;

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

public class HttpCallTask extends AsyncTask<String, Void, RawResponse> {
    private static final String MSG_NO_RESPONSE = "Could not get any response from server";
    private static ServerResponse noResponse = new ServerResponse(false, MSG_NO_RESPONSE);

    private final RequestType type;
    private final OnRequestResult onSuccess;
    private final String url;
    private final OnRequestResult onFailure;
    private final OkHttpClient client;
    private HashMap<String, String> parameters;

    public HttpCallTask(OkHttpClient client,
                        String url,
                        RequestType type,
                        OnRequestResult onSuccess) {
        this.client = client;
        this.url = url;
        this.type = type;
        this.onSuccess = onSuccess;
        this.onFailure = null;
    }

    public HttpCallTask(OkHttpClient client,
                        String url,
                        RequestType type,
                        RequestOnResults onResults) {
        this.client = client;
        this.url = url;
        this.type = type;
        this.onSuccess = onResults.getOnSuccess();
        this.onFailure = onResults.getOnFailure();
    }

    public void setParameters(HashMap<String, String> parameters) {
        this.parameters = parameters;
    }

    @Override
    protected RawResponse doInBackground(String... strings) {
        Request request = createRequest();
        RawResponse result = null;
        try (Response response = client.newCall(request).execute()) {
            String body = Objects.requireNonNull(response.body()).string();
            result = new RawResponse(body, response.code());
        } catch (IOException e) {
            onFailure.run(noResponse);
            e.printStackTrace();
        }
        return result;
    }

    @NotNull
    private Request createRequest() {
        Request request;
        if (type == RequestType.GET) request = new Request.Builder().get().url(url).build();
        else {
            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            parameters.entrySet().forEach(p -> builder.addFormDataPart(p.getKey(), p.getValue()));
            request = new Request.Builder().post(builder.build()).url(url).build();
        }
        return request;
    }

    @Override
    protected void onPostExecute(RawResponse response) {
        super.onPostExecute(response);
        Optional<RawResponse> optional = Optional.ofNullable(response);
        if (optional.isPresent()) {
            try {
                int httpCode = response.getHttpCode();
                String body = response.getBody();
                ServerResponse inflatedResponse = new ServerResponse(body, httpCode);
                if (inflatedResponse.isSuccess()) onSuccess.run(inflatedResponse);
                else onFailure.run(inflatedResponse);
            } catch (ServerResponse.ResponseInflationFailureException e) {
                e.printStackTrace();
                Optional.ofNullable(onFailure).ifPresent(runnable -> runnable.run(noResponse));
            }
        } else Optional.ofNullable(onFailure).ifPresent(runnable -> runnable.run(noResponse));
    }


    public enum RequestType {GET, POST}
}
