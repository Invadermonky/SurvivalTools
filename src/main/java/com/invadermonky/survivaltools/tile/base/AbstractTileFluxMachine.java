package com.invadermonky.survivaltools.tile.base;

import com.invadermonky.survivaltools.api.blocks.RedstoneMode;
import com.invadermonky.survivaltools.util.libs.LibTags;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public abstract class AbstractTileFluxMachine extends TileEntity implements ITickable, IEnergyStorage {
    protected int energyStored;
    protected int energyMaxCapacity;
    protected RedstoneMode redstoneMode;

    public AbstractTileFluxMachine(int energyMaxCapacity) {
        this.energyStored = 0;
        this.energyMaxCapacity = energyMaxCapacity;
    }

    public RedstoneMode getRedstoneMode() {
        return this.redstoneMode;
    }

    public void nextRedstoneMode() {
        this.redstoneMode = this.redstoneMode.next();
        this.markDirty();
    }

    public void previousRedstoneMode() {
        this.redstoneMode = this.redstoneMode.previous();
        this.markDirty();
    }

    public boolean isRunning() {
        if (this.getEnergyStored() < this.getEnergyCost())
            return false;

        switch (redstoneMode) {
            case INVERTED:
                return !this.world.isBlockPowered(this.pos);
            case IGNORED:
                return true;
            case DISABLED:
                return false;
            default:
                return this.world.isBlockPowered(this.pos);
        }
    }

    public boolean tickEnergy() {
        boolean did = false;
        if (!this.world.isRemote) {
            if (this.getEnergyStored() > this.getMaxEnergyStored()) {
                this.energyStored = this.getMaxEnergyStored();
                did = true;
            }
            if (!this.world.isRemote && this.isRunning()) {
                this.energyStored -= this.getEnergyCost();
                did = true;
            }
        }
        return did;
    }

    /*
     *  TileEntity
     */

    @Override
    public void readFromNBT(@NotNull NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.energyStored = compound.getInteger(LibTags.TAG_ENERGY);
        this.redstoneMode = RedstoneMode.values()[compound.getInteger(LibTags.TAG_REDSTONE)];
    }

    @Override
    public @NotNull NBTTagCompound writeToNBT(@NotNull NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger(LibTags.TAG_ENERGY, this.energyStored);
        compound.setInteger(LibTags.TAG_REDSTONE, this.redstoneMode.ordinal());
        return compound;
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (this.world != null) {
            IBlockState state = this.world.getBlockState(this.pos);
            if (state != null) {
                this.world.notifyBlockUpdate(this.pos, state, state, 3);
            }
        }
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, 0, this.getUpdateTag());
    }

    @Override
    public @NotNull NBTTagCompound getUpdateTag() {
        return this.writeToNBT(super.getUpdateTag());
    }

    @Override
    public void onDataPacket(@NotNull NetworkManager net, SPacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public boolean shouldRefresh(@NotNull World world, @NotNull BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }

    @Override
    public boolean hasCapability(@NotNull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityEnergy.ENERGY;
    }

    @Nullable
    @Override
    public <T> T getCapability(@NotNull Capability<T> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityEnergy.ENERGY ? CapabilityEnergy.ENERGY.cast(this) : null;
    }

    /*
     *  IEnergyStorage
     */
    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int energyReceived = Math.min(this.getMaxEnergyStored() - this.getEnergyStored(), Math.min(this.getMaxTransfer(), maxReceive));
        if (!simulate) {
            this.energyStored += energyReceived;
            this.markDirty();
        }
        return energyReceived;
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
    public abstract int getMaxEnergyStored();

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return this.getEnergyStored() < this.getMaxEnergyStored();
    }

    public abstract int getMaxTransfer();

    public abstract int getEnergyCost();


}
