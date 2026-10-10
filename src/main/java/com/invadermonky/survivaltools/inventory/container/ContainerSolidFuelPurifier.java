package com.invadermonky.survivaltools.inventory.container;

import com.invadermonky.survivaltools.compat.survivaltools.tiles.TileSolidFuelPurifier;
import com.invadermonky.survivaltools.crafting.purifier.PurifierRecipeRegistry;
import com.invadermonky.survivaltools.inventory.container.base.AbstractContainerWaterPurifier;
import com.invadermonky.survivaltools.inventory.handler.PurifierStackHandler;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.items.SlotItemHandler;
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
        super.bindTileInventory();
        this.addSlotToContainer(new SlotItemHandler(tilePurifier.getItemHandler(), TileSolidFuelPurifier.SLOT_BURNABLE_FUEL, 10, 39));
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
                this.burnTime = this.tilePurifier.getBurnTime();
            }
            if (this.burnTimeMax != this.tilePurifier.getBurnTimeMax()) {
                listener.sendWindowProperty(this, getFieldId("burnTimeMax"), this.tilePurifier.getBurnTimeMax());
                this.burnTimeMax = this.tilePurifier.getBurnTimeMax();
            }
        }
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

    @Override
    protected boolean performMerge(int slotIndex, ItemStack stack) {
        int invPlayer = 27;
        int invFull = invPlayer + 9;
        int invPurifier = invFull + this.tilePurifier.getItemHandler().getSlots();
        if(slotIndex >= invFull) {
            return this.mergeItemStack(stack, 0, invFull, true);
        } else {
            if(TileEntityFurnace.isItemFuel(stack)) {
                int insertSlot = invFull + TileSolidFuelPurifier.SLOT_BURNABLE_FUEL;
                return this.mergeItemStack(stack, insertSlot, insertSlot + 1, false);
            } else if(PurifierRecipeRegistry.isValidFilterItem(stack)) {
                int insertSlot = invFull + PurifierStackHandler.SLOT_FILTER_INGREDIENT;
                return this.mergeItemStack(stack, insertSlot, insertSlot + 1, false);
            } else if(PurifierStackHandler.isValidInputTankItem(stack)) {
                int insertSlot = invFull + PurifierStackHandler.SLOT_INPUT_TANK_IN;
                return this.mergeItemStack(stack, insertSlot, insertSlot + 1, false);
            } else if(PurifierStackHandler.isValidOutputTankItem(stack)) {
                int insertSlot = invFull + PurifierStackHandler.SLOT_OUTPUT_TANK_IN;
                return this.mergeItemStack(stack, insertSlot, insertSlot + 1, false);
            } else if(slotIndex > invPlayer) {
                return this.mergeItemStack(stack, 0, invPlayer, false);
            } else {
                return this.mergeItemStack(stack, invPlayer, invFull, false);
            }
        }
    }
}
