package com.invadermonky.survivaltools.registry;

import com.invadermonky.survivaltools.api.SurvivalToolsAPI;
import com.invadermonky.survivaltools.crafting.handheldpurifier.HandheldPurifierRecipeRegistry;
import com.invadermonky.survivaltools.crafting.purifier.PurifierRecipeRegistry;
import com.invadermonky.survivaltools.crafting.recipes.RecipeFillPurifiedContainer;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.IForgeRegistry;

public class ModRecipesST {
    public static void registerRecipes(IForgeRegistry<IRecipe> registry) {
        registry.register(new RecipeFillPurifiedContainer());

        if (SurvivalToolsAPI.isThirstFeatureEnabled()) {
            registerPurifierRecipes();
            registerHandheldPurifierRecipes();
        }
    }

    public static void registerPurifierRecipes() {
        PurifierRecipeRegistry.addRecipe(new FluidStack(FluidRegistry.WATER, 800), SurvivalToolsAPI.getPurifiedWaterStack(800));
        PurifierRecipeRegistry.addRecipe(new FluidStack(FluidRegistry.WATER, 2000), SurvivalToolsAPI.getPurifiedWaterStack(2000), CraftingHelper.getIngredient(SurvivalToolsAPI.getWaterFilterStack()), 1600);
    }

    public static void registerHandheldPurifierRecipes() {
        HandheldPurifierRecipeRegistry.addRecipe(FluidRegistry.WATER, 1000, true);
    }
}
