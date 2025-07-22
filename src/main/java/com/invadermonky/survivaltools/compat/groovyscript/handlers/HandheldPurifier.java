package com.invadermonky.survivaltools.compat.groovyscript.handlers;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.invadermonky.survivaltools.SurvivalTools;
import com.invadermonky.survivaltools.crafting.handheldpurifier.HandheldPurifierRecipe;
import com.invadermonky.survivaltools.crafting.handheldpurifier.HandheldPurifierRecipeRegistry;
import com.invadermonky.survivaltools.registry.ModRecipesST;
import net.minecraftforge.fluids.Fluid;

@RegistryDescription(linkGenerator = SurvivalTools.MOD_ID)
public class HandheldPurifier extends VirtualizedRegistry<HandheldPurifierRecipe> {
    @GroovyBlacklist
    @Override
    public void onReload() {
        HandheldPurifierRecipeRegistry.removeAll();
        ModRecipesST.registerHandheldPurifierRecipes();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example(value = "fluid('water').getFluid(), 1000, true"))
    public void addRecipe(Fluid input, int outputAmount, boolean canFillBottles) {
        HandheldPurifierRecipeRegistry.addRecipe(input, outputAmount, canFillBottles);
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL, example = @Example("fluid('water').getFluid()"))
    public void removeRecipe(Fluid input) {
        HandheldPurifierRecipeRegistry.removeRecipe(input);
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL)
    public void removeAll() {
        HandheldPurifierRecipeRegistry.removeAll();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<HandheldPurifierRecipe> streamRecipes() {
        return new SimpleObjectStream<>(HandheldPurifierRecipeRegistry.getRecipes());
    }
}
