package com.invadermonky.survivaltools.compat.survivaltools.tiles;

import com.invadermonky.survivaltools.api.SurvivalToolsAPI;
import com.invadermonky.survivaltools.api.blocks.RedstoneMode;
import com.invadermonky.survivaltools.api.tiles.AbstractTileFluxMachine;
import com.invadermonky.survivaltools.config.ConfigHandlerST;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.energy.IEnergyStorage;

public class TileCentralAirUnit extends AbstractTileFluxMachine implements IEnergyStorage {
    protected int radius;

    public TileCentralAirUnit() {
        super(20000);
        this.radius = ConfigHandlerST.machines.central_air_unit.radius;
        this.redstoneMode = RedstoneMode.REQUIRED;
    }

    @Override
    public void update() {
        if (!this.world.isRemote && this.isRunning() && this.tickEnergy()) {
            if (this.world.getTotalWorldTime() % 60 == 0) {
                BlockPos minPos = new BlockPos(this.getPos().getX() - this.radius, this.getPos().getY() - this.radius, this.getPos().getZ() - this.radius);
                BlockPos maxPos = new BlockPos(this.getPos().getX() + this.radius, this.getPos().getY() + this.radius, this.getPos().getZ() + this.radius);
                for (EntityPlayer player : this.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(minPos, maxPos))) {
                    SurvivalToolsAPI.stabilizePlayerTemperature(player, ConfigHandlerST.machines.central_air_unit.maxCooling, ConfigHandlerST.machines.central_air_unit.maxHeating);
                }
            }
            this.markDirty();
        }
    }


    @Override
    public int getMaxEnergyStored() {
        return 20000;
    }

    @Override
    public int getMaxTransfer() {
        return Math.max(512, this.getEnergyCost());
    }

    @Override
    public int getEnergyCost() {
        return ConfigHandlerST.machines.central_air_unit.energyCost;
    }
}
