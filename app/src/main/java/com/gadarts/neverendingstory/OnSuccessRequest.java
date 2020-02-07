package com.gadarts.neverendingstory;

import com.google.gson.Gson;

public interface OnSuccessRequest {
    void run(String response, Gson gson);
}
