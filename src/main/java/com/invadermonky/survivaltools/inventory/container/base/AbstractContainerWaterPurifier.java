package com.invadermonky.survivaltools.inventory.container.base;

import com.invadermonky.survivaltools.api.tiles.AbstractTileWaterPurifier;
import com.invadermonky.survivaltools.crafting.purifier.PurifierRecipeRegistry;
import com.invadermonky.survivaltools.inventory.handler.PurifierStackHandler;
import com.invadermonky.survivaltools.inventory.slot.SlotItemHandlerOutput;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractContainerWaterPurifier<T extends AbstractTileWaterPurifier> extends Container {
    private static final Map<String, Integer> fieldIds = new HashMap<>();

    protected final InventoryPlayer inventoryPlayer;
    protected final T tilePurifier;

    private int progress;
    private int maxProgress;
    private int inputFluidAmount;
    private int outputFluidAmount;

    public AbstractContainerWaterPurifier(InventoryPlayer inventoryPlayer, T tilePurifier) {
        this.inventoryPlayer = inventoryPlayer;
        this.tilePurifier = tilePurifier;

        this.bindPlayerInventory();
        this.bindTileInventory();
        this.registerFields();
        this.registerAdditionalFields();
    }

    protected static void registerFieldId(String fieldName, int id) throws IllegalArgumentException {
        if (fieldIds.containsKey(fieldName) && fieldIds.get(fieldName) == id) {
            return;
        } else if (fieldIds.containsValue(id)) {
            throw new IllegalArgumentException("Field id " + id + " already registered: " + fieldName);
        }

        fieldIds.put(fieldName, id);
    }

    protected static int getFieldId(String fieldName) throws IllegalArgumentException {
        if (!fieldIds.containsKey(fieldName)) {
            throw new IllegalArgumentException("No registered field: " + fieldName);
        }
        return fieldIds.get(fieldName);
    }

    public T getPurifierTile() {
        return this.tilePurifier;
    }

    protected void registerFields() {
        registerFieldId("progress", 0);
        registerFieldId("maxProgress", 1);
        registerFieldId("inputTankAmount", 2);
        registerFieldId("outputTankAmount", 3);
    }

    protected abstract void registerAdditionalFields();

    protected void bindTileInventory() {
        this.addSlotToContainer(new SlotItemHandler(this.tilePurifier.getItemHandler(), PurifierStackHandler.SLOT_FILTER_INGREDIENT, 93, 44));
        this.addSlotToContainer(new SlotItemHandler(this.tilePurifier.getItemHandler(), PurifierStackHandler.SLOT_INPUT_TANK_IN, 62, 16));
        this.addSlotToContainer(new SlotItemHandlerOutput(this.tilePurifier.getItemHandler(), PurifierStackHandler.SLOT_INPUT_TANK_OUT, 62, 50));
        this.addSlotToContainer(new SlotItemHandler(this.tilePurifier.getItemHandler(), PurifierStackHandler.SLOT_OUTPUT_TANK_IN, 150, 16));
        this.addSlotToContainer(new SlotItemHandlerOutput(this.tilePurifier.getItemHandler(), PurifierStackHandler.SLOT_OUTPUT_TANK_OUT, 150, 50));
    }

    protected void bindPlayerInventory() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlotToContainer(new Slot(this.inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.addSlotToContainer(new Slot(this.inventoryPlayer, k, 8 + k * 18, 142));
        }
    }

    @Override
    public void addListener(@NotNull IContainerListener listener) {
        super.addListener(listener);
        listener.sendWindowProperty(this, getFieldId("progress"), this.tilePurifier.getProgress());
        listener.sendWindowProperty(this, getFieldId("maxProgress"), this.tilePurifier.getMaxProgress());
        listener.sendWindowProperty(this, getFieldId("inputTankAmount"), this.tilePurifier.getInputFluidTank().getFluidAmount());
        listener.sendWindowProperty(this, getFieldId("outputTankAmount"), this.tilePurifier.getOutputFluidTank().getFluidAmount());
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (IContainerListener listener : this.listeners) {
            if (this.progress != this.tilePurifier.getProgress()) {
                listener.sendWindowProperty(this, getFieldId("progress"), this.tilePurifier.getProgress());
            }

            if (this.maxProgress != this.tilePurifier.getProgress()) {
                listener.sendWindowProperty(this, getFieldId("maxProgress"), this.tilePurifier.getMaxProgress());
            }

            if (this.inputFluidAmount != this.tilePurifier.getInputFluidTank().getFluidAmount()) {
                listener.sendWindowProperty(this, getFieldId("inputTankAmount"), this.tilePurifier.getInputFluidTank().getFluidAmount());
            }

            if (this.outputFluidAmount != this.tilePurifier.getOutputFluidTank().getFluidAmount()) {
                listener.sendWindowProperty(this, getFieldId("outputTankAmount"), this.tilePurifier.getOutputFluidTank().getFluidAmount());
            }
        }

        this.progress = this.tilePurifier.getProgress();
        this.maxProgress = this.tilePurifier.getMaxProgress();
        this.inputFluidAmount = this.tilePurifier.getInputFluidTank().getFluidAmount();
        this.outputFluidAmount = this.tilePurifier.getOutputFluidTank().getFluidAmount();
    }

    @Override
    public @NotNull ItemStack transferStackInSlot(@NotNull EntityPlayer playerIn, int slotIndex) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(slotIndex);
        if (slot != null && slot.getHasStack()) {
            ItemStack stackInSlot = slot.getStack();
            stack = stackInSlot.copy();
            if (!this.performMerge(slotIndex, stackInSlot)) {
                return ItemStack.EMPTY;
            }
            slot.onSlotChange(stackInSlot, stack);
            if (stackInSlot.getCount() <= 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.putStack(stackInSlot);
            }

            if (stackInSlot.getCount() == stack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, stackInSlot);
        }
        return stack;
    }

    @Override
    public void updateProgressBar(int id, int data) {
        if (id == getFieldId("progress")) {
            this.progress = data;
            this.tilePurifier.setProgress(data);
        } else if (id == getFieldId("maxProgress")) {
            this.maxProgress = data;
            this.tilePurifier.setMaxProgress(data);
        } else if (id == getFieldId("inputTankAmount")) {
            this.inputFluidAmount = data;
            this.tilePurifier.setInputFluidAmount(data);
        } else if (id == getFieldId("outputTankAmount")) {
            this.outputFluidAmount = data;
            this.tilePurifier.setOutputFluidAmount(data);
        }
    }

    @Override
    public boolean canInteractWith(@NotNull EntityPlayer playerIn) {
        return true;
    }

    protected boolean performMerge(int slotIndex, ItemStack stack) {
        int invPlayer = 27;
        int invFull = invPlayer + 9;
        int invPurifier = invFull + this.tilePurifier.getItemHandler().getSlots();
        if(slotIndex >= invFull) {
            return this.mergeItemStack(stack, 0, invFull, true);
        } else {
            if(PurifierRecipeRegistry.isValidFilterItem(stack)) {
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

    public InventoryPlayer getInventoryPlayer() {
        return inventoryPlayer;
    }
}
