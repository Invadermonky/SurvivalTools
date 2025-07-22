package com.invadermonky.survivaltools.inventory.slot;

import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.IItemHandler;

public class SlotFilteredFluidHandler extends SlotFilteredItemHandler {
    public SlotFilteredFluidHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition, stack -> stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null));
    }

    @Override
    public int getSlotStackLimit() {
        return 1;
    }
}
