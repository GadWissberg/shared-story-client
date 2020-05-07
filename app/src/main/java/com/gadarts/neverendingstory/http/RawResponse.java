package com.gadarts.neverendingstory.http;

import lombok.Getter;

@Getter
class RawResponse {
    private final String body;
    private final int httpCode;

    public RawResponse(String body, int httpCode) {
        this.body = body;
        this.httpCode = httpCode;
    }

}
