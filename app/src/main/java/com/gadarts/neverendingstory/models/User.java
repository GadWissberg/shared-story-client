package com.gadarts.neverendingstory.models;

import lombok.Getter;

@Getter
public class User {
    private final long id;
    private String name;

    public User(long userId) {
        this(userId, null);
    }

    public User(long userId, String userName) {
        this.id = userId;
        this.name = userName;
    }
}
