package com.gadarts.neverendingstory.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class Story {
    private final String title;
    private final long id;
    private final User owner;

    private List<Paragraph> paragraphs = new ArrayList<>();

    private List<Paragraph> suggestions = new ArrayList<>();

    public Story(long id, String title, User owner) {
        this.id = id;
        this.title = title;
        this.owner = owner;
    }

}
