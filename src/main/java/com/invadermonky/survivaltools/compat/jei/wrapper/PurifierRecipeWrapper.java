package com.invadermonky.survivaltools.compat.jei.wrapper;

import com.invadermonky.survivaltools.crafting.purifier.PurifierRecipe;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;

import java.util.Arrays;

public class PurifierRecipeWrapper implements IRecipeWrapper {
    protected PurifierRecipe recipe;

    public PurifierRecipeWrapper(PurifierRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients iIngredients) {
        iIngredients.setInput(VanillaTypes.FLUID, this.recipe.getInputFluid());
        iIngredients.setInputs(VanillaTypes.ITEM, Arrays.asList(this.recipe.getFilterIngredient().getMatchingStacks()));
        iIngredients.setOutput(VanillaTypes.FLUID, this.recipe.getOutputFluid());
    }
}
