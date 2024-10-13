package com.invadermonky.survivaltools.blocks.tile;

import com.invadermonky.survivaltools.api.blocks.RedstoneMode;
import com.invadermonky.survivaltools.config.ConfigHandlerST;
import com.invadermonky.survivaltools.util.helpers.SurvivalHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;

import static com.invadermonky.survivaltools.util.libs.LibTags.TAG_ENERGY;
import static com.invadermonky.survivaltools.util.libs.LibTags.TAG_REDSTONE;

public class TileCentralAirUnit extends TileEntity implements ITickable, IEnergyStorage {
    protected int energyStored;
    protected int energyMaxCapacity;
    protected int energyMaxReceive;
    protected int energyCost;
    protected int radius;
    protected RedstoneMode redstoneMode;

    public TileCentralAirUnit() {
        this.energyStored = 0;
        this.energyMaxCapacity = ConfigHandlerST.flux_tools.central_air_unit.energyCapacity;
        this.energyCost = ConfigHandlerST.flux_tools.central_air_unit.energyCost;
        this.energyMaxReceive = Math.max(2048, this.energyCost * 2);
        this.radius = ConfigHandlerST.flux_tools.central_air_unit.radius;
        this.redstoneMode = RedstoneMode.REQUIRED;
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

    public int getEnergyCost() {
        return this.energyCost;
    }

    public boolean isRunning() {
        if(this.getEnergyStored() < this.energyCost)
            return false;

        switch (redstoneMode) {
            case INVERTED:
                return !world.isBlockPowered(this.pos);
            case IGNORED:
                return true;
            default:
                return world.isBlockPowered(this.pos);
        }
    }

    @Override
    public void update() {
        if(!this.world.isRemote && this.isRunning()) {
            if (this.world.getTotalWorldTime() % 60 == 0) {
                BlockPos minPos = new BlockPos(this.getPos().getX() - this.radius, this.getPos().getY() - this.radius, this.getPos().getZ() - this.radius);
                BlockPos maxPos = new BlockPos(this.getPos().getX() + this.radius, this.getPos().getY() + this.radius, this.getPos().getZ() + this.radius);
                for (EntityPlayer player : this.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(minPos, maxPos))) {
                    SurvivalHelper.stabilizePlayerTemperature(player);
                }
            }
            this.energyStored -= this.energyCost;
            this.markDirty();
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.energyStored = compound.getInteger(TAG_ENERGY);
        this.redstoneMode = RedstoneMode.values()[compound.getInteger(TAG_REDSTONE)];
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger(TAG_ENERGY, this.energyStored);
        compound.setInteger(TAG_REDSTONE, this.redstoneMode.ordinal());
        return compound;
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if(this.world != null) {
            IBlockState state = this.world.getBlockState(this.pos);
            if(state != null) {
                this.world.notifyBlockUpdate(pos, state, state, 3);
            }
        }
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, 0, this.getUpdateTag());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return this.writeToNBT(super.getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityEnergy.ENERGY;
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityEnergy.ENERGY ? CapabilityEnergy.ENERGY.cast(this) : null;
    }

    /*
     *  IEnergyStorage
     */
    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int energyReceived = Math.min(this.getMaxEnergyStored() - this.getEnergyStored(), Math.min(this.energyMaxReceive, maxReceive));
        if(!simulate) {
            this.energyStored += energyReceived;
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
    public int getMaxEnergyStored() {
        return this.energyMaxCapacity;
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return this.getEnergyStored() < this.getMaxEnergyStored();
    }
}
