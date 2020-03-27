package com.gadarts.neverendingstory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class ServerResponse {
    private final static Gson gson = new Gson();
    private static final String PARAMETER_SUCCESS = "success";
    private static final String MESSAGE_INFLATION_FAILURE = "Failed to inflate response!";
    private static final String MESSAGE_PARAMETER_MISSING = MESSAGE_INFLATION_FAILURE
            + " Mandatory parameter missing: %s";
    private static final String PARAMETER_MESSAGE = "message";

    private final boolean success;
    private final String message;

    ServerResponse(String responseString) throws ResponseInflationFailureException {
        JsonObject map = gson.fromJson(responseString, JsonObject.class);
        if (map.has(PARAMETER_SUCCESS)) {
            success = map.get(PARAMETER_SUCCESS).getAsInt() == 1;
        } else {
            throw new ResponseInflationFailureException(MESSAGE_PARAMETER_MISSING);
        }
        message = map.has(PARAMETER_MESSAGE) ? map.get(PARAMETER_MESSAGE).getAsString() : null;
    }

    boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    static class ResponseInflationFailureException extends Throwable {
        ResponseInflationFailureException(String message) {
            super(message);
        }
    }
}
