package com.invadermonky.survivaltools.blocks;

import com.invadermonky.survivaltools.api.SurvivalToolsAPI;
import com.invadermonky.survivaltools.blocks.base.AbstractBlockFluxMachine;
import com.invadermonky.survivaltools.config.ConfigHandlerST;
import com.invadermonky.survivaltools.tile.TileCentralAirUnit;
import com.invadermonky.survivaltools.util.libs.LibTags;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class BlockCentralAirUnit extends AbstractBlockFluxMachine {
    @Nullable
    @Override
    public TileEntity createNewTileEntity(@NotNull World worldIn, int meta) {
        return new TileCentralAirUnit();
    }

    @Override
    public int getLightValue(IBlockState state, @NotNull IBlockAccess world, @NotNull BlockPos pos) {
        return state.getValue(LibTags.ACTIVE) ? 7 : 0;
    }

    /*
     *  IAddition
     */

    @Override
    public void registerRecipe(IForgeRegistry<IRecipe> registry) {
        CraftingHelper.ShapedPrimer primer = CraftingHelper.parseShaped(
                "HGC", "BRB", "HTC",
                'H', SurvivalToolsAPI.getHeaterStack(),
                'C', SurvivalToolsAPI.getCoolerStack(),
                'G', "blockGold",
                'B', Blocks.IRON_BARS,
                'R', "blockRedstone",
                'T', SurvivalToolsAPI.getThermometerStack()
        );
        IRecipe recipe = new ShapedRecipes(this.getRegistryName().toString(), primer.width, primer.height, primer.input, new ItemStack(this));
        recipe.setRegistryName(this.getRegistryName());
        registry.register(recipe);
    }

    @Override
    public boolean isEnabled() {
        return ConfigHandlerST.machines.central_air_unit.enable && SurvivalToolsAPI.isTemperatureFeatureEnabled();
    }
}
