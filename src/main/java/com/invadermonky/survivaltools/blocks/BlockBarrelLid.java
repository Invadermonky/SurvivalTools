package com.invadermonky.survivaltools.blocks;

import com.invadermonky.survivaltools.api.IAddition;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;

public class BlockBarrelLid extends BlockTrapDoor implements IAddition {
    private boolean enabled;

    public BlockBarrelLid(Material material, boolean enabled) {
        super(material);
        this.setHardness(1.5f);
        this.setResistance(6.0f);
        this.enabled = enabled;
    }

    public boolean isSealing(IBlockState state) {
        return !state.getValue(OPEN) && state.getValue(HALF) == DoorHalf.BOTTOM;
    }

    public boolean isSolarLid(IBlockState state) {
        return state.getMaterial() == Material.GLASS;
    }

    @Override
    protected void playSound(@Nullable EntityPlayer player, World worldIn, BlockPos pos, boolean isOpening) {
        if (isOpening) {
            worldIn.playEvent(player, Constants.WorldEvents.IRON_TRAPDOOR_OPEN_SOUND, pos, 0);
        } else {
            worldIn.playEvent(player, Constants.WorldEvents.IRON_TRAPDOOR_CLOSE_SOUND, pos, 0);
        }
    }

    /*
     *  IAddition
     */
    @Override
    public void registerModel(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
