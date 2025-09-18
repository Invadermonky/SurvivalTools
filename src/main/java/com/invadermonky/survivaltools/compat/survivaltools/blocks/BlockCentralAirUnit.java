package com.invadermonky.survivaltools.compat.survivaltools.blocks;

import com.invadermonky.survivaltools.api.SurvivalToolsAPI;
import com.invadermonky.survivaltools.api.blocks.AbstractBlockFluxMachine;
import com.invadermonky.survivaltools.compat.survivaltools.tiles.TileCentralAirUnit;
import com.invadermonky.survivaltools.config.ConfigHandlerST;
import com.invadermonky.survivaltools.util.libs.LibNames;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.IForgeRegistry;

public class BlockCentralAirUnit extends AbstractBlockFluxMachine<TileCentralAirUnit> {
    public BlockCentralAirUnit() {
        super(LibNames.CENTRAL_AIR_UNIT, TileCentralAirUnit.class);
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
