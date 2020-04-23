package com.gadarts.neverendingstory.http;

/**
 * Represents a plain request to the server, with URL, type and callbacks on result.
 */
public class AppRequest {
    private final String url;
    private final HttpCallTask.RequestType type;
    private final OnResults onResults;

    public AppRequest(String url, HttpCallTask.RequestType type, OnRequestResult onSuccess) {
        this(url, type, new OnResults(onSuccess, null));
    }

    /**
     * @param url       Target address.
     * @param type      HTTP request type.
     * @param onResults Callbacks to be processed once response is received.
     */
    public AppRequest(String url, HttpCallTask.RequestType type, OnResults onResults) {
        this.url = url;
        this.type = type;
        this.onResults = onResults;
    }

    OnResults getOnResults() {
        return onResults;
    }

    HttpCallTask.RequestType getType() {
        return type;
    }

    String getUrl() {
        return url;
    }
}
