package com.gadarts.neverendingstory;

import com.google.gson.Gson;

public interface OnRequestResult {
    void run(String response, Gson gson);
}
