package com.gadarts.neverendingstory.bl;

public class Story {
    private final String title;
    private final long id;

    public Story(long id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public long getId() {
        return id;
    }

}
