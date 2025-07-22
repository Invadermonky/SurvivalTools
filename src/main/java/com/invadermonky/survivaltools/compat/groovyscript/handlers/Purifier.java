package com.invadermonky.survivaltools.compat.groovyscript.handlers;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.invadermonky.survivaltools.SurvivalTools;
import com.invadermonky.survivaltools.crafting.purifier.PurifierRecipe;
import com.invadermonky.survivaltools.crafting.purifier.PurifierRecipeRegistry;
import com.invadermonky.survivaltools.registry.ModRecipesST;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

@RegistryDescription(linkGenerator = SurvivalTools.MOD_ID)
public class Purifier extends VirtualizedRegistry<PurifierRecipe> {
    @GroovyBlacklist
    @Override
    public void onReload() {
        PurifierRecipeRegistry.removeAll();
        ModRecipesST.registerPurifierRecipes();
    }

    @MethodDescription(
            type = MethodDescription.Type.ADDITION,
            example = @Example("fluid('water') * 1000, fluid('purified_water') * 1000, item('minecraft:paper'), 800"),
            description = "groovyscript.wiki.survivaltools.purifier.addRecipe1"
    )
    public void addRecipe(FluidStack input, FluidStack output, IIngredient filter, int recipeDuration) {
        this.recipeBuilder()
                .setInputFluid(input)
                .setOutputFluid(output)
                .setFilterIngredient(filter)
                .setRecipeDuration(recipeDuration)
                .register();
    }

    @MethodDescription(
            type = MethodDescription.Type.ADDITION,
            example = @Example("fluid('water') * 1000, fluid('purified_water') * 1000, item('minecraft:paper')"),
            description = "groovyscript.wiki.survivaltools.purifier.addRecipe2"
    )
    public void addRecipe(FluidStack input, FluidStack output, IIngredient filter) {
        addRecipe(input, output, filter, 800);
    }

    @MethodDescription(
            type = MethodDescription.Type.ADDITION,
            example = @Example("fluid('water') * 1000, fluid('purified_water') * 1000"),
            description = "groovyscript.wiki.survivaltools.purifier.addRecipe3"
    )
    public void addRecipe(FluidStack input, FluidStack output) {
        addRecipe(input, output, IIngredient.EMPTY);
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL, example = @Example("fluid('water').getFluid()"))
    public void removeByInput(Fluid input) {
        PurifierRecipeRegistry.removeByInput(input);
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL, example = @Example("fluid('purified_water') * 1000"))
    public void removeByOutput(FluidStack output) {
        PurifierRecipeRegistry.removeByOutput(output);
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL)
    public void removeAll() {
        PurifierRecipeRegistry.removeAll();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<PurifierRecipe> streamRecipes() {
        return new SimpleObjectStream<>(PurifierRecipeRegistry.getPurifierRecipes());
    }

    @RecipeBuilderDescription(
            example = {
                    @Example(".setInputFluid(fluid('water') * 1000).setOutputFluid(fluid('purified_water') * 1000).setFilterIngredient(item('minecraft:paper')).setRecipeDuration(800)"),
                    @Example(".setInputFluid(fluid('water') * 1000).setOutputFluid(fluid('purified_water') * 1000).setFilterIngredient(item('minecraft:paper'))"),
                    @Example(".setInputFluid(fluid('water') * 1000).setOutputFluid(fluid('purified_water') * 1000).setRecipeDuration(800)"),
                    @Example(".setInputFluid(fluid('water') * 1000).setOutputFluid(fluid('purified_water') * 1000)")
            }
    )
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<PurifierRecipe> {
        @Property
        private FluidStack inputFluid;
        @Property
        private FluidStack outputFluid;
        @Property
        private IIngredient filterIngredient;
        @Property(comp = @Comp(gt = 0))
        private int recipeDuration;

        public RecipeBuilder() {
            this.inputFluid = null;
            this.outputFluid = null;
            this.filterIngredient = IIngredient.EMPTY;
            this.recipeDuration = 800;
        }

        @RecipeBuilderMethodDescription(field = "inputFluid")
        public RecipeBuilder setInputFluid(FluidStack inputFluid) {
            this.inputFluid = inputFluid;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "outputFluid")
        public RecipeBuilder setOutputFluid(FluidStack outputFluid) {
            this.outputFluid = outputFluid;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "filterIngredient", priority = 1001)
        public RecipeBuilder setFilterIngredient(IIngredient filterIngredient) {
            this.filterIngredient = filterIngredient;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "recipeDuration", priority = 1002)
        public RecipeBuilder setRecipeDuration(int recipeDuration) {
            this.recipeDuration = recipeDuration;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Purifier recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            msg.add(this.inputFluid == null || this.inputFluid.getFluid() == null, "Input fluid cannot be null");
            msg.add(this.outputFluid == null || this.outputFluid.getFluid() == null, "Output fluid cannot be null");
            msg.add(this.recipeDuration <= 0, "Recipe duration cannot be less than or equal to 0");
        }

        @Override
        public @Nullable PurifierRecipe register() {
            if (this.validate()) {
                PurifierRecipe recipe = new PurifierRecipe(
                        this.inputFluid,
                        this.outputFluid,
                        this.filterIngredient.toMcIngredient(),
                        this.recipeDuration
                );
                PurifierRecipeRegistry.addRecipe(recipe);
                return recipe;
            }
            return null;
        }
    }
}
