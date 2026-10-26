package com.vqexdonut.client.ui;

import com.vqexdonut.client.module.Category;
import com.vqexdonut.client.module.Module;
import com.vqexdonut.client.module.ModuleManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class ClickGuiScreen extends Screen {
    public ClickGuiScreen() {
        super(Component.literal("VqexDonut Argon GUI"));
    }

    // 1.21.4 BULANIKLIĞINI (BLUR) TAMAMEN KALDIRAN METOD!
    @Override
    public void renderBackground(GuiGraphics g, int mouseX, int mouseY, float delta) {
        // Burayı boş bırakıyoruz ki Minecraft'ın varsayılan blur efekti çalışmasın!
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        // Bulanıklık olmadan çok hafif şık koyu vignette geçişi
        g.fillGradient(0, 0, this.width, this.height, 0x3305050A, 0x660D0714);

        int startX = 40;
        int startY = 42;
        int panelW = 132;

        for (Category cat : Category.values()) {
            List<Module> mods = ModuleManager.getModulesByCategory(cat);
            int totalH = 26 + (mods.size() * 21) + 5;

            // Argon Kategori Kutusu (Yuvarlak Köşeli & Gradient)
            RenderUtils.drawHudPanel(g, startX, startY, panelW, totalH, 0xFFA855F7);

            // Kategori Başlığı ve Sağdaki Modül Sayısı
            g.drawString(this.font, "§d§l" + cat.name, startX + 9, startY + 8, 0xFFFFFFFF, true);
            String countStr = "§7" + mods.size();
            g.drawString(this.font, countStr, startX + panelW - this.font.width(countStr) - 8, startY + 8, 0xFFFFFFFF, true);
            g.fill(startX + 6, startY + 21, startX + panelW - 6, startY + 22, 0x28FFFFFF);

            int modY = startY + 26;
            for (Module m : mods) {
                boolean hover = mouseX >= startX + 5 && mouseX <= startX + panelW - 5 && mouseY >= modY && mouseY <= modY + 18;

                int topCol = m.isEnabled() ? 0xFF4C1D95 : (hover ? 0xFF232334 : 0xFF161622);
                int botCol = m.isEnabled() ? 0xFF2E1065 : (hover ? 0xFF1A1A28 : 0xFF11111A);
                int borderCol = m.isEnabled() ? 0xFFA855F7 : (hover ? 0xFF474761 : 0xFF222233);

                RenderUtils.drawArgonBox(g, startX + 5, modY, panelW - 10, 18, topCol, botCol, borderCol);

                // Sol aktiflik neon noktası
                int indicatorCol = m.isEnabled() ? 0xFFD8B4FE : 0xFF334155;
                g.fill(startX + 9, modY + 6, startX + 11, modY + 12, indicatorCol);

                String label = m.isBinding() ? "§ePress key..." : m.getName();
                g.drawString(this.font, label, startX + 16, modY + 5, m.isEnabled() ? 0xFFFFFFFF : 0xFF94A3B8, true);

                // Sağ tarafta tuş ataması
                if (!m.isBinding() && m.getKeybind() > 0) {
                    String k = "[" + m.getKeyName() + "]";
                    int kw = this.font.width(k);
                    g.drawString(this.font, k, startX + panelW - kw - 10, modY + 5, m.isEnabled() ? 0xFFD8B4FE : 0xFF64748B, true);
                }

                modY += 21;
            }

            startX += panelW + 14;
        }

        // Alt bilgi kutusu
        String tip = "§dSol Tik: §fAc/Kapat  §8•  §dSag Tik: §fTus Ata  §8•  §a.spotify Sarki - Sanatci";
        int tw = this.font.width(tip) + 16;
        int tx = (this.width - tw) / 2;
        RenderUtils.drawHudPanel(g, tx, this.height - 26, tw, 18, 0xFFA855F7);
        g.drawString(this.font, tip, tx + 8, this.height - 21, 0xFFFFFFFF, true);

        super.render(g, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int startX = 40;
        int startY = 42;
        int panelW = 132;

        for (Category cat : Category.values()) {
            List<Module> mods = ModuleManager.getModulesByCategory(cat);
            int modY = startY + 26;

            for (Module m : mods) {
                if (mouseX >= startX + 5 && mouseX <= startX + panelW - 5 && mouseY >= modY && mouseY <= modY + 18) {
                    if (button == 0) {
                        m.toggle();
                    } else if (button == 1) {
                        for (Module other : ModuleManager.modules) other.setBinding(false);
                        m.setBinding(true);
                    }
                    return true;
                }
                modY += 21;
            }
            startX += panelW + 14;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (Module m : ModuleManager.modules) {
            if (m.isBinding()) {
                if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_DELETE || keyCode == GLFW.GLFW_KEY_BACKSPACE) {
                    m.setKeybind(GLFW.GLFW_KEY_UNKNOWN);
                } else {
                    m.setKeybind(keyCode);
                }
                m.setBinding(false);
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
