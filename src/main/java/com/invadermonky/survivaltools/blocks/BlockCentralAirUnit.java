package com.invadermonky.survivaltools.blocks;

import com.invadermonky.survivaltools.api.IAddition;
import com.invadermonky.survivaltools.blocks.tile.TileCentralAirUnit;
import com.invadermonky.survivaltools.config.ConfigHandlerST;
import com.invadermonky.survivaltools.util.ChatUtils;
import com.invadermonky.survivaltools.util.helpers.StringHelper;
import com.invadermonky.survivaltools.util.helpers.SurvivalHelper;
import com.invadermonky.survivaltools.util.helpers.SurvivalItemHelper;
import com.invadermonky.survivaltools.util.libs.LibTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
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
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
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
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class BlockCentralAirUnit extends BlockContainer implements IAddition {
    public static final PropertyBool ACTIVE = PropertyBool.create("active");

    public BlockCentralAirUnit() {
        super(Material.IRON);
        this.setHardness(10.0F);
        this.setHarvestLevel("pickaxe", 1);
        this.setDefaultState(this.blockState.getBaseState().withProperty(ACTIVE, false));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileCentralAirUnit tile = this.getTileEntity(world, pos);
        if(tile != null) {
            ItemStack stack = player.getHeldItem(hand);
            if(stack.isEmpty()) {
                ChatUtils.sendNoSpam(player,
                        new TextComponentTranslation(StringHelper.getTranslationKey("rf_info", "chat"), tile.getEnergyStored(), tile.getEnergyCost()),
                        new TextComponentTranslation(StringHelper.getTranslationKey(LibTags.TAG_REDSTONE, "chat", tile.getRedstoneMode().toString()))
                );
                return true;
            } else if(stack.getItem() == Item.getItemFromBlock(Blocks.REDSTONE_TORCH)) {
                if(!player.isSneaking()) {
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
    public boolean hasTileEntity() {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileCentralAirUnit();
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        return state.getValue(ACTIVE) ? 7 : 0;
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        updatePowered(worldIn, pos, state);
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        updatePowered(worldIn, pos, state);
    }

    @Nullable
    private TileCentralAirUnit getTileEntity(World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if(tile instanceof TileCentralAirUnit) {
            return (TileCentralAirUnit) tile;
        }
        return null;
    }

    private void updatePowered(World world, BlockPos pos, IBlockState state) {
        TileCentralAirUnit tile = getTileEntity(world, pos);
        if(tile == null) {
            return;
        }
        boolean running = tile.isRunning();
        if(running != world.getBlockState(pos).getValue(ACTIVE)) {
            world.setBlockState(pos, state.withProperty(ACTIVE, running), 2);
        }
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        TileCentralAirUnit tile = this.getTileEntity(world, pos);
        boolean running = tile != null && tile.isRunning();
        return this.getDefaultState().withProperty(ACTIVE, running);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        worldIn.notifyBlockUpdate(pos, state, state, 2);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(ACTIVE, meta != 0);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(ACTIVE) ? 1 : 0;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ACTIVE);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format(StringHelper.getTranslationKey("rf_info", "tooltip", "desc")));
        tooltip.add(I18n.format(StringHelper.getTranslationKey(LibTags.TAG_REDSTONE, "tooltip", "desc")));
    }

    /*
     *  IAddition
     */

    @Override
    public void registerRecipe(IForgeRegistry<IRecipe> registry) {
        CraftingHelper.ShapedPrimer primer = CraftingHelper.parseShaped(
                "HGC", "BRB", "HTC",
                'H', SurvivalItemHelper.getHeaterStack(),
                'C', SurvivalItemHelper.getCoolerStack(),
                'G', "blockGold",
                'B', Blocks.IRON_BARS,
                'R', "blockRedstone",
                'T', SurvivalItemHelper.getThermometerStack()
        );
        IRecipe recipe = new ShapedRecipes(this.getRegistryName().toString(), primer.width, primer.height, primer.input, new ItemStack(this));
        recipe.setRegistryName(this.getRegistryName());
        registry.register(recipe);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerModel(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
    }

    @Override
    public boolean isEnabled() {
        return ConfigHandlerST.flux_tools.central_air_unit.enable && SurvivalHelper.isTemperatureFeatureEnabled();
    }
}
