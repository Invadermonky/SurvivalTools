package com.invadermonky.survivaltools.api.energy;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EnergyContainerItemWrapper implements ICapabilityProvider {
    protected final ItemStack stack;
    protected final IEnergyContainerItem energyContainer;
    protected final boolean canExtract;
    protected final boolean canReceive;
    protected final IEnergyStorage energyCapacity;

    public EnergyContainerItemWrapper(ItemStack stack, IEnergyContainerItem container, boolean canExtract, boolean canReceive) {
        this.stack = stack;
        this.energyContainer = container;
        this.canExtract = canExtract;
        this.canReceive = canReceive;
        this.energyCapacity = new IEnergyStorage() {
            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                return EnergyContainerItemWrapper.this.energyContainer.receiveEnergy(EnergyContainerItemWrapper.this.stack, maxReceive, simulate);
            }

            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
                return EnergyContainerItemWrapper.this.energyContainer.extractEnergy(EnergyContainerItemWrapper.this.stack, maxExtract, simulate);
            }

            @Override
            public int getEnergyStored() {
                return EnergyContainerItemWrapper.this.energyContainer.getEnergyStored(EnergyContainerItemWrapper.this.stack);
            }

            @Override
            public int getMaxEnergyStored() {
                return EnergyContainerItemWrapper.this.energyContainer.getMaxEnergyStored(EnergyContainerItemWrapper.this.stack);
            }

            @Override
            public boolean canExtract() {
                return EnergyContainerItemWrapper.this.canExtract;
            }

            @Override
            public boolean canReceive() {
                return EnergyContainerItemWrapper.this.canReceive;
            }
        };
    }

    public EnergyContainerItemWrapper(ItemStack stack, IEnergyContainerItem container) {
        this(stack, container, true, true);
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityEnergy.ENERGY;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return !this.hasCapability(capability, facing) ? null : CapabilityEnergy.ENERGY.cast(this.energyCapacity);
    }
}
