package com.invadermonky.survivaltools.client.gui.button;

import com.invadermonky.survivaltools.client.gui.base.AbstractGuiWaterPurifier;
import com.invadermonky.survivaltools.tile.base.AbstractTileWaterPurifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class GuiButtonGuiButtonConsumer<T extends AbstractTileWaterPurifier> extends GuiButton {
    private final Consumer<T> consumer;
    private final T tilePurifier;

    public GuiButtonGuiButtonConsumer(int buttonId, int x, int y, T tilePurifier, Consumer<T> consumer) {
        super(buttonId, x, y, 9, 9, "");
        this.tilePurifier = tilePurifier;
        this.consumer = consumer;
    }

    @Override
    public void drawButton(@NotNull Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            this.hovered = ((mouseX >= this.x) && (mouseY >= this.y) && (mouseX < this.x + this.width) && (mouseY < this.y + this.height));
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            mc.renderEngine.bindTexture(AbstractGuiWaterPurifier.TEXTURE);
            GlStateManager.pushMatrix();
            if (this.hovered) {
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            } else {
                GlStateManager.color(0.8F, 0.8F, 0.8F, 1.0F);
            }
            this.drawTexturedModalRect(this.x, this.y, 176, 106, 9, 9);
            GlStateManager.popMatrix();
            this.mouseDragged(mc, mouseX, mouseY);
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        this.consumer.accept(this.tilePurifier);
    }
}
