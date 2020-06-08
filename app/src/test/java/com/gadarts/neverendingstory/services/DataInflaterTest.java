package com.gadarts.neverendingstory.services;

import com.gadarts.neverendingstory.models.Story;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.gadarts.neverendingstory.services.DataInflater.KEY_ID;
import static com.gadarts.neverendingstory.services.DataInflater.KEY_OWNER;
import static com.gadarts.neverendingstory.services.DataInflater.KEY_OWNER_NAME;
import static com.gadarts.neverendingstory.services.DataInflater.KEY_PARAGRAPHS;
import static com.gadarts.neverendingstory.services.DataInflater.KEY_PARAGRAPHS_SUGGESTIONS;
import static com.gadarts.neverendingstory.services.DataInflater.KEY_TITLE;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DataInflaterTest {

    private static final long STORY_ID_0 = 0;
    private static final long STORY_ID_1 = 1;
    private static final String OWNER_NAME = "mona";
    private static final long OWNER_ID = 0;
    private static final String STORY_TITLE = "title";

    @InjectMocks
    private DataInflater dataInflater;

    @Mock
    private JsonObject storyJsonObject;

    @Mock
    private JsonObject ownerJsonObject;

    @Before
    public void setUp() {
        dataInflater = new DataInflater();
    }

    @Test(expected = NullPointerException.class)
    public void inflateStory() {
        when(storyJsonObject.get(KEY_OWNER)).thenReturn(ownerJsonObject);
        when(ownerJsonObject.getAsJsonObject()).thenReturn(ownerJsonObject);
        when(storyJsonObject.get(KEY_PARAGRAPHS)).thenReturn(new JsonArray());
        when(storyJsonObject.get(KEY_PARAGRAPHS_SUGGESTIONS)).thenReturn(new JsonArray());
        when(storyJsonObject.get(KEY_TITLE)).thenReturn(new JsonPrimitive(STORY_TITLE));
        when(ownerJsonObject.get(KEY_OWNER_NAME)).thenReturn(new JsonPrimitive(OWNER_NAME));
        when(ownerJsonObject.get(KEY_ID)).thenReturn(new JsonPrimitive(OWNER_ID));
        dataInflater.inflateUser(OWNER_ID);

        Story story_1 = dataInflater.inflateStory(STORY_ID_0, storyJsonObject);
        Story story_2 = dataInflater.inflateStory(STORY_ID_0, storyJsonObject);
        assertEquals(story_1, story_2);

        //noinspection ConstantConditions
        dataInflater.inflateStory(STORY_ID_1, null);
    }
}