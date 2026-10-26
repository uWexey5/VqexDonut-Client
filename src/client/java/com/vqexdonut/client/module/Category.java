package com.vqexdonut.client.module;

public enum Category {
    COMBAT("Combat"),
    RENDER("Render"),
    HUD("HUD"),
    UTILITY("Utility");

    public final String name;

    Category(String name) {
        this.name = name;
    }
}
