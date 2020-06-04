package com.gadarts.neverendingstory.services.http;

import android.content.Context;

public interface OnRequestResult {
    void run(ServerResponse response, Context context);
}
