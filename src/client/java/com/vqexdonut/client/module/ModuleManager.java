package com.vqexdonut.client.module;

import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleManager {
    public static final List<Module> modules = new ArrayList<>();

    public static Module watermarkHud;
    public static Module keybindsHud;
    public static Module staffHud;
    public static Module coordsHud;
    public static Module toggleSprint;
    public static Module fullbright;

    public static void init() {
        watermarkHud = new Module("Watermark", Category.HUD, GLFW.GLFW_KEY_UNKNOWN);
        watermarkHud.setEnabled(true);

        keybindsHud = new Module("Keybinds", Category.HUD, GLFW.GLFW_KEY_UNKNOWN);
        keybindsHud.setEnabled(true);

        staffHud = new Module("Staff List", Category.HUD, GLFW.GLFW_KEY_UNKNOWN);
        staffHud.setEnabled(true);

        coordsHud = new Module("Coordinates", Category.HUD, GLFW.GLFW_KEY_UNKNOWN);
        coordsHud.setEnabled(true);

        modules.add(watermarkHud);
        modules.add(keybindsHud);
        modules.add(staffHud);
        modules.add(coordsHud);

        toggleSprint = new Module("AutoSprint", Category.UTILITY, GLFW.GLFW_KEY_V) {
            @Override
            public void onTick() {
                Minecraft mc = Minecraft.getInstance();
                if (mc.player != null && mc.player.zza > 0 && !mc.player.isShiftKeyDown()) {
                    mc.player.setSprinting(true);
                }
            }
        };
        toggleSprint.setEnabled(true);

        fullbright = new Module("Fullbright", Category.RENDER, GLFW.GLFW_KEY_G) {
            @Override
            public void onTick() {
                Minecraft mc = Minecraft.getInstance();
                if (mc.options != null) {
                    mc.options.gamma().set(16.0);
                }
            }
            @Override
            public void onDisable() {
                Minecraft mc = Minecraft.getInstance();
                if (mc.options != null) {
                    mc.options.gamma().set(1.0);
                }
            }
        };

        modules.add(toggleSprint);
        modules.add(fullbright);
        modules.add(new Module("ArmorHUD", Category.COMBAT, GLFW.GLFW_KEY_UNKNOWN));
        modules.add(new Module("HitColor", Category.RENDER, GLFW.GLFW_KEY_UNKNOWN));
    }

    public static List<Module> getModulesByCategory(Category c) {
        return modules.stream().filter(m -> m.getCategory() == c).collect(Collectors.toList());
    }
                  }
