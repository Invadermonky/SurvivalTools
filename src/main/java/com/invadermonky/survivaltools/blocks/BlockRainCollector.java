package com.invadermonky.survivaltools.blocks;

import com.invadermonky.survivaltools.api.IAddition;
import com.invadermonky.survivaltools.blocks.tile.TileRainCollector;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.FluidUtil;

import javax.annotation.Nullable;

public class BlockRainCollector extends BlockContainer implements IAddition {
    public BlockRainCollector() {
        super(Material.IRON, MapColor.STONE);
        this.setHardness(10.0F);
        this.setHarvestLevel("pickaxe", 1);
    }

    @Nullable
    public TileRainCollector getTileEntity(World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof TileRainCollector ? (TileRainCollector) tile : null;
    }

    /*
     *  BlockContainer
     */

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        boolean isFluidContainer = FluidUtil.getFluidHandler(player.getHeldItem(hand)) != null;
        boolean success = FluidUtil.interactWithFluidHandler(player, hand, world, pos, facing);
        if(isFluidContainer) {
            if(success) {
                world.checkLight(pos);
                world.updateComparatorOutputLevel(pos, this);
                world.markAndNotifyBlock(pos, world.getChunk(pos), state, state, 3);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean hasTileEntity() {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileRainCollector();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
        TileRainCollector tile = this.getTileEntity(worldIn, pos);
        if(tile != null) {
            int fluidAmount = tile.getFluidAmount();
            return (int) Math.floor((float) fluidAmount / (float) tile.getFluidMaxCapacity() * 14.0F) + (fluidAmount > 0 ? 1 : 0);
        }
        return 0;
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
        //TODO: Disable before release unless it's finished.
        //  Add config value.
        return false;
    }
}
