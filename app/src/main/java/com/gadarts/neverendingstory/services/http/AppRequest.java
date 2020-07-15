package com.gadarts.neverendingstory.services.http;

import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a plain request to the server, with URL, type and callbacks on result.
 */
@Getter
public class AppRequest {
    private final String url;
    private final HttpCallTask.RequestType type;
    private final OnResults onResults;

    @Setter
    private HashMap<String, Object> parameters = new HashMap<>();

    public AppRequest(final String url,
                      final HttpCallTask.RequestType type,
                      final OnRequestResult onSuccess) {
        this(url, type, new OnResults(onSuccess, null));
    }

    /**
     * @param url       Target address.
     * @param type      HTTP request type.
     * @param onResults Callbacks to be processed once response is received.
     */
    public AppRequest(final String url,
                      final HttpCallTask.RequestType type,
                      final OnResults onResults) {
        this.url = url;
        this.type = type;
        this.onResults = onResults;
    }

    /**
     * Add a new parameter. Overrides existing parameter with same key.
     *
     * @param key
     * @param value
     */
    public void addParameter(final String key, final Object value) {
        parameters.put(key, value);
    }
}
