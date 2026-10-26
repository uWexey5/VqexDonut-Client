package com.vqexdonut.client.ui;

import com.vqexdonut.client.command.StaffCommandManager;
import com.vqexdonut.client.module.Module;
import com.vqexdonut.client.module.ModuleManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.PlayerInfo;

import java.util.Collection;

public class HudRenderer {
    private static final int ACCENT = 0xFFA855F7;

    public static void render(GuiGraphics g) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) return;

        if (ModuleManager.watermarkHud.isEnabled()) {
            String title = "VQEXDONUT";
            String info = " §7| §f" + mc.getFps() + " FPS §7| §f" + mc.player.getName().getString();
            int w = mc.font.width(title + info) + 14;
            RenderUtils.drawHudBox(g, 8, 8, w, 20, ACCENT);
            g.drawString(mc.font, "§d§l" + title + info, 15, 14, 0xFFFFFFFF, true);
        }

        if (ModuleManager.keybindsHud.isEnabled()) {
            int x = 8;
            int y = 34;
            int w = 135;

            long boundCount = ModuleManager.modules.stream()
                    .filter(m -> m.getKeybind() != -1 && m.getKeybind() > 0).count();
            int h = 22 + (int)(boundCount * 15);

            RenderUtils.drawHudBox(g, x, y, w, h, ACCENT);
            g.drawString(mc.font, "§d§lKeybinds", x + 7, y + 6, 0xFFFFFFFF, true);
            g.fill(x + 4, y + 17, x + w - 4, y + 18, 0x44FFFFFF);

            int rowY = y + 21;
            for (Module m : ModuleManager.modules) {
                if (m.getKeybind() != -1 && m.getKeybind() > 0) {
                    int dotColor = m.isEnabled() ? 0xFF22C55E : 0xFFEF4444;
                    g.fill(x + 7, rowY + 3, x + 10, rowY + 6, dotColor);
                    g.drawString(mc.font, m.getName(), x + 14, rowY, 0xFFE2E8F0, true);

                    String keyStr = "[" + m.getKeyName() + "]";
                    int kw = mc.font.width(keyStr);
                    g.drawString(mc.font, keyStr, x + w - kw - 7, rowY, m.isEnabled() ? 0xFFA855F7 : 0xFF64748B, true);
                    rowY += 15;
                }
            }
        }

        if (ModuleManager.staffHud.isEnabled()) {
            int x = 8;
            int y = 130;
            int w = 140;
            int count = Math.max(1, StaffCommandManager.staffList.size());
            int h = 22 + (count * 15);

            RenderUtils.drawHudBox(g, x, y, w, h, ACCENT);
            g.drawString(mc.font, "§d§lStaff Tracker", x + 7, y + 6, 0xFFFFFFFF, true);
            g.fill(x + 4, y + 17, x + w - 4, y + 18, 0x44FFFFFF);

            int rowY = y + 21;
            if (StaffCommandManager.staffList.isEmpty()) {
                g.drawString(mc.font, "§7Kimse yok (.staff add)", x + 7, rowY, 0xFF94A3B8, true);
            } else {
                Collection<PlayerInfo> tabList = mc.getConnection() != null ? mc.getConnection().getOnlinePlayers() : null;
                for (String staff : StaffCommandManager.staffList) {
                    boolean online = tabList != null && tabList.stream()
                            .anyMatch(p -> p.getProfile().getName().equalsIgnoreCase(staff));

                    int dot = online ? 0xFF22C55E : 0xFF64748B;
                    g.fill(x + 7, rowY + 3, x + 10, rowY + 6, dot);
                    g.drawString(mc.font, staff, x + 14, rowY, 0xFFFFFFFF, true);

                    String st = online ? "§aACTIVE" : "§7OFF";
                    int sw = mc.font.width(st);
                    g.drawString(mc.font, st, x + w - sw - 7, rowY, 0xFFFFFFFF, true);
                    rowY += 15;
                }
            }
        }

        if (ModuleManager.coordsHud.isEnabled()) {
            int sh = mc.getWindow().getGuiScaledHeight();
            String xyz = String.format("§dXYZ: §f%.1f, %.1f, %.1f", mc.player.getX(), mc.player.getY(), mc.player.getZ());
            int cw = mc.font.width(xyz) + 14;
            RenderUtils.drawHudBox(g, 8, sh - 28, cw, 20, ACCENT);
            g.drawString(mc.font, xyz, 15, sh - 22, 0xFFFFFFFF, true);
        }
    }
                      }
