package com.gadarts.neverendingstory.http;

import android.widget.Toast;

import java.util.Optional;

public class OnResults {
    private static final OnRequestResult defaultOnFailure = (response, c) -> Toast.makeText(
            c,
            String.format("Failed: %d", response.getCode()),
            Toast.LENGTH_LONG).show();
    private final OnRequestResult onSuccess;
    private final OnRequestResult onFailure;

    public OnResults(OnRequestResult onSuccess, OnRequestResult onFailure) {
        this.onSuccess = onSuccess;
        if (Optional.ofNullable(onFailure).isPresent()) {
            this.onFailure = onFailure;
        } else {
            this.onFailure = defaultOnFailure;
        }
    }

    public OnRequestResult getOnSuccess() {
        return onSuccess;
    }

    public OnRequestResult getOnFailure() {
        return onFailure;
    }
}
