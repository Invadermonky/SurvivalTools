package com.invadermonky.survivaltools.inventory.container;

import com.invadermonky.survivaltools.inventory.container.base.AbstractContainerWaterPurifier;
import com.invadermonky.survivaltools.tile.TilePoweredPurifier;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerPoweredPurifier extends AbstractContainerWaterPurifier<TilePoweredPurifier> {
    public ContainerPoweredPurifier(InventoryPlayer inventoryPlayer, TilePoweredPurifier tilePurifier) {
        super(inventoryPlayer, tilePurifier);
    }

    @Override
    protected void registerAdditionalFields() {}

    @Override
    protected void bindTileInventory() {}
}
