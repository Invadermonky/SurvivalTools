package com.invadermonky.survivaltools.client.gui;

import com.invadermonky.survivaltools.inventory.ContainerBarrelHeater;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class GuiBarrelHeater extends GuiContainer {
    private static final ResourceLocation HEATER_GUI_TEXTURES = new ResourceLocation("survivaltools:textures/gui/barrel_heater.png");
    private final InventoryPlayer playerInventory;
    private final IInventory heaterInventory;

    public GuiBarrelHeater(InventoryPlayer playerInventory, IInventory heaterInventory) {
        super(new ContainerBarrelHeater(playerInventory, heaterInventory));
        this.playerInventory = playerInventory;
        this.heaterInventory = heaterInventory;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String s = this.heaterInventory.getDisplayName().getUnformattedText();
        this.fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 6, 4210752);
        this.fontRenderer.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.getTextureManager().bindTexture(HEATER_GUI_TEXTURES);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);

        if(this.heaterInventory.getField(0 ) > 0) {
            int k = this.getBurnLeftScaled(13);
            this.drawTexturedModalRect(i + 80, j + 34 - k, 176, 12 - k, 14, k + 1);
        }
    }

    private int getBurnLeftScaled(int pixels) {
        int i = this.heaterInventory.getField(1);
        if(i == 0) {
            i = 200;
        }
        return this.heaterInventory.getField(0) * pixels / i;
    }
}
