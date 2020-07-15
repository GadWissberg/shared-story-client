package com.gadarts.neverendingstory.services.http;

import android.content.Context;
import android.os.AsyncTask;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import okhttp3.HttpUrl;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpCallTask extends AsyncTask<String, Void, RawResponse> {
    private static final String MSG_NO_RESPONSE = "Could not get any response from server";
    private static final ServerResponse noResponse = new ServerResponse(false, MSG_NO_RESPONSE);

    private final OkHttpClient client;
    private final AppRequest appRequest;
    private final Context context;

    public HttpCallTask(final OkHttpClient client,
                        final AppRequest appRequest,
                        final Context context) {
        this.client = client;
        this.appRequest = appRequest;
        this.context = context;
    }

    @Override
    protected RawResponse doInBackground(final String... strings) {
        Request request = createRequest();
        RawResponse result = null;
        try (Response response = client.newCall(request).execute()) {
            String body = Objects.requireNonNull(response.body()).string();
            result = new RawResponse(body, response.code());
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @NotNull
    private Request createRequest() {
        Request httpRequest;
        String url = appRequest.getUrl();
        HttpUrl.Builder httpBuilder;
        if (appRequest.getType() == RequestType.GET) {
            httpBuilder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();
            Set<Map.Entry<String, Object>> entries = appRequest.getParameters().entrySet();
            entries.forEach(p -> httpBuilder.addQueryParameter(p.getKey(), String.valueOf(p.getValue())));
            httpRequest = new Request.Builder().url(httpBuilder.build()).build();
        } else if (appRequest.getType() == RequestType.POST) {
            httpRequest = createPostRequest(url);
        } else {
            httpRequest = createPutRequest(url);
        }
        return httpRequest;
    }

    @NotNull
    private Request createPostRequest(final String url) {
        Request httpRequest;
        Set<Map.Entry<String, Object>> entries = appRequest.getParameters().entrySet();
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        entries.forEach(p -> builder.addFormDataPart(p.getKey(), String.valueOf(p.getValue())));
        httpRequest = new Request.Builder().post(builder.build()).url(url).build();
        return Objects.requireNonNull(httpRequest);
    }

    @NotNull
    private Request createPutRequest(final String url) {
        Request httpRequest;
        Set<Map.Entry<String, Object>> entries = appRequest.getParameters().entrySet();
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        entries.forEach(p -> builder.addFormDataPart(p.getKey(), String.valueOf(p.getValue())));
        httpRequest = new Request.Builder().put(builder.build()).url(url).build();
        return Objects.requireNonNull(httpRequest);
    }

    @Override
    protected void onPostExecute(final RawResponse response) {
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
            } catch (final ServerResponse.ResponseInflationFailureException e) {
                e.printStackTrace();
                onFailure.run(noResponse, context);
            }
        } else onFailure.run(noResponse, context);
    }


    public enum RequestType {GET, POST, PUT}
}
