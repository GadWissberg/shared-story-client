package com.gadarts.neverendingstory.services.http;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import lombok.Getter;

@Getter
public class ServerResponse {
    private final static Gson gson = new Gson();
    private static final String PAR_SUCCESS = "success";
    private static final String MESSAGE_INFLATION_FAILURE = "Failed to inflate response!";
    private static final String MSG_PAR_MISSING = MESSAGE_INFLATION_FAILURE
            + " Mandatory parameter missing: %s";
    private static final String PAR_MESSAGE = "message";
    private static final String PAR_DATA = "data";
    private static final int INVALID_RESPONSE = -1;

    private boolean success;
    private String message;
    private JsonObject data;
    private int code;

    ServerResponse(final boolean success, final String message) {
        this.success = success;
        this.message = message;
        this.data = null;
    }

    ServerResponse(final String responseString,
                   final int httpCode) throws ResponseInflationFailureException {
        try {
            initializeResponse(responseString, httpCode);
        } catch (final JsonSyntaxException e) {
            e.printStackTrace();
            code = INVALID_RESPONSE;
        }
    }

    private void initializeResponse(final String responseString,
                                    final int httpCode) throws ResponseInflationFailureException {
        JsonObject map = gson.fromJson(responseString, JsonObject.class);
        this.code = httpCode;
        this.message = map.has(PAR_MESSAGE) ? map.get(PAR_MESSAGE).getAsString() : null;
        this.data = map.has(PAR_DATA) ? map.get(PAR_DATA).getAsJsonObject() : null;
        if (map.has(PAR_SUCCESS)) {
            success = map.get(PAR_SUCCESS).getAsBoolean();
        } else {
            throw new ResponseInflationFailureException(String.format(MSG_PAR_MISSING, PAR_SUCCESS));
        }
    }

    static class ResponseInflationFailureException extends Throwable {
        ResponseInflationFailureException(final String message) {
            super(message);
        }
    }
}
