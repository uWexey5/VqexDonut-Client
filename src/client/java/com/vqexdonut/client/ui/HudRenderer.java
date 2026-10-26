package com.vqexdonut.client.ui;

import com.vqexdonut.client.command.StaffCommandManager;
import com.vqexdonut.client.module.Module;
import com.vqexdonut.client.module.ModuleManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;

public class HudRenderer {
    private static final int ACCENT = 0xFFA855F7;        // Argon Mor
    private static final int SPOTIFY_GREEN = 0xFF1DB954; // Spotify Yeşili

    public static void render(GuiGraphics g) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || mc.options.hideGui) return;

        int sw = mc.getWindow().getGuiScaledWidth();
        int sh = mc.getWindow().getGuiScaledHeight();

        // 1. ORTADA DURAN ARGON WATERMARK
        if (ModuleManager.watermarkHud.isEnabled()) {
            String brand = "VQEXDONUT ";
            String tag = "v2.0 ";
            String stats = "| " + mc.getFps() + " FPS | " + mc.player.getName().getString();
            String fullText = brand + tag + stats;

            int w = mc.font.width(fullText) + 20;
            int x = (sw - w) / 2; // Tam ekran ortası
            int y = 6;

            RenderUtils.drawHudPanel(g, x, y, w, 20, ACCENT);
            g.drawString(mc.font, "§d§l" + brand + "§5" + tag + "§8| §f" + mc.getFps() + " §7FPS §8| §f" + mc.player.getName().getString(), x + 10, y + 6, 0xFFFFFFFF, true);
        }

        // 2. REGION MAP (SAĞ ÜST RADAR & CHUNK HARİTASI)
        if (ModuleManager.regionMapHud.isEnabled()) {
            int mapSize = 86;
            int mx = sw - mapSize - 10;
            int my = 10;

            RenderUtils.drawHudPanel(g, mx, my, mapSize, mapSize + 18, ACCENT);
            int chunkX = mc.player.chunkPosition().x;
            int chunkZ = mc.player.chunkPosition().z;
            String regTitle = "§d§lRegion §7[" + chunkX + "," + chunkZ + "]";
            g.drawString(mc.font, regTitle, mx + 6, my + 5, 0xFFFFFFFF, true);

            // Harita İç Kutusu
            int radarX = mx + 5;
            int radarY = my + 16;
            int radarW = mapSize - 10;
            int radarH = mapSize - 6;
            RenderUtils.drawArgonBox(g, radarX, radarY, radarW, radarH, 0xFF09090F, 0xFF0D0D16, 0xFF1F1F2E);

            // Chunk Izgara Çizgileri (3x3 Bölge)
            g.fill(radarX + radarW / 3, radarY + 2, radarX + radarW / 3 + 1, radarY + radarH - 2, 0x22FFFFFF);
            g.fill(radarX + (radarW * 2) / 3, radarY + 2, radarX + (radarW * 2) / 3 + 1, radarY + radarH - 2, 0x22FFFFFF);
            g.fill(radarX + 2, radarY + radarH / 3, radarX + radarW - 2, radarY + radarH / 3 + 1, 0x22FFFFFF);
            g.fill(radarX + 2, radarY + (radarH * 2) / 3, radarX + radarW - 2, radarY + (radarH * 2) / 3 + 1, 0x22FFFFFF);

            // Merkez Artı Çizgisi
            int centerX = radarX + radarW / 2;
            int centerY = radarY + radarH / 2;
            g.fill(radarX + 2, centerY, radarX + radarW - 2, centerY + 1, 0x33A855F7);
            g.fill(centerX, radarY + 2, centerX + 1, radarY + radarH - 2, 0x33A855F7);

            // Çevredeki Oyuncuları Haritada Göster
            float yawRad = (float) Math.toRadians(mc.player.getYRot());
            for (Player p : mc.level.players()) {
                if (p == mc.player) continue;
                double dx = p.getX() - mc.player.getX();
                double dz = p.getZ() - mc.player.getZ();

                // Oyuncunun baktığı yöne göre döndür
                double rotX = -(dx * Math.cos(yawRad) + dz * Math.sin(yawRad));
                double rotZ = -(dz * Math.cos(yawRad) - dx * Math.sin(yawRad));

                int px = centerX + (int) Mth.clamp(rotX * 0.7, -(radarW / 2 - 4), (radarW / 2 - 4));
                int py = centerY + (int) Mth.clamp(rotZ * 0.7, -(radarH / 2 - 4), (radarH / 2 - 4));

                boolean isStaff = StaffCommandManager.staffList.stream().anyMatch(s -> s.equalsIgnoreCase(p.getName().getString()));
                int dotCol = isStaff ? 0xFFEF4444 : 0xFF38BDF8;
                g.fill(px - 2, py - 2, px + 2, py + 2, dotCol);
            }

            // Merkezde Kendi İkonumuz (Yeşil/Mor Nokta)
            g.fill(centerX - 2, centerY - 2, centerX + 3, centerY + 3, 0xFFA855F7);
            g.fill(centerX - 1, centerY - 1, centerX + 2, centerY + 2, 0xFFFFFFFF);
            g.drawString(mc.font, "§7N", centerX - 2, radarY + 3, 0xFFFFFFFF, false);
        }

        // 3. SPOTIFY HUD (SOL ALT DİNAMİK MÜZİK PANELİ)
        if (ModuleManager.spotifyHud.isEnabled()) {
            int spW = 155;
            int spH = 44;
            int spX = 8;
            int spY = sh - spH - (ModuleManager.coordsHud.isEnabled() ? 32 : 8);

            RenderUtils.drawHudPanel(g, spX, spY, spW, spH, SPOTIFY_GREEN);

            // Sol Albüm/Plak Kutusu ve Canlı Equalizer Barları
            int eqX = spX + 6;
            int eqY = spY + 7;
            RenderUtils.drawArgonBox(g, eqX, eqY, 30, 30, 0xFF14291D, 0xFF0A140E, SPOTIFY_GREEN);

            long time = System.currentTimeMillis();
            for (int i = 0; i < 4; i++) {
                int barH = 6 + (int) (Math.abs(Math.sin((time / 180.0) + i * 1.4)) * 16);
                int bx = eqX + 5 + (i * 6);
                g.fill(bx, eqY + 25 - barH, bx + 4, eqY + 25, SPOTIFY_GREEN);
            }

            // Şarkı ve Sanatçı Bilgisi
            String track = StaffCommandManager.currentTrack;
            String artist = StaffCommandManager.currentArtist;
            if (mc.font.width(track) > 105) track = track.substring(0, 15) + "...";
            if (mc.font.width(artist) > 105) artist = artist.substring(0, 17) + "...";

            g.drawString(mc.font, "§a§lSpotify §7• Playing", spX + 42, spY + 6, 0xFFFFFFFF, true);
            g.drawString(mc.font, "§f" + track, spX + 42, spY + 16, 0xFFFFFFFF, true);
            g.drawString(mc.font, "§7" + artist, spX + 42, spY + 25, 0xFF94A3B8, true);

            // Hareketli Süre Çubuğu (Progress Bar)
            int barX = spX + 42;
            int barY = spY + 36;
            int barW = spW - 48;
            g.fill(barX, barY, barX + barW, barY + 3, 0xFF262638);
            int progress = (int) (((time / 100) % 100) / 100.0 * barW);
            g.fill(barX, barY, barX + progress, barY + 3, SPOTIFY_GREEN);
        }

        // 4. KEYBINDS HUD (SOL ÜST)
        if (ModuleManager.keybindsHud.isEnabled()) {
            int x = 8;
            int y = 10;
            int w = 135;

            long boundCount = ModuleManager.modules.stream()
                    .filter(m -> m.getKeybind() != -1 && m.getKeybind() > 0).count();
            int h = 22 + (int)(boundCount * 15);

            RenderUtils.drawHudPanel(g, x, y, w, h, ACCENT);
            g.drawString(mc.font, "§d§lKeybinds", x + 7, y + 6, 0xFFFFFFFF, true);
            g.fill(x + 5, y + 17, x + w - 5, y + 18, 0x28FFFFFF);

            int rowY = y + 21;
            for (Module m : ModuleManager.modules) {
                if (m.getKeybind() != -1 && m.getKeybind() > 0) {
                    int dotColor = m.isEnabled() ? 0xFF22C55E : 0xFF475569;
                    g.fill(x + 7, rowY + 2, x + 10, rowY + 6, dotColor);
                    g.drawString(mc.font, m.getName(), x + 14, rowY, m.isEnabled() ? 0xFFFFFFFF : 0xFF94A3B8, true);

                    String keyStr = "[" + m.getKeyName() + "]";
                    int kw = mc.font.width(keyStr);
                    g.drawString(mc.font, keyStr, x + w - kw - 7, rowY, m.isEnabled() ? 0xFFD8B4FE : 0xFF64748B, true);
                    rowY += 15;
                }
            }
        }

        // 5. STAFF TRACKER HUD
        if (ModuleManager.staffHud.isEnabled()) {
            int x = 8;
            int y = 105;
            int w = 135;
            int count = Math.max(1, StaffCommandManager.staffList.size());
            int h = 22 + (count * 15);

            RenderUtils.drawHudPanel(g, x, y, w, h, ACCENT);
            g.drawString(mc.font, "§d§lStaff Tracker", x + 7, y + 6, 0xFFFFFFFF, true);
            g.fill(x + 5, y + 17, x + w - 5, y + 18, 0x28FFFFFF);

            int rowY = y + 21;
            if (StaffCommandManager.staffList.isEmpty()) {
                g.drawString(mc.font, "§7Yok (.staff add)", x + 7, rowY, 0xFF64748B, true);
            } else {
                Collection<PlayerInfo> tabList = mc.getConnection() != null ? mc.getConnection().getOnlinePlayers() : null;
                for (String staff : StaffCommandManager.staffList) {
                    boolean online = tabList != null && tabList.stream()
                            .anyMatch(p -> p.getProfile().getName().equalsIgnoreCase(staff));

                    int dot = online ? 0xFF22C55E : 0xFFEF4444;
                    g.fill(x + 7, rowY + 2, x + 10, rowY + 6, dot);
                    g.drawString(mc.font, staff, x + 14, rowY, 0xFFFFFFFF, true);

                    String st = online ? "§aON" : "§cOFF";
                    int sw2 = mc.font.width(st);
                    g.drawString(mc.font, st, x + w - sw2 - 7, rowY, 0xFFFFFFFF, true);
                    rowY += 15;
                }
            }
        }

        // 6. COORDINATES HUD (EN SOL ALT)
        if (ModuleManager.coordsHud.isEnabled()) {
            String dir = getDirection(mc.player.getYRot());
            String xyz = String.format("§dXYZ §f%.0f, %.0f, %.0f §8[§d%s§8]", mc.player.getX(), mc.player.getY(), mc.player.getZ(), dir);
            int cw = mc.font.width(xyz) + 14;
            RenderUtils.drawHudPanel(g, 8, sh - 26, cw, 18, ACCENT);
            g.drawString(mc.font, xyz, 15, sh - 21, 0xFFFFFFFF, true);
        }
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
