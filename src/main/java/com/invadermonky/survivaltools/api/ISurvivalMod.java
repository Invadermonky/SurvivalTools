package com.invadermonky.survivaltools.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

public interface ISurvivalMod {
    /**
     * Returns a single item ItemStack for the purified water bottle for this specific survival mod. This method
     * must return a new instance of this ItemStack every time it is called.
     */
    ItemStack getPurifiedWaterBottleStack();

    /**
     * Returns a single item ItemStack for the water filter (usually Charcoal Filters) for this specific survival mod.
     * This method must return a new instance of this ItemStack every time it is called.
     */
    ItemStack getWaterFilterStack();

    /**
     * Returns a single item ItemStack for the AoE Chiller/Cooler for this specific survival mod. This method
     * must return a new instance of this ItemStack every time it is called.
     */
    ItemStack getCoolerStack();

    /**
     * Returns a single item ItemStack for the AoE Heater for this specific survival mod. This method
     * must return a new instance of this ItemStack every time it is called.
     */
    ItemStack getHeaterStack();

    /**
     * Returns a single item ItemStack for the thermometer for this specific survival mod. This method
     * must return a new instance of this ItemStack every time it is called.
     */
    ItemStack getThermometerStack();

    /**
     * Returns the Purified Water fluid for this specific survival mod.
     */
    Fluid getPurifiedWater();

    /**
     * Returns a FluidStack of the specified amount of purified water, or null if not purified fluid is registered.
     */
    @Nullable
    default FluidStack getPurifiedWaterStack(int fluidAmount) {
        return this.getPurifiedWater() != null ? new FluidStack(this.getPurifiedWater(), fluidAmount) : null;
    }

    /**
     * Returns true if the passed fluid is the purified water fluid for this survival mod.
     */
    default boolean isPurifiedWater(@Nullable Fluid fluid) {
        return fluid != null && this.getPurifiedWater().equals(fluid);
    }

    /**
     * Returns true if the passed FluidStack is the purified water fluid for this survival mod.
     */
    default boolean isPurifiedWater(@Nullable FluidStack fluidStack) {
        return fluidStack != null && this.isPurifiedWater(fluidStack.getFluid());
    }

    /**
     * Returns true if the passed item is an instance of the purified water bottle used by this survival mod.
     */
    boolean isPurifiedWaterBottle(ItemStack stack);

    /**
     * Returns true if the temperature feature of this survival mod is enabled. This method should account
     * for configuration settings within the survival mod itself.
     */
    boolean isTemperatureFeatureEnabled();

    /**
     * Attempts to stabilize the player's temperature, returning it to normal values.
     */
    boolean stabilizePlayerTemperature(EntityPlayer player, int maxCooling, int maxHeating);

    /**
     * Removes any temperature debuffs associated with exposure, such as heatstroke or frostbite.
     */
    boolean clearTemperatureDebuffs(EntityPlayer player);

    /**
     * Returns true if the thirst feature of this survival mod is enabled. This method should account
     * for configuration settings within the survival mod itself.
     */
    boolean isThirstFeatureEnabled();

    /**
     * Returns true if the player is missing thirst
     */
    boolean isPlayerThirsty(EntityPlayer player);

    /**
     * Returns the amount of thirst the player is currently missing.
     */
    int getMissingThirst(EntityPlayer player);

    /**
     * Hydrates the player, restoring missing thirst. The <code>amount</code> parameter used by this method
     * assumes a thirst system similar to Minecraft's hunger system, with a maximum thirst value of 20.
     */
    void hydratePlayer(EntityPlayer player, int amount, float saturation);
}
