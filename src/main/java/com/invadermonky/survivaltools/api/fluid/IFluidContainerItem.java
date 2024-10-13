package com.invadermonky.survivaltools.api.fluid;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IFluidContainerItem {
    @Nonnull
    ItemStack getContainer(ItemStack stack);

    IFluidTankProperties[] getTankProperties(ItemStack stack);

    int fill(ItemStack stack, FluidStack resource, boolean doFill);

    @Nullable
    FluidStack drain(ItemStack stack, FluidStack resource, boolean doDrain);

    @Nullable
    FluidStack drain(ItemStack stack, int maxDrain, boolean doDrain);
}
