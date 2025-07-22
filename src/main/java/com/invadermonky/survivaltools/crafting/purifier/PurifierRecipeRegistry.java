package com.invadermonky.survivaltools.crafting.purifier;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class PurifierRecipeRegistry {
    private static final Set<PurifierRecipe> purifierRecipes = new HashSet<>();

    public static void addRecipe(PurifierRecipe recipe) {
        purifierRecipes.add(recipe);
    }

    public static void addRecipe(FluidStack inputFluid, FluidStack outputFluid, Ingredient filterIngredient, int recipeDuration) {
        try {
            addRecipe(new PurifierRecipe(inputFluid, outputFluid, filterIngredient, recipeDuration));
        } catch (IllegalArgumentException e) {
            e.printStackTrace(System.err);
        }
    }

    public static void addRecipe(FluidStack inputFluid, FluidStack outputFluid, Ingredient filterIngredient) {
        addRecipe(inputFluid, outputFluid, filterIngredient, 400);
    }

    public static void addRecipe(FluidStack inputFluid, FluidStack outputFluid) {
        addRecipe(inputFluid, outputFluid, Ingredient.EMPTY, 400);
    }

    public static void removeByInput(Fluid inputFluid) {
        if (inputFluid != null) {
            purifierRecipes.removeIf(recipe -> recipe.getInputFluid().getFluid() == inputFluid);
        }
    }

    public static void removeByOutput(FluidStack outputFluid) {
        if (outputFluid != null) {
            purifierRecipes.removeIf(recipe -> recipe.outputFluid.getFluid() == outputFluid.getFluid()
                    && recipe.outputFluid.amount == outputFluid.amount);
        }
    }

    public static void removeAll() {
        purifierRecipes.clear();
    }

    public static Set<PurifierRecipe> getPurifierRecipes() {
        return purifierRecipes;
    }

    public static boolean isValidFilterItem(ItemStack filterStack) {
        if (filterStack.isEmpty())
            return false;
        return purifierRecipes.stream().anyMatch(recipe -> recipe.getFilterIngredient().apply(filterStack));
    }

    public static boolean isValidInputFluid(FluidStack inputFluid) {
        if (inputFluid == null || inputFluid.amount <= 0)
            return false;
        return purifierRecipes.stream().anyMatch(recipe -> recipe.getInputFluid().getFluid() == inputFluid.getFluid());
    }

    @Nullable
    public static PurifierRecipe getRecipe(@Nullable FluidStack inputFluid, ItemStack filterStack) {
        if (inputFluid == null) {
            return null;
        }
        return purifierRecipes.stream().filter(recipe -> recipe.matches(inputFluid, filterStack)).findFirst().orElse(null);
    }
}
