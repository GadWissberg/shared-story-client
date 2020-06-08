package com.gadarts.neverendingstory.services;

import com.gadarts.neverendingstory.models.Paragraph;
import com.gadarts.neverendingstory.models.Story;
import com.gadarts.neverendingstory.models.User;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataInflater {
    public static final String KEY_TITLE = "title";
    public static final String KEY_OWNER = "owner";
    public static final String KEY_OWNER_NAME = "name";
    public static final String KEY_ID = "id";
    public static final String KEY_PARAGRAPHS = "paragraphs";
    public static final String KEY_PARAGRAPHS_SUGGESTIONS = "suggestions";
    private static final String KEY_STORY_ID = "story_id";
    private static final String KEY_OWNER_ID = "owner_id";
    private static final String KEY_CONTENT = "content";

    private final Map<Long, Story> cachedStories = new HashMap<>();
    private final Map<Long, User> cachedUsers = new HashMap<>();
    private final Map<Long, Paragraph> cachedParagraphs = new HashMap<>();

    public Story inflateStory(long storyId, @NotNull JsonObject jsonObject) {
        if (cachedStories.containsKey(storyId)) return cachedStories.get(storyId);
        JsonObject ownerJsonObject = jsonObject.get(KEY_OWNER).getAsJsonObject();
        String userName = ownerJsonObject.get(KEY_OWNER_NAME).getAsString();
        User user = inflateUser(ownerJsonObject.get(KEY_ID).getAsLong(), userName);
        Story story = new Story(storyId, jsonObject.get(KEY_TITLE).getAsString(), user);
        inflateAllParagraphs(jsonObject, story);
        cachedStories.put(storyId, story);
        return story;
    }

    private void inflateAllParagraphs(JsonObject jsonObject, Story story) {
        JsonArray paragraphsJsonArray = jsonObject.get(KEY_PARAGRAPHS).getAsJsonArray();
        story.setParagraphs(inflateParagraphs(paragraphsJsonArray));
        JsonArray suggestionsJsonArray = jsonObject.get(KEY_PARAGRAPHS_SUGGESTIONS).getAsJsonArray();
        story.setSuggestions(inflateParagraphs(suggestionsJsonArray));
    }

    private List<Paragraph> inflateParagraphs(JsonArray paragraphsJsonArray) {
        ArrayList<Paragraph> result = new ArrayList<>();
        paragraphsJsonArray.forEach(paragraphJsonElement -> {
            JsonObject asJsonObject = paragraphJsonElement.getAsJsonObject();
            long paragraphId = asJsonObject.get(KEY_ID).getAsLong();
            Paragraph paragraph = cachedParagraphs.containsKey(paragraphId) ?
                    cachedParagraphs.get(paragraphId) : createParagraph(asJsonObject, paragraphId);
            result.add(paragraph);
        });
        return result;
    }

    @NotNull
    private Paragraph createParagraph(JsonObject asJsonObject, long paragraphId) {
        Paragraph paragraph;
        long storyId = asJsonObject.get(KEY_STORY_ID).getAsLong();
        long ownerId = asJsonObject.get(KEY_OWNER_ID).getAsLong();
        String content = asJsonObject.get(KEY_CONTENT).getAsString();
        paragraph = new Paragraph(paragraphId, storyId, ownerId, content);
        return paragraph;
    }

    public User inflateUser(long userId) {
        return inflateUser(userId, null);
    }

    public User inflateUser(long userId, String userName) {
        User user;
        if (cachedUsers.containsKey(userId)) {
            user = cachedUsers.get(userId);
        } else {
            user = new User(userId, userName);
        }
        return user;
    }
}
