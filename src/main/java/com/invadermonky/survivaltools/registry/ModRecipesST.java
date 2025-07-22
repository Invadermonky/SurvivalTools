package com.invadermonky.survivaltools.registry;

import com.invadermonky.survivaltools.SurvivalTools;
import com.invadermonky.survivaltools.api.IAddition;
import com.invadermonky.survivaltools.api.SurvivalToolsAPI;
import com.invadermonky.survivaltools.crafting.handheldpurifier.HandheldPurifierRecipeRegistry;
import com.invadermonky.survivaltools.crafting.purifier.PurifierRecipeRegistry;
import com.invadermonky.survivaltools.crafting.recipes.RecipeFillPurifiedContainer;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = SurvivalTools.MOD_ID)
public class ModRecipesST {

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        IForgeRegistry<IRecipe> registry = event.getRegistry();

        registry.register(new RecipeFillPurifiedContainer());

        ModItemsST.allItems.stream().filter(item -> item instanceof IAddition).forEach(item -> ((IAddition) item).registerRecipe(registry));
        ModBlocksST.allBlocks.keySet().stream().filter(block -> block instanceof IAddition).forEach(block -> ((IAddition) block).registerRecipe(registry));

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
