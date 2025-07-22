package com.invadermonky.survivaltools.tile;

import com.invadermonky.survivaltools.api.blocks.RedstoneMode;
import com.invadermonky.survivaltools.config.ConfigHandlerST;
import com.invadermonky.survivaltools.tile.base.AbstractTileWaterPurifier;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TilePoweredPurifier extends AbstractTileWaterPurifier implements IEnergyStorage {
    public static final int MAX_CAPACITY = 20000;

    protected RedstoneMode redstoneMode;
    protected int energyStored;

    public TilePoweredPurifier() {
        super();
        this.redstoneMode = RedstoneMode.REQUIRED;
        this.energyStored = 0;
    }

    @Override
    public void update() {
        if (this.isRunning()) {
            super.update();
        } else if (!this.world.isRemote) {
            this.setEnabled(this.isRunning());
            if (this.transferFluidToContainer()) {
                this.markDirty();
            }
        }
    }

    @Override
    public boolean onTileUpdate() {
        return false;
    }

    @Override
    public boolean isRunning() {
        switch (this.redstoneMode) {
            case REQUIRED:
                return this.world.isBlockPowered(this.pos);
            case INVERTED:
                return !this.world.isBlockPowered(this.pos);
            case DISABLED:
                return false;
            case IGNORED:
            default:
                return true;
        }
    }

    @Override
    public void readFromTileNBT(NBTTagCompound compound) {
        this.redstoneMode = RedstoneMode.values()[compound.getInteger("redstone")];
        this.energyStored = compound.getInteger("energy");
    }

    @Override
    public @NotNull NBTTagCompound writeToTileNBT(NBTTagCompound compound) {
        compound.setInteger("redstone", this.redstoneMode.ordinal());
        compound.setInteger("energy", this.energyStored);
        return compound;
    }

    @Override
    public boolean hasTileCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityEnergy.ENERGY;
    }

    @Override
    public <T> @Nullable T getTileCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        return hasTileCapability(capability, facing) ? CapabilityEnergy.ENERGY.cast(this) : null;
    }

    @Override
    public boolean hasFuel() {
        return this.getEnergyStored() >= this.getEnergyCost();
    }

    @Override
    public boolean consumeFuelPassive() {
        return false;
    }

    @Override
    public void consumeFuelActive() {
        if (this.canOutputFluid() && this.getEnergyStored() >= this.getEnergyCost()) {
            this.energyStored -= this.getEnergyCost();
        }
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (maxReceive > 0) {
            int received = Math.min(Math.min(maxReceive, this.getMaxTransfer()), this.getMaxEnergyStored() - this.getEnergyStored());
            if (!simulate) {
                this.energyStored += received;
                this.markDirty();
            }
            return received;
        }
        return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return this.energyStored;
    }

    @Override
    public int getMaxEnergyStored() {
        return MAX_CAPACITY;
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return this.getEnergyStored() < this.getMaxEnergyStored();
    }

    public int getEnergyCost() {
        return ConfigHandlerST.machines.powered_purifier.energyCost;
    }

    public int getMaxTransfer() {
        return Math.max(512, this.getEnergyCost());
    }

    public RedstoneMode getRedstoneMode() {
        return this.redstoneMode;
    }

    public void nextRedstoneMode() {
        this.redstoneMode = this.redstoneMode.next();
    }

    public void previousRedstoneMode() {
        this.redstoneMode = this.redstoneMode.previous();
    }

}
