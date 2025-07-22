package com.invadermonky.survivaltools.inventory.handler;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.RangedWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class FilteredStackRangedWrapper extends RangedWrapper {
    protected Function<ItemStack, Boolean> filterFunction;

    public FilteredStackRangedWrapper(IItemHandlerModifiable compose, int minSlot, int maxSlotExclusive, Function<ItemStack, Boolean> filterFunction) {
        super(compose, minSlot, maxSlotExclusive);
        this.filterFunction = filterFunction;
    }

    public void setFilterFunction(Function<ItemStack, Boolean> function) {
        this.filterFunction = function;
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return this.isItemValid(slot, stack) ? super.insertItem(slot, stack, simulate) : stack;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return filterFunction.apply(stack);
    }

    public @NotNull ItemStack insertInternal(int slot, @NotNull ItemStack stack, boolean simulate) {
        return super.insertItem(slot, stack, simulate);
    }
}
