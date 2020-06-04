package com.gadarts.neverendingstory.models;

import lombok.Getter;

@Getter
public class Paragraph {
    private final long id;
    private final long storyId;
    private final long ownerId;
    private final String content;

    public Paragraph(long paragraphId, long storyId, long ownerId, String content) {
        this.id = paragraphId;
        this.storyId = storyId;
        this.ownerId = ownerId;
        this.content = content;
    }
}
