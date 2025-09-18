package com.invadermonky.survivaltools.inventory.container;

import com.invadermonky.survivaltools.compat.survivaltools.tiles.TilePoweredPurifier;
import com.invadermonky.survivaltools.inventory.container.base.AbstractContainerWaterPurifier;
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
