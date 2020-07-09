package com.gadarts.neverendingstory.services;

import com.gadarts.neverendingstory.models.Paragraph;
import com.gadarts.neverendingstory.models.Story;
import com.gadarts.neverendingstory.models.User;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

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
    private static final String KEY_PARTICIPANTS = "participants";

    private final Map<Long, User> cachedUsers = new HashMap<>();

    public Story inflateStory(final long storyId, @NotNull final JsonObject jsonObject) {
        return createStory(storyId, jsonObject);
    }

    @NotNull
    private Story createStory(final long storyId, @NotNull final JsonObject jsonObject) {
        User user = inflateUser(jsonObject.get(KEY_OWNER).getAsJsonObject());
        Story story = new Story(storyId, jsonObject.get(KEY_TITLE).getAsString(), user);
        if (jsonObject.has(KEY_PARAGRAPHS)) {
            inflateAllParagraphs(jsonObject, story);
        } else if (jsonObject.has(KEY_FIRST_PARAGRAPH)) {
            inflateFirstParagraph(jsonObject, story);
        }
        return story;
    }

    private void inflateFirstParagraph(@NotNull final JsonObject jsonObject, final Story story) {
        Optional.ofNullable(story.getParagraphs()).ifPresent(paragraphs -> {
            paragraphs.clear();
            paragraphs.add(inflateParagraph(jsonObject.get(KEY_FIRST_PARAGRAPH).getAsJsonObject()));
        });
    }

    private void inflateAllParagraphs(final JsonObject json, final Story story) {
        JsonArray participants = json.get(KEY_PARTICIPANTS).getAsJsonArray();
        participants.forEach(participantJson -> inflateUser(participantJson.getAsJsonObject()));
        inflateParagraphs(json, KEY_PARAGRAPHS, story.getParagraphs());
        inflateParagraphs(json, KEY_PARAGRAPHS_SUGGESTIONS, story.getSuggestions());
    }

    private void inflateParagraphs(final JsonObject storyJson,
                                   final String keyParagraphs,
                                   final List<Paragraph> output) {
        output.clear();
        JsonArray asJsonArray = storyJson.get(keyParagraphs).getAsJsonArray();
        asJsonArray.forEach(pJson -> output.add(inflateParagraph(pJson.getAsJsonObject())));
    }


    public Paragraph inflateParagraph(final JsonObject pJsonObject) {
        return createParagraph(pJsonObject);
    }

    @NotNull
    private Paragraph createParagraph(final JsonObject asJsonObject) {
        long pId = asJsonObject.get(KEY_ID).getAsLong();
        Paragraph paragraph;
        long storyId = asJsonObject.get(KEY_STORY_ID).getAsLong();
        long ownerId = asJsonObject.get(KEY_OWNER_ID).getAsLong();
        String content = asJsonObject.get(KEY_CONTENT).getAsString();
        paragraph = new Paragraph(pId, storyId, ownerId, content);
        return paragraph;
    }

    public User inflateUser(final JsonObject ownerJsonObject) {
        User user;
        long userId = ownerJsonObject.get(KEY_ID).getAsLong();
        if (cachedUsers.containsKey(userId)) {
            user = cachedUsers.get(userId);
        } else {
            user = new User(userId, ownerJsonObject.get(KEY_NAME).getAsString());
            cachedUsers.put(userId, user);
        }
        return user;
    }

    public User getUserFromCache(long id) {
        return cachedUsers.get(id);
    }
}
