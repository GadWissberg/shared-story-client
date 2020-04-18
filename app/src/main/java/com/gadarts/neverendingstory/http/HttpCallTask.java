package com.gadarts.neverendingstory.http;

import android.content.Context;
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

    private final OkHttpClient client;
    private final AppRequest appRequest;
    private final Context context;
    private HashMap<String, String> parameters;

    public HttpCallTask(OkHttpClient client, AppRequest appRequest, Context context) {
        this.client = client;
        this.appRequest = appRequest;
        this.context = context;
    }

    public HttpCallTask setParameters(HashMap<String, String> parameters) {
        this.parameters = parameters;
        return this;
    }

    @Override
    protected RawResponse doInBackground(String... strings) {
        Request request = createRequest();
        RawResponse result = null;
        try (Response response = client.newCall(request).execute()) {
            String body = Objects.requireNonNull(response.body()).string();
            result = new RawResponse(body, response.code());
        } catch (IOException e) {
            appRequest.getOnResults().getOnFailure().run(noResponse, context);
            e.printStackTrace();
        }
        return result;
    }

    @NotNull
    private Request createRequest() {
        Request httpRequest;
        String url = appRequest.getUrl();
        if (appRequest.getType() == RequestType.GET)
            httpRequest = new Request.Builder().get().url(url).build();
        else {
            httpRequest = createPostRequest(url);
        }
        return httpRequest;
    }

    @NotNull
    private Request createPostRequest(String url) {
        Request httpRequest;
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        parameters.entrySet().forEach(p -> builder.addFormDataPart(p.getKey(), p.getValue()));
        httpRequest = new Request.Builder().post(builder.build()).url(url).build();
        return httpRequest;
    }

    @Override
    protected void onPostExecute(RawResponse response) {
        super.onPostExecute(response);
        Optional<RawResponse> optional = Optional.ofNullable(response);
        OnRequestResult onFailure = appRequest.getOnResults().getOnFailure();
        if (optional.isPresent()) {
            try {
                int httpCode = response.getHttpCode();
                String body = response.getBody();
                ServerResponse inflatedResponse = new ServerResponse(body, httpCode);
                if (inflatedResponse.isSuccess())
                    appRequest.getOnResults().getOnSuccess().run(inflatedResponse, context);
                else onFailure.run(inflatedResponse, context);
            } catch (ServerResponse.ResponseInflationFailureException e) {
                e.printStackTrace();
                onFailure.run(noResponse, context);
            }
        } else onFailure.run(noResponse, context);
    }


    public enum RequestType {GET, POST}
}
