package com.invadermonky.survivaltools.compat.crafttweaker.handlers;

import com.invadermonky.survivaltools.crafting.handheldpurifier.HandheldPurifierRecipeRegistry;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraftforge.fluids.Fluid;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.survivaltools.HandheldPurifier")
public class HandheldPurifierCT {
    @ZenMethod
    public static void addRecipe(ILiquidStack inputLiquid, int outputAmount, boolean canFillBottles) {
        Fluid fluid = CraftTweakerMC.getFluid(inputLiquid.getDefinition());
        if (fluid == null) {
            CraftTweakerAPI.logError("Not a valid fluid stack: " + inputLiquid);
            throw new IllegalArgumentException("Not a valid fluid stack: " + inputLiquid);
        } else if (outputAmount <= 0) {
            CraftTweakerAPI.logError("Output amount must be greater than 0");
            throw new IllegalArgumentException("Output amount must be greater than 0");
        } else {
            HandheldPurifierRecipeRegistry.addRecipe(fluid, outputAmount, canFillBottles);
        }
    }

    @ZenMethod
    public static void removeRecipe(ILiquidStack inputLiquid) {
        HandheldPurifierRecipeRegistry.removeRecipe(CraftTweakerMC.getFluid(inputLiquid.getDefinition()));
    }

    @ZenMethod
    public static void removeAll() {
        HandheldPurifierRecipeRegistry.removeAll();
    }
}
