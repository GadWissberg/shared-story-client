package com.gadarts.neverendingstory.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Story {
    private final String title;
    private final long id;
    private final User owner;

    @Setter
    private long deadline;

    private final List<Paragraph> paragraphs = new ArrayList<>();

    private final List<Paragraph> suggestions = new ArrayList<>();

    public Story(final long id, final String title, final User owner) {
        this.id = id;
        this.title = title;
        this.owner = owner;
    }

}
