package com.invadermonky.survivaltools.crafting.handheldpurifier;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class HandheldPurifierRecipeRegistry {
    private static final Set<HandheldPurifierRecipe> recipes = new HashSet<>();

    public static void addRecipe(HandheldPurifierRecipe recipe) {
        recipes.add(recipe);
    }

    public static void addRecipe(Fluid inputFluid, int outputAmount, boolean canFillBottles) {
        try {
            addRecipe(new HandheldPurifierRecipe(inputFluid, outputAmount, canFillBottles));
        } catch (IllegalArgumentException e) {
            e.printStackTrace(System.err);
        }
    }

    public static void removeRecipe(Fluid inputFluid) {
        recipes.removeIf(recipe -> recipe.getInputFluid() == inputFluid);
    }

    public static void removeAll() {
        recipes.clear();
    }

    public static Set<HandheldPurifierRecipe> getRecipes() {
        return recipes;
    }

    @Nullable
    public static HandheldPurifierRecipe getRecipe(FluidStack fluidStack) {
        if (fluidStack != null) {
            return recipes.stream().filter(recipe -> recipe.matches(fluidStack)).findFirst().orElse(null);
        }
        return null;
    }
}
