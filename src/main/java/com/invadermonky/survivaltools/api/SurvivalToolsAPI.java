package com.invadermonky.survivaltools.api;

import com.invadermonky.survivaltools.util.helpers.SurvivalHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

public class SurvivalToolsAPI {
    public static void registerSurvivalMod(ISurvivalMod survivalMod) {
        SurvivalHelper.registerSurvivalMod(survivalMod);
    }

    public static ItemStack getPurifiedWaterBottleStack() {
        return SurvivalHelper.internal.getPurifiedWaterBottleStack();
    }

    public static ItemStack getWaterFilterStack() {
        return SurvivalHelper.internal.getWaterFilterStack();
    }

    public static ItemStack getCoolerStack() {
        return SurvivalHelper.internal.getCoolerStack();
    }

    public static ItemStack getHeaterStack() {
        return SurvivalHelper.internal.getHeaterStack();
    }

    public static ItemStack getThermometerStack() {
        return SurvivalHelper.internal.getThermometerStack();
    }

    public static Fluid getPurifiedWater() {
        return SurvivalHelper.internal.getPurifiedWater();
    }

    public static FluidStack getPurifiedWaterStack(int fluidAmount) {
        return SurvivalHelper.internal.getPurifiedWaterStack(fluidAmount);
    }

    public static boolean isPurifiedWater(@Nullable Fluid fluid) {
        return SurvivalHelper.internal.isPurifiedWater(fluid);
    }

    public static boolean isPurifiedWater(@Nullable FluidStack fluidStack) {
        return SurvivalHelper.internal.isPurifiedWater(fluidStack);
    }

    public static boolean isPurifiedWaterBottle(ItemStack stack) {
        return SurvivalHelper.internal.isPurifiedWaterBottle(stack);
    }

    public static boolean isTemperatureFeatureEnabled() {
        return SurvivalHelper.internal.isTemperatureFeatureEnabled();
    }

    public static boolean stabilizePlayerTemperature(EntityPlayer player, int maxCooling, int maxHeating) {
        return SurvivalHelper.internal.stabilizePlayerTemperature(player, maxCooling, maxHeating);
    }

    public static boolean clearTemperatureDebuffs(EntityPlayer player) {
        return SurvivalHelper.internal.clearTemperatureDebuffs(player);
    }

    public static boolean isThirstFeatureEnabled() {
        return SurvivalHelper.internal.isThirstFeatureEnabled();
    }

    public static boolean isPlayerThirsty(EntityPlayer player) {
        return SurvivalHelper.internal.isPlayerThirsty(player);
    }

    public static void hydratePlayer(EntityPlayer player, int amount, float saturation) {
        SurvivalHelper.internal.hydratePlayer(player, amount, saturation);
    }

    public static int getMissingThirst(EntityPlayer player) {
        return SurvivalHelper.internal.getMissingThirst(player);
    }
}
