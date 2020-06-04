package com.gadarts.neverendingstory.http;

import com.gadarts.neverendingstory.services.http.AppRequest;
import com.gadarts.neverendingstory.services.http.HttpCallTask;
import com.gadarts.neverendingstory.services.http.OnRequestResult;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class AppRequestTest {

    private static final String URL = "http://127.0.0.1";
    private static final String REQUEST_KEY = "key";
    private static final String VALUE = "value";
    @InjectMocks
    private AppRequest request;

    @Mock
    private OnRequestResult result;

    @Before
    public void setUp() {
        request = new AppRequest(URL, HttpCallTask.RequestType.GET, result);
    }

    @Test
    public void addParameter() {
        request.addParameter(REQUEST_KEY, VALUE);
        assertEquals(request.getParameters().get(REQUEST_KEY), VALUE);
    }
}