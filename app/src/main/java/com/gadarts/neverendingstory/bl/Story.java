package com.gadarts.neverendingstory.bl;

public class Story {
    private final String title;
    private final long id;
    private final long owner;

    public Story(long id, String title, long owner) {
        this.id = id;
        this.title = title;
        this.owner = owner;
    }

    public long getOwner() {
        return owner;
    }

    public String getTitle() {
        return title;
    }

    public long getId() {
        return id;
    }

}
