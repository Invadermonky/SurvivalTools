package com.invadermonky.survivaltools.inventory.container;

import com.invadermonky.survivaltools.inventory.container.base.AbstractContainerWaterPurifier;
import com.invadermonky.survivaltools.inventory.slot.SlotFilteredItemHandler;
import com.invadermonky.survivaltools.tile.TileSolidFuelPurifier;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.tileentity.TileEntityFurnace;
import org.jetbrains.annotations.NotNull;

public class ContainerSolidFuelPurifier extends AbstractContainerWaterPurifier<TileSolidFuelPurifier> {
    private int burnTime;
    private int burnTimeMax;

    public ContainerSolidFuelPurifier(InventoryPlayer inventoryPlayer, TileSolidFuelPurifier tilePurifier) {
        super(inventoryPlayer, tilePurifier);
    }

    @Override
    protected void registerAdditionalFields() {
        registerFieldId("burnTime", 4);
        registerFieldId("burnTimeMax", 5);
    }

    @Override
    protected void bindTileInventory() {
        this.addSlotToContainer(new SlotFilteredItemHandler(this.tilePurifier.getItemHandler(), 2, 17, 39, TileEntityFurnace::isItemFuel));
    }

    @Override
    public void addListener(@NotNull IContainerListener listener) {
        super.addListener(listener);
        listener.sendWindowProperty(this, getFieldId("burnTime"), this.tilePurifier.getBurnTime());
        listener.sendWindowProperty(this, getFieldId("burnTimeMax"), this.tilePurifier.getBurnTimeMax());
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (IContainerListener listener : this.listeners) {
            if (this.burnTime != this.tilePurifier.getBurnTime()) {
                listener.sendWindowProperty(this, getFieldId("burnTime"), this.tilePurifier.getBurnTime());
            }
            if (this.burnTimeMax != this.tilePurifier.getBurnTimeMax()) {
                listener.sendWindowProperty(this, getFieldId("burnTimeMax"), this.tilePurifier.getBurnTimeMax());
            }
        }

        this.burnTime = this.tilePurifier.getBurnTime();
        this.burnTimeMax = this.tilePurifier.getBurnTimeMax();
    }

    @Override
    public void updateProgressBar(int id, int data) {
        super.updateProgressBar(id, data);
        if (id == getFieldId("burnTime")) {
            this.tilePurifier.setBurnTime(data);
        } else if (id == getFieldId("burnTimeMax")) {
            this.tilePurifier.setBurnTimeMax(data);
        }
    }
}
