package com.gadarts.neverendingstory.http;

import java.util.HashMap;

import lombok.Getter;

/**
 * Represents a plain request to the server, with URL, type and callbacks on result.
 */
@Getter
public class AppRequest {
    private final String url;
    private final HttpCallTask.RequestType type;
    private final OnResults onResults;
    private HashMap<String, Object> parameters = new HashMap<>();

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

    public void setParameters(HashMap<String, Object> parameters) {
        this.parameters = parameters;
    }

    public void addParameter(String keyRequestId, Object value) {
        parameters.put(keyRequestId, value);
    }
}
