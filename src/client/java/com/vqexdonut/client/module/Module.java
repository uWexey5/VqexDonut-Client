package com.vqexdonut.client.module;

import org.lwjgl.glfw.GLFW;

public class Module {
    private final String name;
    private final Category category;
    private boolean enabled;
    private int keybind;
    private boolean binding;

    public Module(String name, Category category, int defaultKey) {
        this.name = name;
        this.category = category;
        this.keybind = defaultKey;
        this.enabled = false;
        this.binding = false;
    }

    public void toggle() {
        this.enabled = !this.enabled;
        if (this.enabled) onEnable();
        else onDisable();
    }

    public void onEnable() {}
    public void onDisable() {}
    public void onTick() {}

    public String getName() { return name; }
    public Category getCategory() { return category; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public int getKeybind() { return keybind; }
    public void setKeybind(int keybind) { this.keybind = keybind; }
    public boolean isBinding() { return binding; }
    public void setBinding(boolean binding) { this.binding = binding; }

    public String getKeyName() {
        if (keybind == GLFW.GLFW_KEY_UNKNOWN || keybind <= 0) return "NONE";
        String name = GLFW.glfwGetKeyName(keybind, 0);
        return name != null ? name.toUpperCase() : "KEY_" + keybind;
    }
}
