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

public class FluidContainerItemWrapper implements ICapabilityProvider {
    protected final ItemStack stack;
    protected final IFluidContainerItem fluidContainer;
    protected final IFluidHandlerItem fluidHandler;

    public FluidContainerItemWrapper(ItemStack stack, IFluidContainerItem fluidContainer) {
        this.stack = stack;
        this.fluidContainer = fluidContainer;
        this.fluidHandler = new IFluidHandlerItem() {
            @Nonnull
            @Override
            public ItemStack getContainer() {
                return FluidContainerItemWrapper.this.fluidContainer.getContainer(FluidContainerItemWrapper.this.stack);
            }

            @Override
            public IFluidTankProperties[] getTankProperties() {
                return FluidContainerItemWrapper.this.fluidContainer.getTankProperties(FluidContainerItemWrapper.this.stack);
            }

            @Override
            public int fill(FluidStack resource, boolean doFill) {
                return FluidContainerItemWrapper.this.fluidContainer.fill(FluidContainerItemWrapper.this.stack, resource, doFill);
            }

            @Nullable
            @Override
            public FluidStack drain(FluidStack resource, boolean doDrain) {
                return FluidContainerItemWrapper.this.fluidContainer.drain(FluidContainerItemWrapper.this.stack, resource, doDrain);
            }

            @Nullable
            @Override
            public FluidStack drain(int maxDrain, boolean doDrain) {
                return FluidContainerItemWrapper.this.fluidContainer.drain(FluidContainerItemWrapper.this.stack, maxDrain, doDrain);
            }
        };
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return !this.hasCapability(capability, facing) ? null : CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.cast(this.fluidHandler);
    }
}
