package com.gadarts.neverendingstory;

class RawResponse {
    private final String body;
    private final int httpCode;

    public RawResponse(String body, int httpCode) {
        this.body = body;
        this.httpCode = httpCode;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public String getBody() {
        return body;
    }
}
