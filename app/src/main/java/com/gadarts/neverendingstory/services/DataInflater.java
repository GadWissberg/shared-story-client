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
import java.util.Optional;

public class DataInflater {
    public static final String KEY_TITLE = "title";
    public static final String KEY_OWNER = "owner";
    public static final String KEY_NAME = "name";
    public static final String KEY_ID = "id";
    public static final String KEY_PARAGRAPHS = "paragraphs";
    public static final String KEY_PARAGRAPHS_SUGGESTIONS = "suggestions";
    private static final String KEY_STORY_ID = "story_id";
    private static final String KEY_OWNER_ID = "owner_id";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_FIRST_PARAGRAPH = "first_paragraph";

    private final Map<Long, Story> cachedStories = new HashMap<>();
    private final Map<Long, User> cachedUsers = new HashMap<>();
    private final Map<Long, Paragraph> cachedParagraphs = new HashMap<>();

    public Story inflateStory(long storyId, @NotNull JsonObject jsonObject) {
        if (cachedStories.containsKey(storyId)) return cachedStories.get(storyId);
        return createStory(storyId, jsonObject);
    }

    @NotNull
    private Story createStory(long storyId, @NotNull JsonObject jsonObject) {
        User user = inflateUser(jsonObject.get(KEY_OWNER).getAsJsonObject());
        Story story = new Story(storyId, jsonObject.get(KEY_TITLE).getAsString(), user);
        if (jsonObject.has(KEY_PARAGRAPHS)) {
            inflateAllParagraphs(jsonObject, story);
        } else if (jsonObject.has(KEY_FIRST_PARAGRAPH)) {
            inflateFirstParagraph(jsonObject, story);
        }
        cachedStories.put(storyId, story);
        return story;
    }

    private void inflateFirstParagraph(@NotNull JsonObject jsonObject, Story story) {
        Optional.ofNullable(story.getParagraphs()).ifPresent(paragraphs -> {
            paragraphs.clear();
            paragraphs.add(inflateParagraph(jsonObject.get(KEY_FIRST_PARAGRAPH).getAsJsonObject()));
        });
    }

    private void inflateAllParagraphs(JsonObject jsonObject, Story story) {
        JsonArray paragraphsJsonArray = jsonObject.get(KEY_PARAGRAPHS).getAsJsonArray();
        List<Paragraph> paragraphs = story.getParagraphs();
        paragraphs.clear();
        paragraphs.addAll(inflateParagraphs(paragraphsJsonArray));
        JsonArray suggestionsJsonArray = jsonObject.get(KEY_PARAGRAPHS_SUGGESTIONS).getAsJsonArray();
        List<Paragraph> suggestions = story.getSuggestions();
        suggestions.clear();
        suggestions.addAll(inflateParagraphs(suggestionsJsonArray));
    }

    private List<Paragraph> inflateParagraphs(JsonArray paragraphsJsonArray) {
        ArrayList<Paragraph> result = new ArrayList<>();
        paragraphsJsonArray.forEach(json -> result.add(inflateParagraph(json.getAsJsonObject())));
        return result;
    }

    public Paragraph inflateParagraph(JsonObject paragraphJsonObject) {
        long pId = paragraphJsonObject.get(KEY_ID).getAsLong();
        boolean contains = cachedParagraphs.containsKey(pId);
        return contains ? cachedParagraphs.get(pId) : createParagraph(paragraphJsonObject);
    }

    @NotNull
    private Paragraph createParagraph(JsonObject asJsonObject) {
        long pId = asJsonObject.get(KEY_ID).getAsLong();
        Paragraph paragraph;
        long storyId = asJsonObject.get(KEY_STORY_ID).getAsLong();
        long ownerId = asJsonObject.get(KEY_OWNER_ID).getAsLong();
        String content = asJsonObject.get(KEY_CONTENT).getAsString();
        paragraph = new Paragraph(pId, storyId, ownerId, content);
        return paragraph;
    }

    public User inflateUser(JsonObject ownerJsonObject) {
        User user;
        long userId = ownerJsonObject.get(KEY_ID).getAsLong();
        if (cachedUsers.containsKey(userId)) {
            user = cachedUsers.get(userId);
        } else {
            user = new User(userId, ownerJsonObject.get(KEY_NAME).getAsString());
        }
        return user;
    }
}
