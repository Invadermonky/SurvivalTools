package com.invadermonky.survivaltools.inventory.slot;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class SlotFilteredItemHandler extends SlotItemHandler {
    private final Function<ItemStack, Boolean> slotFunction;

    public SlotFilteredItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition, Function<ItemStack, Boolean> slotFunction) {
        super(itemHandler, index, xPosition, yPosition);
        this.slotFunction = slotFunction;
    }

    @Override
    public boolean isItemValid(@NotNull ItemStack stack) {
        return this.slotFunction.apply(stack) && super.isItemValid(stack);
    }
}
