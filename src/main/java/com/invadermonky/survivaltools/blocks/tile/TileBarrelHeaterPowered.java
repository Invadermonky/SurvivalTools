package com.invadermonky.survivaltools.blocks.tile;

import com.invadermonky.survivaltools.api.blocks.IBarrelHeater;
import com.invadermonky.survivaltools.api.blocks.RedstoneMode;
import com.invadermonky.survivaltools.config.ConfigHandlerST;

public class TileBarrelHeaterPowered extends AbstractTileFluxMachine implements IBarrelHeater {
    public TileBarrelHeaterPowered() {
        super(ConfigHandlerST.open_barrel.water_boiler.powered_heater.energyCapacity, ConfigHandlerST.open_barrel.water_boiler.powered_heater.energyCost);
        this.redstoneMode = RedstoneMode.REQUIRED;
    }

    @Override
    public boolean isHeaterActive() {
        return this.isRunning();
    }

    @Override
    public void update() {
        if(!this.world.isRemote && this.isRunning() && this.tickEnergy()) {
            this.markDirty();
        }
    }
}
