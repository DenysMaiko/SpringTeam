package com.example.notepad.model;

public enum Priority {
    HIGH("Високий"),
    MEDIUM("Середній"),
    LOW("Низький");

    private final String displayName;

    Priority(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}