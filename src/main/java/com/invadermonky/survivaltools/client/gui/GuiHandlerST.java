package com.invadermonky.survivaltools.client.gui;

import com.invadermonky.survivaltools.compat.survivaltools.tiles.TilePoweredPurifier;
import com.invadermonky.survivaltools.compat.survivaltools.tiles.TileSolidFuelPurifier;
import com.invadermonky.survivaltools.inventory.container.ContainerPoweredPurifier;
import com.invadermonky.survivaltools.inventory.container.ContainerSolidFuelPurifier;
import com.invadermonky.survivaltools.util.libs.ModGui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

public class GuiHandlerST implements IGuiHandler {
    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
        ModGui guiId = ModGui.values()[ID];
        switch (guiId) {
            case CENTRAL_AIR_UNIT:
                return null;
            case POWERED_PURIFIER:
                return new ContainerPoweredPurifier(player.inventory, (TilePoweredPurifier) tile);
            case SOLID_FUEL_PURIFIER:
                return new ContainerSolidFuelPurifier(player.inventory, (TileSolidFuelPurifier) tile);
        }
        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
        ModGui modGui = ModGui.values()[ID];
        switch (modGui) {
            case CENTRAL_AIR_UNIT:
                return null;
            case POWERED_PURIFIER:
                return new GuiPoweredPurifier(new ContainerPoweredPurifier(player.inventory, (TilePoweredPurifier) tile));
            case SOLID_FUEL_PURIFIER:
                return new GuiSolidFuelPurifier(new ContainerSolidFuelPurifier(player.inventory, (TileSolidFuelPurifier) tile));
        }
        return null;
    }
}
