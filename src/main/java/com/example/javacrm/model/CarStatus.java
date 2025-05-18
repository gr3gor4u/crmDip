package com.example.javacrm.model;

public enum CarStatus {
    AVAILABLE("Доступен"),
    RESERVED("Зарезервирован"),
    SOLD("Продан"),
    IN_SERVICE("На обслуживании");

    private final String displayName;

    CarStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
} 