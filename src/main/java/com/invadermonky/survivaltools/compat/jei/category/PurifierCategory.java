package com.invadermonky.survivaltools.compat.jei.category;

import com.invadermonky.survivaltools.SurvivalTools;
import com.invadermonky.survivaltools.api.SurvivalToolsAPI;
import com.invadermonky.survivaltools.client.gui.base.AbstractGuiWaterPurifier;
import com.invadermonky.survivaltools.compat.jei.wrapper.PurifierRecipeWrapper;
import com.invadermonky.survivaltools.registry.ModBlocksST;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.util.Translator;
import net.minecraft.item.Item;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PurifierCategory implements IRecipeCategory<PurifierRecipeWrapper> {
    public static final String UID = SurvivalTools.MOD_ID + "purifier";
    public static final String L18N_KEY = "jei." + SurvivalTools.MOD_ID + ".recipe.purifier";

    private final String localizedName;
    private final IDrawable background;
    private final IDrawable icon;

    public PurifierCategory(IGuiHelper guiHelper) {
        this.localizedName = Translator.translateToLocal(L18N_KEY);
        this.background = guiHelper.createDrawable(AbstractGuiWaterPurifier.TEXTURE, 44, 12, 88, 58);
        if (ModBlocksST.solid_fuel_purifier.isEnabled()) {
            this.icon = guiHelper.createDrawableIngredient(Item.getItemFromBlock(ModBlocksST.solid_fuel_purifier).getDefaultInstance());
        } else if (ModBlocksST.powered_purifier.isEnabled()) {
            this.icon = guiHelper.createDrawableIngredient(Item.getItemFromBlock(ModBlocksST.powered_purifier).getDefaultInstance());
        } else {
            this.icon = guiHelper.createDrawableIngredient(SurvivalToolsAPI.getPurifiedWaterBottleStack());
        }
    }

    @Override
    public @NotNull String getUid() {
        return UID;
    }

    @Override
    public @NotNull String getTitle() {
        return localizedName;
    }

    @Override
    public @NotNull String getModName() {
        return SurvivalTools.MOD_NAME;
    }

    @Override
    public @NotNull IDrawable getBackground() {
        return this.background;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayout iRecipeLayout, @NotNull PurifierRecipeWrapper purifierRecipeWrapper, IIngredients iIngredients) {
        IGuiFluidStackGroup fluids = iRecipeLayout.getFluidStacks();
        fluids.init(0, true, 4, 4, 13, 50, 4000, true, null);
        fluids.init(1, false, 71, 4, 13, 50, 4000, true, null);
        IGuiItemStackGroup stacks = iRecipeLayout.getItemStacks();
        stacks.init(0, true, 35, 7);

        fluids.set(0, iIngredients.getInputs(VanillaTypes.FLUID).get(0));

        if (iIngredients.getInputs(VanillaTypes.ITEM).size() == 1) {
            stacks.set(0, iIngredients.getInputs(VanillaTypes.ITEM).get(0));
        }

        fluids.set(1, iIngredients.getOutputs(VanillaTypes.FLUID).get(0));
    }
}
