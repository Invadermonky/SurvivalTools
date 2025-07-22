package com.invadermonky.survivaltools.survivalmods;

import com.charles445.simpledifficulty.api.*;
import com.charles445.simpledifficulty.api.config.QuickConfig;
import com.charles445.simpledifficulty.api.temperature.ITemperatureCapability;
import com.charles445.simpledifficulty.api.temperature.TemperatureUtil;
import com.charles445.simpledifficulty.api.thirst.IThirstCapability;
import com.invadermonky.survivaltools.api.ISurvivalMod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;

public class SurvivalModSD implements ISurvivalMod {
    @Override
    public ItemStack getPurifiedWaterBottleStack() {
        return new ItemStack(SDItems.purifiedWaterBottle);
    }

    @Override
    public ItemStack getWaterFilterStack() {
        return new ItemStack(SDItems.charcoalFilter);
    }

    @Override
    public ItemStack getCoolerStack() {
        return new ItemStack(SDBlocks.chiller);
    }

    @Override
    public ItemStack getHeaterStack() {
        return new ItemStack(SDBlocks.heater);
    }

    @Override
    public ItemStack getThermometerStack() {
        return new ItemStack(SDItems.thermometer);
    }

    @Override
    public Fluid getPurifiedWater() {
        return SDFluids.purifiedWater;
    }

    @Override
    public boolean isPurifiedWaterBottle(ItemStack stack) {
        return ItemStack.areItemsEqual(stack, this.getPurifiedWaterBottleStack());
    }

    @Override
    public boolean isTemperatureFeatureEnabled() {
        return QuickConfig.isTemperatureEnabled();
    }

    @Override
    public boolean stabilizePlayerTemperature(EntityPlayer player, int maxCooling, int maxHeating) {
        boolean did = false;
        if (this.isTemperatureFeatureEnabled()) {
            ITemperatureCapability tempData = SDCapabilities.getTemperatureData(player);
            int tempTarget = TemperatureUtil.getPlayerTargetTemperature(player);
            //The temperature the cooling will reach
            int coolTo = maxCooling <= -1 ? 13 : Math.max(13, tempTarget - maxCooling);
            //The temperature the heating will reach
            int heatTo = maxHeating <= -1 ? 12 : Math.min(12, tempTarget + maxHeating);
            if (tempData.getTemperatureLevel() < heatTo) {
                tempData.addTemperatureLevel(2);
                did = true;
            } else if (tempData.getTemperatureLevel() > coolTo) {
                tempData.addTemperatureLevel(-2);
                did = true;
            }
        }
        return did;
    }

    @Override
    public boolean clearTemperatureDebuffs(EntityPlayer player) {
        boolean did = false;
        if (player.isPotionActive(SDPotions.hyperthermia)) {
            player.removePotionEffect(SDPotions.hyperthermia);
            did = true;
        }
        if (player.isPotionActive(SDPotions.hypothermia)) {
            player.removePotionEffect(SDPotions.hypothermia);
            did = true;
        }
        return did;
    }

    @Override
    public boolean isThirstFeatureEnabled() {
        return QuickConfig.isThirstEnabled();
    }

    @Override
    public boolean isPlayerThirsty(EntityPlayer player) {
        return SDCapabilities.getThirstData(player).isThirsty();
    }

    @Override
    public int getMissingThirst(EntityPlayer player) {
        return 20 - SDCapabilities.getThirstData(player).getThirstLevel();
    }

    @Override
    public void hydratePlayer(EntityPlayer player, int amount, float saturation) {
        IThirstCapability thirstData = SDCapabilities.getThirstData(player);
        thirstData.addThirstLevel(amount);
        thirstData.addThirstSaturation(saturation);
    }
}
