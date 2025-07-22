package com.invadermonky.survivaltools.tile;

import com.invadermonky.survivaltools.api.SurvivalToolsAPI;
import com.invadermonky.survivaltools.config.ConfigHandlerST;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.TileFluidHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class TileOpenBarrel extends TileFluidHandler implements ITickable {
    public TileOpenBarrel() {
        this.tank = new FluidTank(4000);
    }

    @Nullable
    public FluidStack getContainedFluid() {
        return this.tank.getFluid();
    }

    @Override
    public void update() {
        if (!this.world.isRemote) {
            boolean did = false;
            if (this.tank.getFluid() != null && (this.tank.getFluid().amount > this.tank.getCapacity())) {
                this.tank.setFluid(new FluidStack(this.tank.getFluid().getFluid(), this.tank.getCapacity()));
                did = true;
            }

            if (this.world.getTotalWorldTime() % 20L == 0L && this.canCollectRainwater()) {
                this.tank.fill(new FluidStack(SurvivalToolsAPI.getPurifiedWater(), ConfigHandlerST.machines.rain_collector.waterCollected), true);
                did = true;
            }

            if (did) {
                this.markDirty();
            }
        }
    }

    @Override
    public void markDirty() {
        super.markDirty();
        IBlockState state = this.world.getBlockState(this.pos);
        this.world.notifyBlockUpdate(this.pos, state, state, 3);
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

    public int getFluidAmount() {
        return this.tank.getFluidAmount();
    }

    public int getFluidMaxCapacity() {
        return this.tank.getCapacity();
    }

    public float getFluidHeight() {
        return this.tank != null && this.tank.getFluidAmount() > 0 ? (float) this.tank.getFluidAmount() / (float) this.getFluidMaxCapacity() : 0.0F;
    }

    public boolean canHoldPurifiedWater() {
        FluidStack fluidStack = this.tank.getFluid();
        return fluidStack == null || (SurvivalToolsAPI.getPurifiedWater() == fluidStack.getFluid() && fluidStack.amount < this.tank.getCapacity());
    }

    public boolean canCollectRainwater() {
        if (this.world.isRainingAt(this.getPos().up())) {
            return this.canHoldPurifiedWater();
        }
        return false;
    }

}
