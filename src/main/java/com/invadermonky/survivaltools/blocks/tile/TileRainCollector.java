package com.invadermonky.survivaltools.blocks.tile;

import com.invadermonky.survivaltools.util.helpers.SurvivalHelper;
import com.invadermonky.survivaltools.util.libs.LibTags;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;

public class TileRainCollector extends TileEntity implements ITickable, IFluidHandler {
    protected Fluid fluid;
    protected int fluidAmount;
    protected int fluidMaxCapacity = 4000;

    public boolean canCollectRainwater() {
        return this.fluid == null || (SurvivalHelper.isPurifiedWater(this.fluid) && this.fluidAmount < this.fluidMaxCapacity);
    }

    public int getFluidAmount() {
        return this.fluidAmount;
    }

    public int getFluidMaxCapacity() {
        return this.fluidMaxCapacity;
    }

    @Nullable
    public Fluid getFluid() {
        return this.fluid;
    }

    @Nullable
    public FluidStack getContainedFluid() {
        return this.fluid == null || this.fluidAmount <= 0 ? null : new FluidStack(this.fluid, this.fluidAmount);
    }

    @Override
    public void update() {
        if(!this.world.isRemote) {
            if(this.world.getTotalWorldTime() % 20L == 0L && this.world.isRainingAt(this.pos.offset(EnumFacing.UP)) && this.canCollectRainwater()) {
                if(this.fluid == null) {
                    this.fluid = SurvivalHelper.getPurifiedWater();
                }
                //TODO: Config for how much fluid is collected by rain.
                this.fluidAmount += 15;
                this.markDirty();
            }
            //If raining and empty or contains purified water, fill with purified water.
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.fluid = FluidRegistry.getFluid(compound.getString(LibTags.TAG_FLUID));
        this.fluidAmount = compound.getInteger(LibTags.TAG_FLUID_AMOUNT);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setString(LibTags.TAG_FLUID, this.fluid != null ? this.fluid.getName() : "");
        compound.setInteger(LibTags.TAG_FLUID_AMOUNT, this.fluidAmount);
        return compound;
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if(this.world != null) {
            IBlockState state = this.world.getBlockState(this.pos);
            if(state != null) {
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
    public NBTTagCompound getUpdateTag() {
        return this.writeToNBT(super.getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ? CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(this) : null;
    }

    /*
     *  IFluidHandler
     */

    @Override
    public IFluidTankProperties[] getTankProperties() {
        return new IFluidTankProperties[] {new FluidTankProperties(this.getContainedFluid(), this.getFluidMaxCapacity())};
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        if(resource != null && (this.fluid == null || resource.getFluid() == this.fluid)) {
            if(this.fluid == null) {
                this.fluid = resource.getFluid();
            }
            int fillAmount = Math.min(this.fluidMaxCapacity - this.fluidAmount, resource.amount);
            if(doFill) {
                this.fluidAmount += fillAmount;
                this.markDirty();
            }
            return fillAmount;
        }
        return 0;
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        if(resource != null && resource.getFluid() == this.fluid) {
            return this.drain(resource.amount, doDrain);
        }
        return null;
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        if(this.fluidAmount > 0) {
            Fluid fluid = this.fluid;
            int fluidDrained = Math.min(maxDrain, this.fluidAmount);
            if(doDrain) {
                this.fluidAmount -= fluidDrained;
                if(this.fluidAmount <= 0) {
                    this.fluid = null;
                }
            }
            return fluidDrained > 0 && fluid != null ? new FluidStack(fluid, fluidDrained) : null;
        }
        return null;
    }
}
