package com.invadermonky.survivaltools.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

public interface ISurvivalMod {
    /**
     * Returns a single item ItemStack for the purified water bottle for this specific survival mod.
     */
    ItemStack getPurifiedWaterBottleStack();

    /**
     * Returns a single item ItemStack for the water filter (usually Charcoal Filters) for this specific survival mod.
     */
    ItemStack getWaterFilterStack();

    /**
     * Returns a single item ItemStack for the AoE Chiller/Cooler for this specific survival mod.
     */
    ItemStack getCoolerStack();

    /**
     * Returns a single item ItemStack for the AoE Heater for this specific survival mod.
     */
    ItemStack getHeaterStack();

    /**
     * Returns a single item ItemStack for the thermometer for this specific survival mod.
     */
    ItemStack getThermometerStack();

    /**
     * Returns the Purified Water fluid for this specific survival mod.
     */
    Fluid getPurifiedWater();

    /**
     * @param fluidAmount
     * @return
     */
    @Nullable
    default FluidStack getPurifiedWaterStack(int fluidAmount) {
        return this.getPurifiedWater() != null ? new FluidStack(this.getPurifiedWater(), fluidAmount) : null;
    }

    /**
     * @param fluid
     * @return
     */
    default boolean isPurifiedWater(@Nullable Fluid fluid) {
        return fluid != null && this.getPurifiedWater() == fluid;
    }

    /**
     * @param fluidStack
     * @return
     */
    default boolean isPurifiedWater(@Nullable FluidStack fluidStack) {
        return fluidStack != null && this.isPurifiedWater(fluidStack.getFluid());
    }

    /**
     * @param stack
     * @return
     */
    boolean isPurifiedWaterBottle(ItemStack stack);

    /**
     * @return
     */
    boolean isTemperatureFeatureEnabled();

    /**
     * @param player
     */
    boolean stabilizePlayerTemperature(EntityPlayer player, int maxCooling, int maxHeating);

    /**
     * @param player
     */
    boolean clearTemperatureDebuffs(EntityPlayer player);

    /**
     * @return
     */
    boolean isThirstFeatureEnabled();

    /**
     * @param player
     * @return
     */
    boolean isPlayerThirsty(EntityPlayer player);

    /**
     * @param player
     * @return
     */
    int getMissingThirst(EntityPlayer player);

    /**
     * @param player
     * @param amount
     * @param saturation
     */
    void hydratePlayer(EntityPlayer player, int amount, float saturation);
}
