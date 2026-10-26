package com.vqexdonut.client.command;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import java.util.ArrayList;
import java.util.List;

public class StaffCommandManager {
    public static final List<String> staffList = new ArrayList<>();

    public static boolean handleChat(String message) {
        if (!message.startsWith(".staff")) return false;

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
                sendMsg("§a" + target + " §7listeye eklendi.");
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

    private static void sendMsg(String text) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            mc.player.displayClientMessage(Component.literal("§8[§5Vqex§dDonut§8] §r" + text), false);
        }
    }
}
