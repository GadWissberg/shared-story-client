package com.gadarts.neverendingstory.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Paragraph {
    private final long id;
    private final long storyId;
    private final long ownerId;
    private final String content;

    @Setter(AccessLevel.PUBLIC)
    private boolean voteable;

    @Setter(AccessLevel.PUBLIC)
    private int votes;

    public Paragraph(final long paragraphId,
                     final long storyId,
                     final long ownerId,
                     final String content) {
        this.id = paragraphId;
        this.storyId = storyId;
        this.ownerId = ownerId;
        this.content = content;
    }
}
