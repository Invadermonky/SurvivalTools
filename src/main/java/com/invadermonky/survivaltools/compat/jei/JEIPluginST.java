package com.invadermonky.survivaltools.compat.jei;

import com.invadermonky.survivaltools.client.gui.GuiPoweredPurifier;
import com.invadermonky.survivaltools.client.gui.GuiSolidFuelPurifier;
import com.invadermonky.survivaltools.compat.jei.category.PurifierCategory;
import com.invadermonky.survivaltools.compat.jei.wrapper.PurifierRecipeWrapper;
import com.invadermonky.survivaltools.crafting.purifier.PurifierRecipe;
import com.invadermonky.survivaltools.crafting.purifier.PurifierRecipeRegistry;
import com.invadermonky.survivaltools.registry.ModBlocksST;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.Item;
import org.jetbrains.annotations.NotNull;

@JEIPlugin
public class JEIPluginST implements IModPlugin {
    public static PurifierCategory purifierCategory;
    public boolean isPurifierCategoryEnabled;

    @SuppressWarnings("ConstantConditions")
    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
        this.isPurifierCategoryEnabled = ModBlocksST.isBlockEnabled(ModBlocksST.SOLID_FUEL_PURIFIER) || ModBlocksST.isBlockEnabled(ModBlocksST.POWERED_PURIFIER);

        if (this.isPurifierCategoryEnabled) {
            registry.addRecipeCategories(purifierCategory = new PurifierCategory(guiHelper));
        }
    }

    @Override
    public void register(@NotNull IModRegistry registry) {
        if (this.isPurifierCategoryEnabled) {
            this.handlePurifierRecipes(registry);
            registry.addRecipeClickArea(GuiSolidFuelPurifier.class, 77, 43, 22, 17, PurifierCategory.UID);
            registry.addRecipeClickArea(GuiPoweredPurifier.class, 77, 43, 22, 17, PurifierCategory.UID);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void handlePurifierRecipes(IModRegistry registry) {
        registry.handleRecipes(PurifierRecipe.class, PurifierRecipeWrapper::new, PurifierCategory.UID);
        if (ModBlocksST.isBlockEnabled(ModBlocksST.SOLID_FUEL_PURIFIER)) {
            registry.addRecipeCatalyst(Item.getItemFromBlock(ModBlocksST.SOLID_FUEL_PURIFIER).getDefaultInstance(), PurifierCategory.UID);
        }
        if (ModBlocksST.isBlockEnabled(ModBlocksST.POWERED_PURIFIER)) {
            registry.addRecipeCatalyst(Item.getItemFromBlock(ModBlocksST.POWERED_PURIFIER).getDefaultInstance(), PurifierCategory.UID);
        }
        registry.addRecipes(PurifierRecipeRegistry.getPurifierRecipes(), PurifierCategory.UID);
    }
}
