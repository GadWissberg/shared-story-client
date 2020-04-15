package com.gadarts.neverendingstory.http;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class ServerResponse {
    private final static Gson gson = new Gson();
    private static final String PAR_SUCCESS = "success";
    private static final String MESSAGE_INFLATION_FAILURE = "Failed to inflate response!";
    private static final String MSG_PAR_MISSING = MESSAGE_INFLATION_FAILURE
            + " Mandatory parameter missing: %s";
    private static final String PAR_MESSAGE = "message";
    private static final String PAR_DATA = "data";

    private final boolean success;
    private final String message;
    private final JsonObject data;
    private int code;

    ServerResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.data = null;
    }

    ServerResponse(String responseString,
                   int httpCode) throws ResponseInflationFailureException {
        JsonObject map = gson.fromJson(responseString, JsonObject.class);
        this.code = httpCode;
        this.message = map.has(PAR_MESSAGE) ? map.get(PAR_MESSAGE).getAsString() : null;
        this.data = map.has(PAR_DATA) ? map.get(PAR_DATA).getAsJsonObject() : null;
        if (map.has(PAR_SUCCESS)) success = map.get(PAR_SUCCESS).getAsBoolean();
        else
            throw new ResponseInflationFailureException(String.format(MSG_PAR_MISSING, PAR_SUCCESS));
    }

    public JsonObject getData() {
        return data;
    }

    boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }

    static class ResponseInflationFailureException extends Throwable {
        ResponseInflationFailureException(String message) {
            super(message);
        }
    }
}
