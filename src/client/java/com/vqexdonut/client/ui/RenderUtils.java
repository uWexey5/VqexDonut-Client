package com.vqexdonut.client.ui;

import net.minecraft.client.gui.GuiGraphics;

public class RenderUtils {

    // Yumuşak köşeli, dış gölgeli (glow/shadow) ve dikey renk geçişli (gradient) Argon kutusu
    public static void drawArgonBox(GuiGraphics g, int x, int y, int w, int h, int topBg, int bottomBg, int borderColor) {
        // 1. Yumuşak Dış Gölge (2 katmanlı)
        g.fill(x - 2, y - 1, x + w + 2, y + h + 2, 0x22000000);
        g.fill(x - 1, y, x + w + 1, y + h + 1, 0x44000000);

        // 2. Yuvarlatılmış/Pahlı Ana Gövde (Gradient geçişli)
        g.fillGradient(x + 2, y + 1, x + w - 2, y + h - 1, topBg, bottomBg);
        g.fillGradient(x + 1, y + 2, x + w - 1, y + h - 2, topBg, bottomBg);

        // 3. Yumuşak Köşe Çerçeveleri (Anti-aliased köşe efekti)
        g.fill(x + 2, y, x + w - 2, y + 1, borderColor);         // Üst kenar
        g.fill(x + 2, y + h - 1, x + w - 2, y + h, borderColor); // Alt kenar
        g.fill(x, y + 2, x + 1, y + h - 2, borderColor);         // Sol kenar
        g.fill(x + w - 1, y + 2, x + w, y + h - 2, borderColor); // Sağ kenar

        // Köşe yumuşatma pikselleri
        g.fill(x + 1, y + 1, x + 2, y + 2, borderColor);
        g.fill(x + w - 2, y + 1, x + w - 1, y + 2, borderColor);
        g.fill(x + 1, y + h - 2, x + 2, y + h - 1, borderColor);
        g.fill(x + w - 2, y + h - 2, x + w - 1, y + h - 1, borderColor);
    }

    // Üst kısmı neon parlamalı standart HUD arka planı
    public static void drawHudPanel(GuiGraphics g, int x, int y, int w, int h, int accentColor) {
        // Koyu lacivert-mor Argon cam geçişi
        drawArgonBox(g, x, y, w, h, 0xE611111B, 0xE60A0A10, 0xFF262638);
        // Üst neon vurgu çizgisi
        g.fill(x + 3, y, x + w - 3, y + 2, accentColor);
        // Üst iç hafif parlama (Glow)
        g.fillGradient(x + 2, y + 2, x + w - 2, y + 8, (accentColor & 0x00FFFFFF) | 0x28000000, 0x00000000);
    }
}
