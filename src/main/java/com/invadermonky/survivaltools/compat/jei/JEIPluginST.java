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

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
        this.isPurifierCategoryEnabled = ModBlocksST.solid_fuel_purifier.isEnabled() || ModBlocksST.powered_purifier.isEnabled();

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

    private void handlePurifierRecipes(IModRegistry registry) {
        registry.handleRecipes(PurifierRecipe.class, PurifierRecipeWrapper::new, PurifierCategory.UID);
        if (ModBlocksST.solid_fuel_purifier.isEnabled()) {
            registry.addRecipeCatalyst(Item.getItemFromBlock(ModBlocksST.solid_fuel_purifier).getDefaultInstance(), PurifierCategory.UID);
        }
        if (ModBlocksST.powered_purifier.isEnabled()) {
            registry.addRecipeCatalyst(Item.getItemFromBlock(ModBlocksST.powered_purifier).getDefaultInstance(), PurifierCategory.UID);
        }
        registry.addRecipes(PurifierRecipeRegistry.getPurifierRecipes(), PurifierCategory.UID);
    }
}
