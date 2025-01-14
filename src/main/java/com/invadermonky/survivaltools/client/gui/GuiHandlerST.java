package com.invadermonky.survivaltools.client.gui;

import com.invadermonky.survivaltools.blocks.tile.TileBarrelHeater;
import com.invadermonky.survivaltools.inventory.ContainerBarrelHeater;
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
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity tile = world.getTileEntity(pos);
        if(ID == 0 && tile instanceof TileBarrelHeater) {
            return new ContainerBarrelHeater(player.inventory, (TileBarrelHeater) tile);
        }
        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity tile = world.getTileEntity(pos);
        if(ID == 0 && tile instanceof TileBarrelHeater) {
            return new GuiBarrelHeater(player.inventory, (TileBarrelHeater) tile);
        }
        return null;
    }
}
