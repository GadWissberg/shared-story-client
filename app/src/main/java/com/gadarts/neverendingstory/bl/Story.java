package com.gadarts.neverendingstory.bl;

import lombok.Getter;

@Getter
public class Story {
    private final String title;
    private final long id;
    private final long owner;

    public Story(long id, String title, long owner) {
        this.id = id;
        this.title = title;
        this.owner = owner;
    }

}
