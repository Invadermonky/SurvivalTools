package com.invadermonky.survivaltools.crafting.handheldpurifier;

import com.google.common.base.Preconditions;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.Objects;

public class HandheldPurifierRecipe {
    protected Fluid inputFluid;
    protected int outputAmount;
    protected boolean canFillBottles;

    public HandheldPurifierRecipe(Fluid inputFluid, int outputAmount, boolean canFillBottles) throws IllegalArgumentException {
        Preconditions.checkArgument(inputFluid != null, "Input fluid cannot be null");
        Preconditions.checkArgument(outputAmount > 0, "Output amount must be greater than 0");

        this.inputFluid = inputFluid;
        this.outputAmount = outputAmount;
        this.canFillBottles = canFillBottles;
    }

    public Fluid getInputFluid() {
        return inputFluid;
    }

    public int getOutputAmount() {
        return outputAmount;
    }

    public boolean canFillBottles() {
        return canFillBottles;
    }

    public boolean matches(FluidStack fluidStack) {
        return fluidStack != null && fluidStack.getFluid() == this.inputFluid && fluidStack.amount >= 1000;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getInputFluid());
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof HandheldPurifierRecipe))
            return false;
        HandheldPurifierRecipe that = (HandheldPurifierRecipe) object;
        return getInputFluid() == that.getInputFluid();
    }
}
