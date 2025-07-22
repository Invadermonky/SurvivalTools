package com.invadermonky.survivaltools.compat.crafttweaker.handlers;

import com.invadermonky.survivaltools.crafting.purifier.PurifierRecipeRegistry;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.survivaltools.Purifier")
public class PurifierCT {
    @ZenMethod
    public static void addRecipe(ILiquidStack input, ILiquidStack output, @Optional IIngredient filter, @Optional int recipeDuration) {
        FluidStack inputFluid = CraftTweakerMC.getLiquidStack(input);
        FluidStack outputFluid = CraftTweakerMC.getLiquidStack(output);
        Ingredient filterItem = filter != null ? CraftTweakerMC.getIngredient(filter) : Ingredient.EMPTY;
        if (recipeDuration == 0)
            recipeDuration = 800;
        PurifierRecipeRegistry.addRecipe(inputFluid, outputFluid, filterItem, recipeDuration);
    }

    @ZenMethod
    public static void removeByInput(ILiquidStack liquidStack) {
        Fluid inputFluid = CraftTweakerMC.getFluid(liquidStack.getDefinition());
        PurifierRecipeRegistry.removeByInput(inputFluid);
    }

    @ZenMethod
    public static void removeByOutput(ILiquidStack outputFluid) {
        PurifierRecipeRegistry.removeByOutput(CraftTweakerMC.getLiquidStack(outputFluid));
    }

    @ZenMethod
    public static void removeAll() {
        PurifierRecipeRegistry.removeAll();
    }
}
