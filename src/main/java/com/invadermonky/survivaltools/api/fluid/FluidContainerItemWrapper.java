package com.invadermonky.survivaltools.api.fluid;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FluidContainerItemWrapper implements IFluidHandlerItem, ICapabilityProvider {
    protected final ItemStack stack;
    protected final IFluidContainerItem fluidContainer;

    public FluidContainerItemWrapper(ItemStack stack, IFluidContainerItem fluidContainer) {
        this.stack = stack;
        this.fluidContainer = fluidContainer;
    }

    @Nonnull
    @Override
    public ItemStack getContainer() {
        return this.fluidContainer.getContainer(this.stack);
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
        return this.fluidContainer.getTankProperties(this.stack);
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        return this.fluidContainer.fill(this.stack, resource, doFill);
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        return this.fluidContainer.drain(this.stack, resource, doDrain);
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        return this.fluidContainer.drain(this.stack, maxDrain, doDrain);
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return !this.hasCapability(capability, facing) ? null : CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.cast(this);
    }
}
