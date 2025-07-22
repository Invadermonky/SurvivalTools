package com.invadermonky.survivaltools.client.gui;

import com.invadermonky.survivaltools.client.gui.base.AbstractGuiWaterPurifier;
import com.invadermonky.survivaltools.inventory.container.ContainerPoweredPurifier;
import com.invadermonky.survivaltools.tile.TilePoweredPurifier;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.ArrayList;
import java.util.List;

public class GuiPoweredPurifier extends AbstractGuiWaterPurifier<TilePoweredPurifier, ContainerPoweredPurifier> {
    public GuiPoweredPurifier(ContainerPoweredPurifier inventoryContainer) {
        super(inventoryContainer);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        List<String> tooltip = new ArrayList<>();
        this.handleGuiEnergy(this.tilePurifier, i + 18, j + 20, 14, 42, mouseX, mouseY, tooltip);

        if (!tooltip.isEmpty()) {
            this.drawHoveringTooltip(mouseX, mouseY, tooltip);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundElements(float partialTicks, int mouseX, int mouseY) {
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;

        //Drawing RF power bar
        this.drawTexturedModalRect(i + 18, j + 20, 176, 31, 14, 42);

        if (this.tilePurifier.getEnergyStored() > 0) {
            int k = this.getPowerBarScaled(42);
            this.drawTexturedModalRect(i + 18, j + 20 + (42 - k), 190, 31 + (42 - k), 14, k);
        }
    }

    public void handleGuiEnergy(IEnergyStorage storage, int x, int y, int w, int h, int mouseX, int mouseY, List<String> tooltip) {
        if (tooltip != null) {
            if (mouseX >= x && mouseY >= y && mouseX <= x + w && mouseY <= y + h) {
                addEnergyTooltip(storage, tooltip);
            }
        }
    }

    public void addEnergyTooltip(IEnergyStorage storage, List<String> tooltip) {
        if (storage != null) {
            tooltip.add(String.format("%d/%d RF", storage.getEnergyStored(), storage.getMaxEnergyStored()));
        } else {
            tooltip.add("0 RF");
        }
    }

    private int getPowerBarScaled(int pixels) {
        int energy = this.tilePurifier.getEnergyStored();
        int capacity = this.tilePurifier.getMaxEnergyStored();
        if (capacity <= 0) {
            capacity = 10000;
        }
        return Math.min(pixels, pixels * energy / capacity);
    }
}
