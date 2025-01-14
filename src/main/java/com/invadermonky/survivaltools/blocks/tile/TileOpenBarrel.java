package com.invadermonky.survivaltools.blocks.tile;

import com.invadermonky.survivaltools.api.SurvivalToolsAPI;
import com.invadermonky.survivaltools.api.blocks.IBarrelHeater;
import com.invadermonky.survivaltools.blocks.BlockBarrelLid;
import com.invadermonky.survivaltools.config.ConfigHandlerST;
import com.invadermonky.survivaltools.config.ModTags;
import com.invadermonky.survivaltools.util.libs.LibTags;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.TileFluidHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TileOpenBarrel extends TileFluidHandler implements ITickable {
    public static int fluidMaxCapacity;
    public static int rainCollectionAmount;
    public static int rainCollectionInterval;
    public static int solarPurifierAmount;
    public static int solarPurifierInterval;
    public static int waterBoilerAmount;
    public static int waterBoilerInterval;

    private int boilProgress;
    private int solarProgress;
    private double biomeMultiplier = 0;

    public TileOpenBarrel() {
        this.tank = new FluidTank(ConfigHandlerST.open_barrel.openBarrelCapacity);
    }

    public int getFluidAmount() {
        return this.tank.getFluidAmount();
    }

    public int getFluidMaxCapacity() {
        return this.tank.getCapacity();
    }

    @Nullable
    public FluidStack getContainedFluid() {
        return this.tank.getFluid();
    }

    public float getFluidHeight() {
        return this.tank != null && this.tank.getFluidAmount() > 0 ? (float) this.tank.getFluidAmount() / (float) this.tank.getCapacity() : 0.0F;
    }

    public boolean isSealed() {
        IBlockState aboveState = this.world.getBlockState(this.pos.up());
        if(aboveState.getBlock() instanceof BlockBarrelLid) {
            return ((BlockBarrelLid) aboveState.getBlock()).isSealing(aboveState);
        }
        return false;
    }

    public boolean canHoldPurifiedWater() {
        FluidStack fluidStack = this.tank.getFluid();
        return fluidStack == null || (SurvivalToolsAPI.getPurifiedWater() == fluidStack.getFluid() && fluidStack.amount < fluidMaxCapacity);
    }

    public boolean isPurifierMultiblock() {
        FluidStack fluidStack = this.tank.getFluid();
        if(fluidStack != null && fluidStack.getFluid() == FluidRegistry.WATER) {
            for (EnumFacing horizontal : EnumFacing.HORIZONTALS) {
                TileEntity tile = this.world.getTileEntity(this.pos.offset(horizontal));
                if (tile instanceof TileOpenBarrel && ((TileOpenBarrel) tile).canHoldPurifiedWater()) {
                    return true;
                }
            }
        }
        return false;
    }

    public double getBiomeMultiplier() {
        if(ConfigHandlerST.open_barrel.solar_purifier.enableBiomeBonuses) {
            Biome biome = this.world.getBiome(this.getPos());
            if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.HOT)) {
                this.biomeMultiplier = 0.20f;
            } else if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.COLD)) {
                this.biomeMultiplier = -0.20f;
            }
        }
        return this.biomeMultiplier;
    }

    public boolean checkRainCollector() {
        if(ConfigHandlerST.open_barrel.rain_collector._enable) {
            if(this.world.isRainingAt(this.getPos().up())) {
                return this.canHoldPurifiedWater();
            } else {
                IBlockState aboveState = this.world.getBlockState(this.pos.up());
                if(aboveState.getBlock() instanceof BlockBarrelLid && !((BlockBarrelLid) aboveState.getBlock()).isSealing(aboveState) && this.world.isRainingAt(this.pos.up(2))) {
                    return this.canHoldPurifiedWater();
                }
            }
        }
        return false;
    }

    public boolean checkSolarPurifier() {
        if(ConfigHandlerST.open_barrel.solar_purifier._enable) {
            BlockPos abovePos = this.getPos().up();
            IBlockState aboveState = this.world.getBlockState(abovePos);
            Block aboveBlock = aboveState.getBlock();
            boolean isSolarLid = aboveBlock instanceof BlockBarrelLid && ((BlockBarrelLid) aboveBlock).isSolarLid(aboveState);
            if (isSolarLid && this.isSealed() && this.world.isDaytime() && this.world.canBlockSeeSky(abovePos) && !this.world.isRaining()) {
                return this.isPurifierMultiblock();
            }
        }
        return false;
    }

    public boolean checkWaterBoiler() {
        if(ConfigHandlerST.open_barrel.water_boiler._enable) {
            return this.isPurifierMultiblock();
        }
        return false;
    }

    public boolean canBoilWater() {
        IBlockState downState = this.world.getBlockState(this.pos.down());
        TileEntity tile = this.world.getTileEntity(this.pos.down());
        return (tile instanceof IBarrelHeater && ((IBarrelHeater) tile).isHeaterActive()) || ModTags.contains(ModTags.BOILER_HEATERS, downState);
    }

    public void doSteamParticles(BlockPos pos) {
        for(int i = 0; i < 2; i++) {
            double yMotion = this.world.rand.nextDouble() * 0.1;
            this.world.spawnParticle(
                    EnumParticleTypes.EXPLOSION_NORMAL,
                    pos.getX() + (this.world.rand.nextFloat() * 0.7) + 0.2,
                    pos.getY(),
                    pos.getZ() + (this.world.rand.nextFloat() * 0.7) + 0.2,
                    0, yMotion, 0
            );
        }
    }

    protected void doPurifierTransfer(int fillAmount) {
        if(!this.isSealed()) {
            this.tank.drain(fillAmount, true);
            this.doSteamParticles(this.pos.up());
        } else {
            List<TileOpenBarrel> openBarrels = new ArrayList<>();
            for (EnumFacing facing : EnumFacing.HORIZONTALS) {
                BlockPos checkPos = this.getPos().offset(facing);
                TileEntity tile = this.world.getTileEntity(checkPos);
                if (tile instanceof TileOpenBarrel && ((TileOpenBarrel) tile).canHoldPurifiedWater()) {
                    openBarrels.add((TileOpenBarrel) tile);
                }
            }

            if (!openBarrels.isEmpty()) {
                TileOpenBarrel barrel = openBarrels.get(this.world.rand.nextInt(openBarrels.size()));
                if (barrel.isSealed()) {
                    this.tank.drain(fillAmount, true);
                    barrel.tank.fill(new FluidStack(SurvivalToolsAPI.getPurifiedWater(), fillAmount), true);
                } else {
                    this.tank.drain(fillAmount, true);
                    barrel.doSteamParticles(barrel.getPos().up());
                }
            }
        }
    }

    @Override
    public void update() {
        if(!this.world.isRemote) {
            boolean did = false;
            if(this.tank.getFluid() != null && (this.tank.getFluid().amount > this.tank.getCapacity())) {
                this.tank.setFluid(new FluidStack(this.tank.getFluid().getFluid(), this.tank.getCapacity()));
                did = true;
            }

            if(this.world.getTotalWorldTime() % (long) rainCollectionInterval == 0L && this.checkRainCollector()) {
                this.tank.fill(new FluidStack(SurvivalToolsAPI.getPurifiedWater(), rainCollectionAmount), true);
                did = true;
            }

            if(this.checkSolarPurifier()) {
                int tickInterval = (int) (solarPurifierInterval * (1.0 - this.getBiomeMultiplier()));
                if(this.solarProgress >= tickInterval) {
                    this.doPurifierTransfer(solarPurifierAmount);
                    this.solarProgress = 0;
                } else {
                    this.solarProgress++;
                }
                did = true;
            }

            if(this.checkWaterBoiler() && this.canBoilWater()) {
                if(this.boilProgress >= waterBoilerInterval) {
                    this.doPurifierTransfer(waterBoilerAmount);
                    this.boilProgress = 0;
                } else {
                    this.boilProgress++;
                }
                did = true;
            }

            if(did) {
                this.markDirty();
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.boilProgress = tag.getInteger(LibTags.TAG_PROGRESS_BOIL);
        this.solarProgress = tag.getInteger(LibTags.TAG_PROGRESS_SOLAR);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger(LibTags.TAG_PROGRESS_BOIL, this.boilProgress);
        tag.setInteger(LibTags.TAG_PROGRESS_SOLAR, this.solarProgress);
        return tag;
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
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ? (T) this.tank : super.getCapability(capability, facing);
    }
}
