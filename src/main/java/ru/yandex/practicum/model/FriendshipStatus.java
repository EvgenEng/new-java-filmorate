package ru.yandex.practicum.model;

public enum FriendshipStatus {
    PENDING("неподтверждённая"),
    CONFIRMED("подтверждённая");

    private final String description;

    FriendshipStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
