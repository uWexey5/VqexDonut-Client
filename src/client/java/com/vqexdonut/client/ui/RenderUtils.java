package com.vqexdonut.client.ui;

import net.minecraft.client.gui.GuiGraphics;

public class RenderUtils {

    public static void drawCustomBox(GuiGraphics g, int x, int y, int w, int h, int bgColor, int borderColor) {
        g.fill(x - 1, y - 1, x + w + 1, y + h + 2, 0x55000000);

        g.fill(x + 2, y, x + w - 2, y + h, bgColor);
        g.fill(x, y + 2, x + w, y + h - 2, bgColor);
        g.fill(x + 1, y + 1, x + w - 1, y + h - 1, bgColor);

        g.fill(x + 2, y, x + w - 2, y + 1, borderColor);
        g.fill(x + 2, y + h - 1, x + w - 2, y + h, borderColor);

        g.fill(x, y + 2, x + 1, y + h - 2, borderColor);
        g.fill(x + w - 1, y + 2, x + w, y + h - 2, borderColor);
    }

    public static void drawHudBox(GuiGraphics g, int x, int y, int w, int h, int accentColor) {
        drawCustomBox(g, x, y, w, h, 0xD90E0E14, 0xFF222230);
        g.fill(x + 2, y, x + w - 2, y + 2, accentColor);
    }
}
