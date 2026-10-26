package com.vqexdonut.client.ui;

import com.vqexdonut.client.command.StaffCommandManager;
import com.vqexdonut.client.module.Module;
import com.vqexdonut.client.module.ModuleManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.glfw.GLFW;

import java.util.Collection;

public class HudRenderer {
    private static final int ACCENT = 0xFFA855F7;        // Argon Mor
    private static final int SPOTIFY_GREEN = 0xFF1DB954; // Spotify Yeşili

    // HUD Koordinatları (-1 ise ilk açılışta ekran boyutuna göre otomatik hizalanır)
    public static int wmX = -1, wmY = 6;
    public static int mapX = -1, mapY = 10;
    public static int spX = 8, spY = -1;
    public static int kbX = 8, kbY = 10;
    public static int stX = 8, stY = 105;
    public static int coX = 8, coY = -1;

    // Sürükleme (Drag) Değişkenleri
    private static int draggingId = 0; // 1:WM, 2:Map, 3:Spotify, 4:Keybinds, 5:Staff, 6:Coords
    private static int dragOffsetX = 0;
    private static int dragOffsetY = 0;

    public static void render(GuiGraphics g) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || mc.options.hideGui) return;

        int sw = mc.getWindow().getGuiScaledWidth();
        int sh = mc.getWindow().getGuiScaledHeight();

        // İlk açılış varsayılan konumları
        String brand = "VQEXDONUT ";
        String tag = "v2.0 ";
        String stats = "| " + mc.getFps() + " FPS | " + mc.player.getName().getString();
        int wmW = mc.font.width(brand + tag + stats) + 20;
        int wmH = 20;

        int mapW = 86, mapH = 104;
        int spW = 155, spH = 44;
        int kbW = 135;
        long boundCount = ModuleManager.modules.stream().filter(m -> m.getKeybind() != -1 && m.getKeybind() > 0).count();
        int kbH = 22 + (int)(boundCount * 15);

        int stW = 135;
        int stCount = Math.max(1, StaffCommandManager.staffList.size());
        int stH = 22 + (stCount * 15);

        String dir = getDirection(mc.player.getYRot());
        String xyz = String.format("§dXYZ §f%.0f, %.0f, %.0f §8[§d%s§8]", mc.player.getX(), mc.player.getY(), mc.player.getZ(), dir);
        int coW = mc.font.width(xyz) + 14;
        int coH = 18;

        if (wmX == -1) wmX = (sw - wmW) / 2;
        if (mapX == -1) mapX = sw - mapW - 10;
        if (spY == -1) spY = sh - spH - 32;
        if (coY == -1) coY = sh - 26;

        // CHAT AÇIKKEN FARE İLE SÜRÜKLEME KONTROLÜ
        boolean inChat = mc.screen instanceof ChatScreen;
        double mx = mc.mouseHandler.xpos() * (double) sw / (double) mc.getWindow().getScreenWidth();
        double my = mc.mouseHandler.ypos() * (double) sh / (double) mc.getWindow().getScreenHeight();
        boolean leftDown = GLFW.glfwGetMouseButton(mc.getWindow().getWindow(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;

        if (inChat) {
            if (!leftDown) {
                draggingId = 0;
            } else {
                if (draggingId == 0) {
                    if (ModuleManager.watermarkHud.isEnabled() && isHovered(mx, my, wmX, wmY, wmW, wmH)) {
                        draggingId = 1; dragOffsetX = (int)mx - wmX; dragOffsetY = (int)my - wmY;
                    } else if (ModuleManager.regionMapHud.isEnabled() && isHovered(mx, my, mapX, mapY, mapW, mapH)) {
                        draggingId = 2; dragOffsetX = (int)mx - mapX; dragOffsetY = (int)my - mapY;
                    } else if (ModuleManager.spotifyHud.isEnabled() && isHovered(mx, my, spX, spY, spW, spH)) {
                        draggingId = 3; dragOffsetX = (int)mx - spX; dragOffsetY = (int)my - spY;
                    } else if (ModuleManager.keybindsHud.isEnabled() && isHovered(mx, my, kbX, kbY, kbW, kbH)) {
                        draggingId = 4; dragOffsetX = (int)mx - kbX; dragOffsetY = (int)my - kbY;
                    } else if (ModuleManager.staffHud.isEnabled() && isHovered(mx, my, stX, stY, stW, stH)) {
                        draggingId = 5; dragOffsetX = (int)mx - stX; dragOffsetY = (int)my - stY;
                    } else if (ModuleManager.coordsHud.isEnabled() && isHovered(mx, my, coX, coY, coW, coH)) {
                        draggingId = 6; dragOffsetX = (int)mx - coX; dragOffsetY = (int)my - coY;
                    }
                }

                // Sürüklenen HUD'un konumunu güncelle
                if (draggingId == 1) { wmX = Mth.clamp((int)mx - dragOffsetX, 0, sw - wmW); wmY = Mth.clamp((int)my - dragOffsetY, 0, sh - wmH); }
                else if (draggingId == 2) { mapX = Mth.clamp((int)mx - dragOffsetX, 0, sw - mapW); mapY = Mth.clamp((int)my - dragOffsetY, 0, sh - mapH); }
                else if (draggingId == 3) { spX = Mth.clamp((int)mx - dragOffsetX, 0, sw - spW); spY = Mth.clamp((int)my - dragOffsetY, 0, sh - spH); }
                else if (draggingId == 4) { kbX = Mth.clamp((int)mx - dragOffsetX, 0, sw - kbW); kbY = Mth.clamp((int)my - dragOffsetY, 0, sh - kbH); }
                else if (draggingId == 5) { stX = Mth.clamp((int)mx - dragOffsetX, 0, sw - stW); stY = Mth.clamp((int)my - dragOffsetY, 0, sh - stH); }
                else if (draggingId == 6) { coX = Mth.clamp((int)mx - dragOffsetX, 0, sw - coW); coY = Mth.clamp((int)my - dragOffsetY, 0, sh - coH); }
            }

            // Üstte bilgilendirme yazısı
            String dragTip = "§d§l[HUD Editor] §7HUD'lari fareyle tutup istedigin yere surukleyebilirsin!";
            g.drawString(mc.font, dragTip, (sw - mc.font.width(dragTip)) / 2, 32, 0xFFFFFFFF, true);
        } else {
            draggingId = 0;
        }

        // 1. WATERMARK HUD
        if (ModuleManager.watermarkHud.isEnabled()) {
            RenderUtils.drawHudPanel(g, wmX, wmY, wmW, wmH, ACCENT);
            g.drawString(mc.font, "§d§l" + brand + "§5" + tag + "§8| §f" + mc.getFps() + " §7FPS §8| §f" + mc.player.getName().getString(), wmX + 10, wmY + 6, 0xFFFFFFFF, true);
            if (inChat) drawEditOutline(g, wmX, wmY, wmW, wmH, draggingId == 1);
        }

        // 2. REGION MAP HUD
        if (ModuleManager.regionMapHud.isEnabled()) {
            RenderUtils.drawHudPanel(g, mapX, mapY, mapW, mapH, ACCENT);
            int chunkX = mc.player.chunkPosition().x;
            int chunkZ = mc.player.chunkPosition().z;
            String regTitle = "§d§lRegion §7[" + chunkX + "," + chunkZ + "]";
            g.drawString(mc.font, regTitle, mapX + 6, mapY + 5, 0xFFFFFFFF, true);

            int radarX = mapX + 5;
            int radarY = mapY + 16;
            int radarW = mapW - 10;
            int radarH = mapW - 6;
            RenderUtils.drawArgonBox(g, radarX, radarY, radarW, radarH, 0xFF09090F, 0xFF0D0D16, 0xFF1F1F2E);

            g.fill(radarX + radarW / 3, radarY + 2, radarX + radarW / 3 + 1, radarY + radarH - 2, 0x22FFFFFF);
            g.fill(radarX + (radarW * 2) / 3, radarY + 2, radarX + (radarW * 2) / 3 + 1, radarY + radarH - 2, 0x22FFFFFF);
            g.fill(radarX + 2, radarY + radarH / 3, radarX + radarW - 2, radarY + radarH / 3 + 1, 0x22FFFFFF);
            g.fill(radarX + 2, radarY + (radarH * 2) / 3, radarX + radarW - 2, radarY + (radarH * 2) / 3 + 1, 0x22FFFFFF);

            int centerX = radarX + radarW / 2;
            int centerY = radarY + radarH / 2;
            g.fill(radarX + 2, centerY, radarX + radarW - 2, centerY + 1, 0x33A855F7);
            g.fill(centerX, radarY + 2, centerX + 1, radarY + radarH - 2, 0x33A855F7);

            float yawRad = (float) Math.toRadians(mc.player.getYRot());
            for (Player p : mc.level.players()) {
                if (p == mc.player) continue;
                double dx = p.getX() - mc.player.getX();
                double dz = p.getZ() - mc.player.getZ();

                double rotX = -(dx * Math.cos(yawRad) + dz * Math.sin(yawRad));
                double rotZ = -(dz * Math.cos(yawRad) - dx * Math.sin(yawRad));

                int px = centerX + (int) Mth.clamp(rotX * 0.7, -(radarW / 2 - 4), (radarW / 2 - 4));
                int py = centerY + (int) Mth.clamp(rotZ * 0.7, -(radarH / 2 - 4), (radarH / 2 - 4));

                boolean isStaff = StaffCommandManager.staffList.stream().anyMatch(s -> s.equalsIgnoreCase(p.getName().getString()));
                int dotCol = isStaff ? 0xFFEF4444 : 0xFF38BDF8;
                g.fill(px - 2, py - 2, px + 2, py + 2, dotCol);
            }

            g.fill(centerX - 2, centerY - 2, centerX + 3, centerY + 3, 0xFFA855F7);
            g.fill(centerX - 1, centerY - 1, centerX + 2, centerY + 2, 0xFFFFFFFF);
            g.drawString(mc.font, "§7N", centerX - 2, radarY + 3, 0xFFFFFFFF, false);
            if (inChat) drawEditOutline(g, mapX, mapY, mapW, mapH, draggingId == 2);
        }

        // 3. SPOTIFY HUD
        if (ModuleManager.spotifyHud.isEnabled()) {
            RenderUtils.drawHudPanel(g, spX, spY, spW, spH, SPOTIFY_GREEN);

            int eqX = spX + 6;
            int eqY = spY + 7;
            RenderUtils.drawArgonBox(g, eqX, eqY, 30, 30, 0xFF14291D, 0xFF0A140E, SPOTIFY_GREEN);

            long time = System.currentTimeMillis();
            for (int i = 0; i < 4; i++) {
                int barH = 6 + (int) (Math.abs(Math.sin((time / 180.0) + i * 1.4)) * 16);
                int bx = eqX + 5 + (i * 6);
                g.fill(bx, eqY + 25 - barH, bx + 4, eqY + 25, SPOTIFY_GREEN);
            }

            String track = StaffCommandManager.currentTrack;
            String artist = StaffCommandManager.currentArtist;
            if (mc.font.width(track) > 105) track = track.substring(0, 15) + "...";
            if (mc.font.width(artist) > 105) artist = artist.substring(0, 17) + "...";

            g.drawString(mc.font, "§a§lSpotify §7• Playing", spX + 42, spY + 6, 0xFFFFFFFF, true);
            g.drawString(mc.font, "§f" + track, spX + 42, spY + 16, 0xFFFFFFFF, true);
            g.drawString(mc.font, "§7" + artist, spX + 42, spY + 25, 0xFF94A3B8, true);

            int barX = spX + 42;
            int barY = spY + 36;
            int barW = spW - 48;
            g.fill(barX, barY, barX + barW, barY + 3, 0xFF262638);
            int progress = (int) (((time / 100) % 100) / 100.0 * barW);
            g.fill(barX, barY, barX + progress, barY + 3, SPOTIFY_GREEN);
            if (inChat) drawEditOutline(g, spX, spY, spW, spH, draggingId == 3);
        }

        // 4. KEYBINDS HUD
        if (ModuleManager.keybindsHud.isEnabled()) {
            RenderUtils.drawHudPanel(g, kbX, kbY, kbW, kbH, ACCENT);
            g.drawString(mc.font, "§d§lKeybinds", kbX + 7, kbY + 6, 0xFFFFFFFF, true);
            g.fill(kbX + 5, kbY + 17, kbX + kbW - 5, kbY + 18, 0x28FFFFFF);

            int rowY = kbY + 21;
            for (Module m : ModuleManager.modules) {
                if (m.getKeybind() != -1 && m.getKeybind() > 0) {
                    int dotColor = m.isEnabled() ? 0xFF22C55E : 0xFF475569;
                    g.fill(kbX + 7, rowY + 2, kbX + 10, rowY + 6, dotColor);
                    g.drawString(mc.font, m.getName(), kbX + 14, rowY, m.isEnabled() ? 0xFFFFFFFF : 0xFF94A3B8, true);

                    String keyStr = "[" + m.getKeyName() + "]";
                    int kw = mc.font.width(keyStr);
                    g.drawString(mc.font, keyStr, kbX + kbW - kw - 7, rowY, m.isEnabled() ? 0xFFD8B4FE : 0xFF64748B, true);
                    rowY += 15;
                }
            }
            if (inChat) drawEditOutline(g, kbX, kbY, kbW, kbH, draggingId == 4);
        }

        // 5. STAFF TRACKER HUD
        if (ModuleManager.staffHud.isEnabled()) {
            RenderUtils.drawHudPanel(g, stX, stY, stW, stH, ACCENT);
            g.drawString(mc.font, "§d§lStaff Tracker", stX + 7, stY + 6, 0xFFFFFFFF, true);
            g.fill(stX + 5, stY + 17, stX + stW - 5, stY + 18, 0x28FFFFFF);

            int rowY = stY + 21;
            if (StaffCommandManager.staffList.isEmpty()) {
                g.drawString(mc.font, "§7Yok (.staff add)", stX + 7, rowY, 0xFF64748B, true);
            } else {
                Collection<PlayerInfo> tabList = mc.getConnection() != null ? mc.getConnection().getOnlinePlayers() : null;
                for (String staff : StaffCommandManager.staffList) {
                    boolean online = tabList != null && tabList.stream()
                            .anyMatch(p -> p.getProfile().getName().equalsIgnoreCase(staff));

                    int dot = online ? 0xFF22C55E : 0xFFEF4444;
                    g.fill(stX + 7, rowY + 2, stX + 10, rowY + 6, dot);
                    g.drawString(mc.font, staff, stX + 14, rowY, 0xFFFFFFFF, true);

                    String st = online ? "§aON" : "§cOFF";
                    int sw2 = mc.font.width(st);
                    g.drawString(mc.font, st, stX + stW - sw2 - 7, rowY, 0xFFFFFFFF, true);
                    rowY += 15;
                }
            }
            if (inChat) drawEditOutline(g, stX, stY, stW, stH, draggingId == 5);
        }

        // 6. COORDINATES HUD
        if (ModuleManager.coordsHud.isEnabled()) {
            RenderUtils.drawHudPanel(g, coX, coY, coW, coH, ACCENT);
            g.drawString(mc.font, xyz, coX + 7, coY + 5, 0xFFFFFFFF, true);
            if (inChat) drawEditOutline(g, coX, coY, coW, coH, draggingId == 6);
        }
    }

    private static boolean isHovered(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    private static void drawEditOutline(GuiGraphics g, int x, int y, int w, int h, boolean dragging) {
        int col = dragging ? 0xFF22C55E : 0xAAFFFFFF;
        g.fill(x - 1, y - 1, x + w + 1, y, col);
        g.fill(x - 1, y + h, x + w + 1, y + h + 1, col);
        g.fill(x - 1, y, x, y + h, col);
        g.fill(x + w, y, x + w + 1, y + h, col);
    }

    private static String getDirection(float yaw) {
        int dir = Mth.floor((double) (yaw * 4.0F / 360.0F) + 0.5D) & 3;
        return switch (dir) {
            case 0 -> "S";
            case 1 -> "W";
            case 2 -> "N";
            case 3 -> "E";
            default -> "N";
        };
    }
}
