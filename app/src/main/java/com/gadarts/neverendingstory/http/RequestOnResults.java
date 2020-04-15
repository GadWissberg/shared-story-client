package com.gadarts.neverendingstory.http;

public class RequestOnResults {
    private final OnRequestResult onSuccess;
    private final OnRequestResult onFailure;

    public RequestOnResults(OnRequestResult onSuccess, OnRequestResult onFailure) {
        this.onSuccess = onSuccess;
        this.onFailure = onFailure;
    }

    public OnRequestResult getOnSuccess() {
        return onSuccess;
    }

    public OnRequestResult getOnFailure() {
        return onFailure;
    }
}
