package com.github.sharifrahim.onboard.domain;

public enum ProgressState {
    PROFILE, CONTACT, OPERATIONS, COMPLETED;

    public ProgressState next() {
        return switch (this) {
        case PROFILE -> CONTACT;
        case CONTACT -> OPERATIONS;
        case OPERATIONS, COMPLETED -> COMPLETED;
        };
    }
}
