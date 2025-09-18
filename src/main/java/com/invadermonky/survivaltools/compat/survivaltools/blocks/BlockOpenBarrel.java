package com.invadermonky.survivaltools.compat.survivaltools.blocks;

import com.invadermonky.survivaltools.SurvivalTools;
import com.invadermonky.survivaltools.api.blocks.IBlockAddition;
import com.invadermonky.survivaltools.client.render.RenderOpenBarrel;
import com.invadermonky.survivaltools.compat.survivaltools.tiles.TileOpenBarrel;
import com.invadermonky.survivaltools.config.ConfigHandlerST;
import com.invadermonky.survivaltools.util.libs.CreativeTabST;
import com.invadermonky.survivaltools.util.libs.LibNames;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BlockOpenBarrel extends BlockContainer implements IBlockAddition {
    /*
        Model bounds and hitbox taken from Inspirations Enhanced Cauldron licensed under MIT License.
        https://github.com/KnightMiner/Inspirations/blob/1.12/src/main/java/knightminer/inspirations/recipes/block/BlockEnhancedCauldron.java
    */
    public static final AxisAlignedBB[] BOUNDS = {
            new AxisAlignedBB(0, 0.1875, 0, 1, 1, 1),
            new AxisAlignedBB(0, 0, 0, 0.25, 0.1875, 0.125),
            new AxisAlignedBB(0, 0, 0.125, 0.125, 0.1875, 0.25),
            new AxisAlignedBB(0.75, 0, 0, 1, 0.1875, 0.125),
            new AxisAlignedBB(0.875, 0, 0.125, 1, 0.1875, 0.25),
            new AxisAlignedBB(0, 0, 0.875, 0.25, 0.1875, 1),
            new AxisAlignedBB(0, 0, 0.75, 0.125, 0.1875, 0.875),
            new AxisAlignedBB(0.75, 0, 0.875, 1, 0.1875, 1),
            new AxisAlignedBB(0.875, 0, 0.75, 1, 0.1875, 0.875)
    };

    public BlockOpenBarrel() {
        super(Material.IRON, MapColor.STONE);
        this.setRegistryName(SurvivalTools.MOD_ID, LibNames.OPEN_BARREL);
        this.setTranslationKey(this.getRegistryName().toString());
        this.setCreativeTab(CreativeTabST.TAB_ST);
        this.setHardness(6.0F);
        this.setHarvestLevel("pickaxe", 1);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isFullCube(@NotNull IBlockState state) {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean hasTileEntity() {
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull BlockFaceShape getBlockFaceShape(@NotNull IBlockAccess worldIn, @NotNull IBlockState state, @NotNull BlockPos pos, @NotNull EnumFacing face) {
        return face == EnumFacing.DOWN ? BlockFaceShape.UNDEFINED : BlockFaceShape.SOLID;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isOpaqueCube(@NotNull IBlockState state) {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Nullable
    @Override
    public RayTraceResult collisionRayTrace(@NotNull IBlockState blockState, @NotNull World worldIn, @NotNull BlockPos pos, @NotNull Vec3d start, @NotNull Vec3d end) {
        List<RayTraceResult> list = new ArrayList<>();
        for (AxisAlignedBB axisAlignedBB : BOUNDS) {
            list.add(this.rayTrace(pos, start, end, axisAlignedBB));
        }

        RayTraceResult closest = null;
        double max = 0;
        for (RayTraceResult trace : list) {
            if (trace != null) {
                double distance = trace.hitVec.squareDistanceTo(end);
                if (distance > max) {
                    closest = trace;
                    max = distance;
                }
            }
        }
        return closest;
    }

    @Override
    public @NotNull BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    /*
     *  BlockContainer
     */

    @Override
    public boolean onBlockActivated(@NotNull World world, @NotNull BlockPos pos, @NotNull IBlockState state, EntityPlayer player, @NotNull EnumHand hand, @NotNull EnumFacing facing, float hitX, float hitY, float hitZ) {
        boolean isFluidContainer = FluidUtil.getFluidHandler(player.getHeldItem(hand)) != null;
        boolean success = FluidUtil.interactWithFluidHandler(player, hand, world, pos, facing);
        if (isFluidContainer) {
            if (success) {
                world.checkLight(pos);
                world.updateComparatorOutputLevel(pos, this);
                world.markAndNotifyBlock(pos, world.getChunk(pos), state, state, 3);
            }
            return true;
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean hasComparatorInputOverride(@NotNull IBlockState state) {
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getComparatorInputOverride(@NotNull IBlockState blockState, @NotNull World worldIn, @NotNull BlockPos pos) {
        TileOpenBarrel tile = this.getTileEntity(worldIn, pos);
        if (tile != null) {
            int fluidAmount = tile.getFluidAmount();
            return (int) Math.floor((float) fluidAmount / (float) tile.getFluidMaxCapacity() * 14.0F) + (fluidAmount > 0 ? 1 : 0);
        }
        return 0;
    }

    @Nullable
    public TileOpenBarrel getTileEntity(World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof TileOpenBarrel ? (TileOpenBarrel) tile : null;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(@NotNull World worldIn, int meta) {
        return new TileOpenBarrel();
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull EnumBlockRenderType getRenderType(@NotNull IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    /*
     *  IAddition
     */

    @SuppressWarnings("ConstantConditions")
    @Override
    public void registerBlock(IForgeRegistry<Block> registry) {
        IBlockAddition.super.registerBlock(registry);
        GameRegistry.registerTileEntity(TileOpenBarrel.class, this.getRegistryName());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerModel(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
        ClientRegistry.bindTileEntitySpecialRenderer(TileOpenBarrel.class, new RenderOpenBarrel());
    }

    @Override
    public boolean isEnabled() {
        return ConfigHandlerST.machines.rain_collector.enable;
    }
}
