package com.invadermonky.survivaltools.crafting.purifier;

import com.google.common.base.Preconditions;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class PurifierRecipe {
    protected FluidStack inputFluid;
    protected FluidStack outputFluid;
    protected Ingredient filterIngredient;
    protected int recipeDuration;

    public PurifierRecipe(FluidStack inputFluid, FluidStack outputFluid, @Nullable Ingredient filterIngredient, int recipeDuration) throws IllegalArgumentException {
        Preconditions.checkArgument(verifyFluid(inputFluid), "Input fluid cannot be null");
        Preconditions.checkArgument(verifyFluid(outputFluid), "Output fluid cannot be null");
        Preconditions.checkArgument(recipeDuration > 0, "Recipe duration must be greater than 0");

        this.inputFluid = inputFluid;
        this.outputFluid = outputFluid;
        this.filterIngredient = filterIngredient != null ? filterIngredient : Ingredient.EMPTY;
        this.recipeDuration = recipeDuration;
    }

    public PurifierRecipe(FluidStack inputFluid, FluidStack outputFluid, @Nullable Ingredient filterIngredient) throws IllegalArgumentException {
        this(inputFluid, outputFluid, filterIngredient, 400);
    }

    public PurifierRecipe(FluidStack inputFluid, FluidStack outputFluid) throws IllegalArgumentException {
        this(inputFluid, outputFluid, Ingredient.EMPTY);
    }

    private static boolean verifyFluid(FluidStack fluidStack) {
        return fluidStack != null && fluidStack.getFluid() != null && fluidStack.amount > 0;
    }

    public FluidStack getInputFluid() {
        return inputFluid;
    }

    public Ingredient getFilterIngredient() {
        return filterIngredient;
    }

    public FluidStack getOutputFluid() {
        return outputFluid;
    }

    public int getRecipeDuration() {
        return recipeDuration;
    }

    public boolean matches(@NotNull FluidStack inputFluid, @NotNull ItemStack filterStack) {
        return inputFluid.getFluid() == this.inputFluid.getFluid()
                && inputFluid.amount >= (this.inputFluid.amount / this.recipeDuration)
                && (this.filterIngredient == Ingredient.EMPTY || this.filterIngredient.test(filterStack));
    }

    @Override
    public int hashCode() {
        return Objects.hash(getInputFluid().getFluid(), getFilterIngredient());
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof PurifierRecipe))
            return false;
        PurifierRecipe that = (PurifierRecipe) object;
        boolean fluidMatch = getInputFluid().getFluid() == that.getInputFluid().getFluid();
        boolean filterMatch = false;
        for (ItemStack stack : this.filterIngredient.getMatchingStacks()) {
            if (that.getFilterIngredient().test(stack)) {
                filterMatch = true;
                break;
            }
        }
        return fluidMatch && filterMatch;
    }
}
