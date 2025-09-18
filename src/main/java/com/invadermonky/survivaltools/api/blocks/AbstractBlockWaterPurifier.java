package com.invadermonky.survivaltools.api.blocks;

import com.invadermonky.survivaltools.SurvivalTools;
import com.invadermonky.survivaltools.api.tiles.AbstractTileWaterPurifier;
import com.invadermonky.survivaltools.compat.survivaltools.tiles.TileSolidFuelPurifier;
import com.invadermonky.survivaltools.util.helpers.StringHelper;
import com.invadermonky.survivaltools.util.libs.CreativeTabST;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class AbstractBlockWaterPurifier<T extends AbstractTileWaterPurifier> extends BlockContainer implements IBlockAddition {
    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    public static final PropertyBool ENABLED = PropertyBool.create("enabled");

    private final Class<T> tileClass;

    public AbstractBlockWaterPurifier(String unlocName, Material material, Class<T> tileClass) {
        super(material);
        this.setRegistryName(SurvivalTools.MOD_ID, unlocName);
        this.setTranslationKey(this.getRegistryName().toString());
        this.setCreativeTab(CreativeTabST.TAB_ST);
        this.setHardness(3.5f);
        this.setResistance(3.5f);
        this.setHarvestLevel("pickaxe", 1);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(ENABLED, false));
        this.tileClass = tileClass;
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getLightValue(IBlockState state) {
        return state.getValue(ENABLED) ? 13 : 0;
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta & -5)).withProperty(ENABLED, (meta & 4) != 0);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return (state.getValue(ENABLED) ? 4 : 0) | state.getValue(FACING).getHorizontalIndex();
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
        return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
    }

    @Override
    public void onBlockAdded(@NotNull World worldIn, @NotNull BlockPos pos, @NotNull IBlockState state) {
        this.setDefaultFacing(worldIn, pos, state);
    }

    @Override
    public boolean onBlockActivated(@NotNull World worldIn, @NotNull BlockPos pos, @NotNull IBlockState state, @NotNull EntityPlayer playerIn, @NotNull EnumHand hand, @NotNull EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (FluidUtil.interactWithFluidHandler(playerIn, hand, worldIn, pos, facing)) {
            return true;
        }
        return this.onPurifierActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, @NotNull BlockPos pos, @NotNull IBlockState state, @NotNull EntityLivingBase placer, @NotNull ItemStack stack) {
        if (!worldIn.isRemote) {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof AbstractTileWaterPurifier && stack.hasTagCompound() && stack.getTagCompound().hasKey("BlockEntityTag")) {
                tile.readFromNBT(stack.getTagCompound().getCompoundTag("BlockEntityTag"));
            }
            worldIn.markAndNotifyBlock(pos, worldIn.getChunk(pos), state, state, 3);
        }
    }

    @Override
    protected @NotNull BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, ENABLED);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, @NotNull List<String> tooltip, @NotNull ITooltipFlag flagIn) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("BlockEntityTag")) {
            FluidStack inputFluid = FluidStack.loadFluidStackFromNBT(stack.getTagCompound().getCompoundTag("inputFluid"));
            FluidStack outputFluid = FluidStack.loadFluidStackFromNBT(stack.getTagCompound().getCompoundTag("outputFluid"));

            if (inputFluid != null) {
                tooltip.add(I18n.format("%4dmb %s", inputFluid.amount, inputFluid.getLocalizedName()));
            } else {
                tooltip.add(I18n.format(StringHelper.getTranslationKey("empty", "tooltip")));
            }

            if (outputFluid != null) {
                tooltip.add(I18n.format("%4dmb %s", outputFluid.amount, outputFluid.getLocalizedName()));
            } else {
                tooltip.add(I18n.format(StringHelper.getTranslationKey("empty", "tooltip")));
            }
        }
    }

    @Override
    public boolean hasTileEntity(@NotNull IBlockState state) {
        return true;
    }

    @Override
    public @NotNull IBlockState getStateForPlacement(@NotNull World world, @NotNull BlockPos pos, @NotNull EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, @NotNull EnumHand hand) {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(ENABLED, false);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull EnumBlockRenderType getRenderType(@NotNull IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public void breakBlock(World worldIn, @NotNull BlockPos pos, @NotNull IBlockState state) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof AbstractTileWaterPurifier) {
            ((AbstractTileWaterPurifier) tile).dropInventory();
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public void harvestBlock(@NotNull World worldIn, @NotNull EntityPlayer player, @NotNull BlockPos pos, @NotNull IBlockState state, @Nullable TileEntity te, @NotNull ItemStack stack) {
        ItemStack drop = new ItemStack(this);
        NBTTagCompound tileTag = new NBTTagCompound();
        if (te instanceof TileSolidFuelPurifier) {
            tileTag.setTag("BlockEntityTag", te.writeToNBT(new NBTTagCompound()));
            FluidStack inputFluid = ((TileSolidFuelPurifier) te).getInputFluidTank().getFluid();
            FluidStack outputFluid = ((TileSolidFuelPurifier) te).getOutputFluidTank().getFluid();
            if (inputFluid != null) {
                tileTag.setTag("inputFluid", inputFluid.writeToNBT(new NBTTagCompound()));
            }
            if (outputFluid != null) {
                tileTag.setTag("outputFluid", outputFluid.writeToNBT(new NBTTagCompound()));
            }
        }
        drop.setTagCompound(tileTag);
        spawnAsEntity(worldIn, pos, drop);
    }

    @Override
    public @Nullable TileEntity createNewTileEntity(@NotNull World worldIn, int meta) {
        if (this.tileClass != null) {
            try {
                return this.tileClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace(System.err);
            }
        }
        return null;
    }

    public void setDefaultFacing(World world, BlockPos pos, IBlockState state) {
        if (!world.isRemote) {
            IBlockState iblockstate = world.getBlockState(pos.north());
            IBlockState iblockstate1 = world.getBlockState(pos.south());
            IBlockState iblockstate2 = world.getBlockState(pos.west());
            IBlockState iblockstate3 = world.getBlockState(pos.east());
            EnumFacing enumfacing = state.getValue(FACING);

            if (enumfacing == EnumFacing.NORTH && iblockstate.isFullBlock() && !iblockstate1.isFullBlock()) {
                enumfacing = EnumFacing.SOUTH;
            } else if (enumfacing == EnumFacing.SOUTH && iblockstate1.isFullBlock() && !iblockstate.isFullBlock()) {
                enumfacing = EnumFacing.NORTH;
            } else if (enumfacing == EnumFacing.WEST && iblockstate2.isFullBlock() && !iblockstate3.isFullBlock()) {
                enumfacing = EnumFacing.EAST;
            } else if (enumfacing == EnumFacing.EAST && iblockstate3.isFullBlock() && !iblockstate2.isFullBlock()) {
                enumfacing = EnumFacing.WEST;
            }

            world.setBlockState(pos, state.withProperty(FACING, enumfacing), 2);
        }
    }

    public abstract boolean onPurifierActivated(@NotNull World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ);

    @SuppressWarnings("ConstantConditions")
    @Override
    public void registerBlock(IForgeRegistry<Block> registry) {
        IBlockAddition.super.registerBlock(registry);
        GameRegistry.registerTileEntity(this.tileClass, this.getRegistryName());
    }
}
