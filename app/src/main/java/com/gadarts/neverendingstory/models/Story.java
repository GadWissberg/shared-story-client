package com.gadarts.neverendingstory.models;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Story {
    private final String title;
    private final long id;
    private final User owner;

    @Setter
    private List<Paragraph> paragraphs;

    @Setter
    private List<Paragraph> suggestions;

    public Story(long id, String title, User owner) {
        this.id = id;
        this.title = title;
        this.owner = owner;
    }

}
