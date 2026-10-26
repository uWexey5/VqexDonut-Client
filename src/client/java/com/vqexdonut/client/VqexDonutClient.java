package com.vqexdonut.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.vqexdonut.client.command.StaffCommandManager;
import com.vqexdonut.client.module.Module;
import com.vqexdonut.client.module.ModuleManager;
import com.vqexdonut.client.ui.ClickGuiScreen;
import com.vqexdonut.client.ui.HudRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

public class VqexDonutClient implements ClientModInitializer {
    private boolean rShiftWasDown = false;
    private final Map<Module, Boolean> keyStates = new HashMap<>();

    @Override
    public void onInitializeClient() {
        ModuleManager.init();

        HudRenderCallback.EVENT.register((graphics, tickCounter) -> {
            HudRenderer.render(graphics);
        });

        ClientSendMessageEvents.ALLOW_CHAT.register((message) -> {
            return !StaffCommandManager.handleChat(message);
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            for (Module m : ModuleManager.modules) {
                if (m.isEnabled()) {
                    m.onTick();
                }
            }

            if (client.screen != null) return;

            long window = client.getWindow().getWindow();

            boolean rShiftDown = InputConstants.isKeyDown(window, GLFW.GLFW_KEY_RIGHT_SHIFT);
            if (rShiftDown && !rShiftWasDown) {
                client.setScreen(new ClickGuiScreen());
            }
            rShiftWasDown = rShiftDown;

            for (Module m : ModuleManager.modules) {
                int key = m.getKeybind();
                if (key != GLFW.GLFW_KEY_UNKNOWN && key > 0) {
                    boolean down = InputConstants.isKeyDown(window, key);
                    boolean wasDown = keyStates.getOrDefault(m, false);
                    if (down && !wasDown) {
                        m.toggle();
                    }
                    keyStates.put(m, down);
                }
            }
        });
    }
            }
                    
