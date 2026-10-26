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
        super(Component.literal("VqexDonut ClickGUI"));
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        g.fillGradient(0, 0, this.width, this.height, 0x9909090E, 0xCC160C24);

        int startX = 35;
        int startY = 35;
        int panelW = 130;

        for (Category cat : Category.values()) {
            List<Module> mods = ModuleManager.getModulesByCategory(cat);
            int totalH = 24 + (mods.size() * 20) + 4;

            RenderUtils.drawHudBox(g, startX, startY, panelW, totalH, 0xFFA855F7);

            g.drawString(this.font, "§l" + cat.name, startX + 8, startY + 7, 0xFFFFFFFF, true);
            g.fill(startX + 4, startY + 20, startX + panelW - 4, startY + 21, 0x33FFFFFF);

            int modY = startY + 24;
            for (Module m : mods) {
                boolean hover = mouseX >= startX + 4 && mouseX <= startX + panelW - 4 && mouseY >= modY && mouseY <= modY + 18;

                int modBg = m.isEnabled() ? 0xFF3B1D5E : (hover ? 0xFF1E1E2A : 0xFF14141D);
                int modBorder = m.isEnabled() ? 0xFFA855F7 : (hover ? 0xFF38384A : 0xFF1F1F2E);

                RenderUtils.drawCustomBox(g, startX + 4, modY, panelW - 8, 18, modBg, modBorder);

                if (m.isEnabled()) {
                    g.fill(startX + 6, modY + 3, startX + 8, modY + 15, 0xFFD8B4FE);
                }

                String label = m.isBinding() ? "§eListening..." : m.getName();
                g.drawString(this.font, label, startX + 12, modY + 5, m.isEnabled() ? 0xFFFFFFFF : 0xFF94A3B8, true);

                if (!m.isBinding() && m.getKeybind() > 0) {
                    String k = m.getKeyName();
                    int kw = this.font.width(k);
                    g.drawString(this.font, k, startX + panelW - kw - 10, modY + 5, 0xFF64748B, true);
                }

                modY += 20;
            }

            startX += panelW + 14;
        }

        String tip = "§7Sol Tik: §fAc/Kapat  §8|  §7Sag Tik: §fTus Ata (ESC Sifirlar)";
        int tw = this.font.width(tip);
        g.drawString(this.font, tip, (this.width - tw) / 2, this.height - 18, 0xFFFFFFFF, true);

        super.render(g, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int startX = 35;
        int startY = 35;
        int panelW = 130;

        for (Category cat : Category.values()) {
            List<Module> mods = ModuleManager.getModulesByCategory(cat);
            int modY = startY + 24;

            for (Module m : mods) {
                if (mouseX >= startX + 4 && mouseX <= startX + panelW - 4 && mouseY >= modY && mouseY <= modY + 18) {
                    if (button == 0) {
                        m.toggle();
                    } else if (button == 1) {
                        for (Module other : ModuleManager.modules) other.setBinding(false);
                        m.setBinding(true);
                    }
                    return true;
                }
                modY += 20;
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
