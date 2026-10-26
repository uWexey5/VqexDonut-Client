package com.vqexdonut.client.command;

import com.mojang.blaze3d.platform.InputConstants;
import com.vqexdonut.client.module.Module;
import com.vqexdonut.client.module.ModuleManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class StaffCommandManager {
    public static final List<String> staffList = new ArrayList<>();
    public static String currentTrack = "Starboy";
    public static String currentArtist = "The Weeknd, Daft Punk";

    public static boolean handleChat(String message) {
        // 1. .BIND KOMUTLARI (.bind add / remove / list)
        if (message.startsWith(".bind")) {
            String[] args = message.split(" ");
            if (args.length < 2) {
                sendMsg("§7Kullanim: §d.bind <add/remove/list> [Modul] [Tus]");
                return true;
            }

            String sub = args[1].toLowerCase();

            if (sub.equals("list")) {
                sendMsg("§d§lAktif Tus Atamalari (Keybinds):");
                boolean found = false;
                for (Module m : ModuleManager.modules) {
                    if (m.getKeybind() != GLFW.GLFW_KEY_UNKNOWN && m.getKeybind() > 0) {
                        sendMsg(" §8• §f" + m.getName() + " §7-> §d[" + m.getKeyName() + "]");
                        found = true;
                    }
                }
                if (!found) sendMsg(" §7Hicbir module tus atanmamis.");
                return true;
            }

            if (sub.equals("remove") || sub.equals("del")) {
                if (args.length < 3) {
                    sendMsg("§7Kullanim: §d.bind remove <ModulIsmi>");
                    return true;
                }
                Module m = findModule(args[2]);
                if (m == null) {
                    sendMsg("§cModul bulunamadi: §f" + args[2]);
                    return true;
                }
                m.setKeybind(GLFW.GLFW_KEY_UNKNOWN);
                sendMsg("§a" + m.getName() + " §7modulunun tus atamasi sifirlandi.");
                return true;
            }

            if (sub.equals("add") || sub.equals("set")) {
                if (args.length < 4) {
                    sendMsg("§7Kullanim: §d.bind add <ModulIsmi> <Tus> §8(Orn: .bind add Fullbright G)");
                    return true;
                }
                Module m = findModule(args[2]);
                if (m == null) {
                    sendMsg("§cModul bulunamadi: §f" + args[2]);
                    return true;
                }
                int key = parseKey(args[3]);
                if (key == GLFW.GLFW_KEY_UNKNOWN) {
                    sendMsg("§cGecersiz tus: §f" + args[3].toUpperCase());
                    return true;
                }
                m.setKeybind(key);
                sendMsg("§a" + m.getName() + " §7modulu §d[" + m.getKeyName() + "] §7tusuna atandi!");
                return true;
            }

            sendMsg("§7Kullanim: §d.bind <add/remove/list>");
            return true;
        }

        // 2. .SPOTIFY KOMUTU
        if (message.startsWith(".spotify")) {
            String content = message.substring(8).trim();
            if (content.isEmpty()) {
                sendMsg("§7Kullanim: §a.spotify Sarki Adi - Sanatci");
                return true;
            }
            if (content.contains("-")) {
                String[] parts = content.split("-", 2);
                currentTrack = parts[0].trim();
                currentArtist = parts[1].trim();
            } else {
                currentTrack = content;
                currentArtist = "Spotify Web API";
            }
            sendMsg("§aSpotify HUD guncellendi: §f" + currentTrack + " §7(" + currentArtist + ")");
            return true;
        }

        // 3. .STAFF KOMUTU
        if (message.startsWith(".staff")) {
            String[] args = message.split(" ");
            if (args.length < 3) {
                sendMsg("§7Kullanim: §d.staff <add/remove> <isim>");
                return true;
            }

            String sub = args[1].toLowerCase();
            String target = args[2];

            if (sub.equals("add")) {
                if (!staffList.contains(target)) {
                    staffList.add(target);
                    sendMsg("§a" + target + " §7Staff HUD listesine eklendi.");
                } else {
                    sendMsg("§e" + target + " §7zaten listede.");
                }
            } else if (sub.equals("remove")) {
                if (staffList.remove(target)) {
                    sendMsg("§c" + target + " §7listeden silindi.");
                } else {
                    sendMsg("§cOyuncu listede yok.");
                }
            }
            return true;
        }

        return false;
    }

    private static Module findModule(String input) {
        String cleanInput = input.replace("_", "").replace(" ", "");
        for (Module m : ModuleManager.modules) {
            String cleanMod = m.getName().replace(" ", "");
            if (cleanMod.equalsIgnoreCase(cleanInput) || m.getName().toLowerCase().startsWith(cleanInput.toLowerCase())) {
                return m;
            }
        }
        return null;
    }

    private static int parseKey(String keyStr) {
        String k = keyStr.toUpperCase();
        if (k.equals("NONE") || k.equals("0")) return GLFW.GLFW_KEY_UNKNOWN;
        if (k.length() == 1) {
            char c = k.charAt(0);
            if ((c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')) {
                return (int) c; // GLFW'da A-Z ve 0-9 ASCII kodlarıyla birebir aynıdır
            }
        }
        try {
            InputConstants.Key key = InputConstants.getKey("key.keyboard." + keyStr.toLowerCase());
            if (key != null && key != InputConstants.UNKNOWN) {
                return key.getValue();
            }
        } catch (Exception ignored) {}

        return switch (k) {
            case "RSHIFT" -> GLFW.GLFW_KEY_RIGHT_SHIFT;
            case "LSHIFT", "SHIFT" -> GLFW.GLFW_KEY_LEFT_SHIFT;
            case "LCTRL", "CTRL" -> GLFW.GLFW_KEY_LEFT_CONTROL;
            case "RCTRL" -> GLFW.GLFW_KEY_RIGHT_CONTROL;
            case "LALT", "ALT" -> GLFW.GLFW_KEY_LEFT_ALT;
            case "SPACE" -> GLFW.GLFW_KEY_SPACE;
            case "TAB" -> GLFW.GLFW_KEY_TAB;
            case "CAPS" -> GLFW.GLFW_KEY_CAPS_LOCK;
            default -> GLFW.GLFW_KEY_UNKNOWN;
        };
    }

    private static void sendMsg(String text) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            mc.player.displayClientMessage(Component.literal("§8[§dVqex§5Argon§8] §r" + text), false);
        }
    }
}
