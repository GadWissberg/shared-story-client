package com.gadarts.neverendingstory.http;

public class AppRequest {
    private final String url;
    private final HttpCallTask.RequestType type;
    private final OnResults onResults;

    public AppRequest(String url, HttpCallTask.RequestType type, OnRequestResult onSuccess) {
        this(url, type, new OnResults(onSuccess, null));
    }

    public AppRequest(String url, HttpCallTask.RequestType type, OnResults onResults) {
        this.url = url;
        this.type = type;
        this.onResults = onResults;
    }

    public OnResults getOnResults() {
        return onResults;
    }

    public HttpCallTask.RequestType getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }
}
