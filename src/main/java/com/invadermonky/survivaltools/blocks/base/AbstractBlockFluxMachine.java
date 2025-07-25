package com.invadermonky.survivaltools.blocks.base;

import com.invadermonky.survivaltools.api.IAddition;
import com.invadermonky.survivaltools.tile.base.AbstractTileFluxMachine;
import com.invadermonky.survivaltools.util.ChatUtils;
import com.invadermonky.survivaltools.util.helpers.StringHelper;
import com.invadermonky.survivaltools.util.libs.LibTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public abstract class AbstractBlockFluxMachine extends BlockContainer implements IAddition {
    public AbstractBlockFluxMachine() {
        super(Material.IRON);
        this.setHardness(6.0F);
        this.setHarvestLevel("pickaxe", 1);
        this.setDefaultState(this.blockState.getBaseState().withProperty(LibTags.ACTIVE, false));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public @NotNull EnumBlockRenderType getRenderType(@NotNull IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Nullable
    private AbstractTileFluxMachine getTileEntity(World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof AbstractTileFluxMachine) {
            return (AbstractTileFluxMachine) tile;
        }
        return null;
    }

    protected void updatePowered(World world, BlockPos pos, IBlockState state) {
        AbstractTileFluxMachine tile = getTileEntity(world, pos);
        if (tile == null) {
            return;
        }
        boolean running = tile.isRunning();
        if (running != world.getBlockState(pos).getValue(LibTags.ACTIVE)) {
            world.setBlockState(pos, state.withProperty(LibTags.ACTIVE, running), 2);
        }
    }

    @Override
    public @NotNull IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(LibTags.ACTIVE, meta != 0);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(LibTags.ACTIVE) ? 1 : 0;
    }

    @Override
    public boolean hasTileEntity() {
        return true;
    }

    @Override
    public void updateTick(@NotNull World worldIn, @NotNull BlockPos pos, @NotNull IBlockState state, @NotNull Random rand) {
        updatePowered(worldIn, pos, state);
    }

    @Override
    public void neighborChanged(@NotNull IBlockState state, @NotNull World worldIn, @NotNull BlockPos pos, @NotNull Block blockIn, @NotNull BlockPos fromPos) {
        updatePowered(worldIn, pos, state);
    }

    @Override
    public boolean onBlockActivated(@NotNull World world, @NotNull BlockPos pos, @NotNull IBlockState state, @NotNull EntityPlayer player, @NotNull EnumHand hand, @NotNull EnumFacing facing, float hitX, float hitY, float hitZ) {
        AbstractTileFluxMachine tile = this.getTileEntity(world, pos);
        if (tile != null) {
            ItemStack stack = player.getHeldItem(hand);
            if (stack.isEmpty()) {
                ChatUtils.sendNoSpam(player,
                        new TextComponentTranslation(StringHelper.getTranslationKey("rf_info", "chat"), tile.getEnergyStored(), tile.getEnergyCost()),
                        new TextComponentTranslation(StringHelper.getTranslationKey(LibTags.TAG_REDSTONE, "chat", tile.getRedstoneMode().toString()))
                );
                return true;
            } else if (stack.getItem() == Item.getItemFromBlock(Blocks.REDSTONE_TORCH)) {
                if (!player.isSneaking()) {
                    tile.nextRedstoneMode();
                } else {
                    tile.previousRedstoneMode();
                }
                ChatUtils.sendNoSpam(player, new TextComponentTranslation(StringHelper.getTranslationKey(LibTags.TAG_REDSTONE, "chat", tile.getRedstoneMode().toString())));
                world.scheduleUpdate(pos, this, 0);
                tile.markDirty();
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, @NotNull BlockPos pos, @NotNull IBlockState state, @NotNull EntityLivingBase placer, @NotNull ItemStack stack) {
        worldIn.notifyBlockUpdate(pos, state, state, 2);
    }

    @Override
    protected @NotNull BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, LibTags.ACTIVE);
    }

    @Override
    public void addInformation(@NotNull ItemStack stack, @Nullable World worldIn, List<String> tooltip, @NotNull ITooltipFlag flagIn) {
        tooltip.add(I18n.format(StringHelper.getTranslationKey("rf_info", "tooltip", "desc")));
        tooltip.add(I18n.format(StringHelper.getTranslationKey(LibTags.TAG_REDSTONE, "tooltip", "desc")));
    }

    @Override
    public int getLightValue(IBlockState state, @NotNull IBlockAccess world, @NotNull BlockPos pos) {
        return state.getValue(LibTags.ACTIVE) ? 7 : 0;
    }

    @Override
    public @NotNull IBlockState getStateForPlacement(@NotNull World world, @NotNull BlockPos pos, @NotNull EnumFacing facing, float hitX, float hitY, float hitZ, int meta, @NotNull EntityLivingBase placer, @NotNull EnumHand hand) {
        AbstractTileFluxMachine tile = this.getTileEntity(world, pos);
        boolean running = tile != null && tile.isRunning();
        return this.getDefaultState().withProperty(LibTags.ACTIVE, running);
    }

    /*
     *  IAddition
     */

    @SideOnly(Side.CLIENT)
    @Override
    public void registerModel(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
    }
}
