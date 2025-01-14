package com.invadermonky.survivaltools.api.fluid;

import com.invadermonky.survivaltools.api.SurvivalToolsAPI;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;

public class PurifiedFluidTankProperties implements IFluidTankProperties {
    private final FluidStack contents;
    private final int maxCapacity;

    public PurifiedFluidTankProperties(int currentAmount, int capacity) {
        this.contents = new FluidStack(SurvivalToolsAPI.getPurifiedWater(), currentAmount);
        this.maxCapacity = capacity;
    }

    @Nullable
    @Override
    public FluidStack getContents() {
        return this.contents;
    }

    @Override
    public int getCapacity() {
        return this.maxCapacity;
    }

    @Override
    public boolean canFill() {
        return false;
    }

    @Override
    public boolean canDrain() {
        return false;
    }

    @Override
    public boolean canFillFluidType(FluidStack fluidStack) {
        return fluidStack != null && SurvivalToolsAPI.isPurifiedWater(fluidStack.getFluid());
    }

    @Override
    public boolean canDrainFluidType(FluidStack fluidStack) {
        return fluidStack != null && SurvivalToolsAPI.isPurifiedWater(fluidStack.getFluid());
    }
}
