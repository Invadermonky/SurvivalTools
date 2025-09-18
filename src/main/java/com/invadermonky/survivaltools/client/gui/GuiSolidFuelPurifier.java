package com.invadermonky.survivaltools.client.gui;

import com.invadermonky.survivaltools.client.gui.base.AbstractGuiWaterPurifier;
import com.invadermonky.survivaltools.compat.survivaltools.tiles.TileSolidFuelPurifier;
import com.invadermonky.survivaltools.inventory.container.ContainerSolidFuelPurifier;

public class GuiSolidFuelPurifier extends AbstractGuiWaterPurifier<TileSolidFuelPurifier, ContainerSolidFuelPurifier> {
    public GuiSolidFuelPurifier(ContainerSolidFuelPurifier inventoryContainer) {
        super(inventoryContainer);
    }

    @Override
    protected void drawGuiContainerBackgroundElements(float partialTicks, int mouseX, int mouseY) {
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;

        //Drawing fuel slot/burning icon
        this.drawTexturedModalRect(i + 16, j + 23, 176, 73, 18, 33);

        if (this.tilePurifier.isBurning()) {
            int k = this.getBurnLeftScaled(13);
            this.drawTexturedModalRect(i + 18, j + 35 - k, 176, 12 - k, 14, k + 1);
        }
    }

    private int getBurnLeftScaled(int pixels) {
        int burnMax = this.tilePurifier.getBurnTimeMax();
        if (burnMax <= 0) {
            burnMax = 200;
        }
        return pixels * this.tilePurifier.getBurnTime() / burnMax;
    }
}
