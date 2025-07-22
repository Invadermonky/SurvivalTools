package com.invadermonky.survivaltools.inventory.handler;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

public class FluidContainerRangedWrapper extends FilteredStackRangedWrapper {
    public FluidContainerRangedWrapper(IItemHandlerModifiable compose, int minSlot, int maxSlotExclusive) {
        super(compose, minSlot, maxSlotExclusive, stack -> stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null));
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        ItemStack stack = super.extractItem(slot, amount, true);
        return this.isFluidContainerFull(stack) ? super.extractItem(slot, amount, simulate) : ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    public @NotNull ItemStack extractInternal(int slot, int amount, boolean simulate) {
        return super.extractItem(slot, amount, simulate);
    }

    public boolean isFluidContainerFull(ItemStack stack) {
        IFluidHandlerItem handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
        if (handler != null) {
            IFluidTankProperties props = handler.getTankProperties()[0];
            return props.getContents() != null && props.getContents().amount >= props.getCapacity();
        }
        return false;
    }
}
