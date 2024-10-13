package com.invadermonky.survivaltools.api.fluid;

import com.invadermonky.survivaltools.util.helpers.SurvivalHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.invadermonky.survivaltools.util.libs.LibTags.TAG_FLUID;

public interface IPurifiedFluidContainerItem extends IFluidContainerItem {
    int getMaxFluidCapacity(ItemStack stack);

    default NBTTagCompound getFluidTagCompound(ItemStack stack) {
        if(!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        NBTTagCompound tag = stack.getTagCompound();
        if(!tag.hasKey(TAG_FLUID)) {
            tag.setInteger(TAG_FLUID, 0);
        }
        return stack.getTagCompound();
    }

    default int getFluidAmountStored(ItemStack stack) {
        return this.getFluidTagCompound(stack).getInteger(TAG_FLUID);
    }

    default void setFluidAmountStored(ItemStack stack, int fluidAmount) {
        this.getFluidTagCompound(stack).setInteger(TAG_FLUID, fluidAmount);
    }

    default void setFluidFull(ItemStack stack) {
        this.setFluidAmountStored(stack, this.getMaxFluidCapacity(stack));
    }

    @Nonnull
    @Override
    default ItemStack getContainer(ItemStack stack) {
        return stack.copy();
    }

    @Override
    default IFluidTankProperties[] getTankProperties(ItemStack stack) {
        return new IFluidTankProperties[] { new PurifiedFluidTankProperties(this.getFluidAmountStored(stack), this.getMaxFluidCapacity(stack)) };
    }

    @Override
    default int fill(ItemStack stack, FluidStack resource, boolean doFill) {
        if(resource != null && SurvivalHelper.isPurifiedWater(resource.getFluid())) {
            int fluid = this.getFluidAmountStored(stack);
            int fluidReceived = Math.min(this.getMaxFluidCapacity(stack) - fluid, resource.amount);
            if(doFill) {
                fluid += fluidReceived;
                this.setFluidAmountStored(stack, fluid);
            }
            return fluidReceived;
        }
        return 0;
    }

    @Nullable
    @Override
    default FluidStack drain(ItemStack stack, FluidStack resource, boolean doDrain) {
        if(resource != null && resource.getFluid() == SurvivalHelper.getPurifiedWater()) {
            return this.drain(stack, resource.amount, doDrain);
        }
        return null;
    }

    @Nullable
    @Override
    default FluidStack drain(ItemStack stack, int maxDrain, boolean doDrain) {
        int fluid = this.getFluidAmountStored(stack);
        int fluidDrained = Math.min(fluid, maxDrain);
        if(doDrain) {
            fluid -= fluidDrained;
            this.setFluidAmountStored(stack, fluid);
        }
        return fluidDrained > 0 ? new FluidStack(SurvivalHelper.getPurifiedWater(), fluidDrained) : null;
    }
}
