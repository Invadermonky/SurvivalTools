package com.invadermonky.survivaltools.api.fluid;

import com.invadermonky.survivaltools.api.energy.EnergyContainerItemWrapper;
import com.invadermonky.survivaltools.api.energy.IEnergyContainerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FluidEnergyContainerItemWrapper extends EnergyContainerItemWrapper {
    protected final IFluidContainerItem fluidContainer;
    protected final IFluidHandlerItem fluidHandler;

    public FluidEnergyContainerItemWrapper(ItemStack stack, IEnergyContainerItem energyContainer, IFluidContainerItem fluidContainer) {
        super(stack, energyContainer);
        this.fluidContainer = fluidContainer;
        this.fluidHandler = new IFluidHandlerItem() {
            @Nonnull
            @Override
            public ItemStack getContainer() {
                return FluidEnergyContainerItemWrapper.this.fluidContainer.getContainer(FluidEnergyContainerItemWrapper.this.stack);
            }

            @Override
            public IFluidTankProperties[] getTankProperties() {
                return FluidEnergyContainerItemWrapper.this.fluidContainer.getTankProperties(FluidEnergyContainerItemWrapper.this.stack);
            }

            @Override
            public int fill(FluidStack resource, boolean doFill) {
                return FluidEnergyContainerItemWrapper.this.fluidContainer.fill(FluidEnergyContainerItemWrapper.this.stack, resource, doFill);
            }

            @Nullable
            @Override
            public FluidStack drain(FluidStack resource, boolean doDrain) {
                return FluidEnergyContainerItemWrapper.this.fluidContainer.drain(FluidEnergyContainerItemWrapper.this.stack, resource, doDrain);
            }

            @Nullable
            @Override
            public FluidStack drain(int maxDrain, boolean doDrain) {
                return FluidEnergyContainerItemWrapper.this.fluidContainer.drain(FluidEnergyContainerItemWrapper.this.stack, maxDrain, doDrain);
            }
        };
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return super.hasCapability(capability, facing) || capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (this.hasCapability(capability, facing)) {
            return capability == CapabilityEnergy.ENERGY ? super.getCapability(capability, facing) : CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.cast(this.fluidHandler);
        }
        return null;
    }
}
