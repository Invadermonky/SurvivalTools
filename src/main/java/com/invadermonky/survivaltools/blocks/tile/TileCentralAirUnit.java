package com.invadermonky.survivaltools.blocks.tile;

import com.invadermonky.survivaltools.api.SurvivalToolsAPI;
import com.invadermonky.survivaltools.api.blocks.RedstoneMode;
import com.invadermonky.survivaltools.config.ConfigHandlerST;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class TileCentralAirUnit extends AbstractTileFluxMachine {
    protected int radius;

    public TileCentralAirUnit() {
        super(ConfigHandlerST.flux_tools.central_air_unit.energyCapacity, ConfigHandlerST.flux_tools.central_air_unit.energyCost);
        this.radius = ConfigHandlerST.flux_tools.central_air_unit.radius;
        this.redstoneMode = RedstoneMode.REQUIRED;
    }

    @Override
    public void update() {
        if(!this.world.isRemote && this.isRunning() && this.tickEnergy()) {
            if (this.world.getTotalWorldTime() % 60 == 0) {
                BlockPos minPos = new BlockPos(this.getPos().getX() - this.radius, this.getPos().getY() - this.radius, this.getPos().getZ() - this.radius);
                BlockPos maxPos = new BlockPos(this.getPos().getX() + this.radius, this.getPos().getY() + this.radius, this.getPos().getZ() + this.radius);
                for (EntityPlayer player : this.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(minPos, maxPos))) {
                    SurvivalToolsAPI.stabilizePlayerTemperature(player, ConfigHandlerST.flux_tools.central_air_unit.maxCooling, ConfigHandlerST.flux_tools.central_air_unit.maxHeating);
                }
            }
            this.markDirty();
        }
    }
}
