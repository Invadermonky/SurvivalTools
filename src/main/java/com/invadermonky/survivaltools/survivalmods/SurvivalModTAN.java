package com.invadermonky.survivaltools.survivalmods;

import com.invadermonky.survivaltools.api.ISurvivalMod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import toughasnails.api.TANBlocks;
import toughasnails.api.TANPotions;
import toughasnails.api.config.GameplayOption;
import toughasnails.api.config.SyncedConfig;
import toughasnails.api.item.TANItems;
import toughasnails.api.stat.capability.ITemperature;
import toughasnails.api.stat.capability.IThirst;
import toughasnails.api.temperature.Temperature;
import toughasnails.api.temperature.TemperatureHelper;
import toughasnails.api.thirst.ThirstHelper;

public class SurvivalModTAN implements ISurvivalMod {

    @Override
    public ItemStack getPurifiedWaterBottleStack() {
        return new ItemStack(TANItems.purified_water_bottle);
    }

    @Override
    public ItemStack getWaterFilterStack() {
        return new ItemStack(TANItems.charcoal_filter);
    }

    @Override
    public ItemStack getCoolerStack() {
        return new ItemStack(TANBlocks.temperature_coil, 1, 0);
    }

    @Override
    public ItemStack getHeaterStack() {
        return new ItemStack(TANBlocks.temperature_coil, 1, 1);
    }

    @Override
    public ItemStack getThermometerStack() {
        return new ItemStack(TANItems.thermometer);
    }

    @Override
    public Fluid getPurifiedWater() {
        return TANBlocks.purified_water_fluid;
    }

    @Override
    public boolean isPurifiedWaterBottle(ItemStack stack) {
        return ItemStack.areItemsEqual(stack, this.getPurifiedWaterBottleStack());
    }

    @Override
    public boolean isTemperatureFeatureEnabled() {
        return SyncedConfig.getBooleanValue(GameplayOption.ENABLE_TEMPERATURE);
    }

    @Override
    public boolean stabilizePlayerTemperature(EntityPlayer player, int maxCooling, int maxHeating) {
        boolean did = false;
        if (this.isTemperatureFeatureEnabled()) {
            ITemperature temperature = TemperatureHelper.getTemperatureData(player);
            int currTemp = temperature.getTemperature().getRawValue();
            int tempTarget = TemperatureHelper.getTemperatureData(player).getPlayerTarget(player);
            //The temperature the cooling will reach
            int coolTo = maxCooling <= -1 ? 13 : Math.max(13, tempTarget - maxCooling);
            //The temperature the heating will reach
            int heatTo = maxHeating <= -1 ? 12 : Math.min(12, tempTarget + maxHeating);
            if (currTemp < heatTo) {
                temperature.setTemperature(new Temperature(currTemp + 1));
                did = true;
            } else if (currTemp > coolTo) {
                temperature.setTemperature(new Temperature(currTemp - 1));
                did = true;
            }
        }
        return did;
    }

    @Override
    public boolean clearTemperatureDebuffs(EntityPlayer player) {
        boolean did = false;
        if (player.isPotionActive(TANPotions.hyperthermia)) {
            player.removePotionEffect(TANPotions.hyperthermia);
            did = true;
        }
        if (player.isPotionActive(TANPotions.hypothermia)) {
            player.removePotionEffect(TANPotions.hypothermia);
            did = true;
        }
        return did;
    }

    @Override
    public boolean isThirstFeatureEnabled() {
        return SyncedConfig.getBooleanValue(GameplayOption.ENABLE_THIRST);
    }

    @Override
    public boolean isPlayerThirsty(EntityPlayer player) {
        IThirst thirst = ThirstHelper.getThirstData(player);
        return thirst.getThirst() < 20;
    }

    @Override
    public int getMissingThirst(EntityPlayer player) {
        return 20 - ThirstHelper.getThirstData(player).getThirst();
    }

    @Override
    public void hydratePlayer(EntityPlayer player, int amount, float saturation) {
        IThirst thirst = ThirstHelper.getThirstData(player);
        thirst.addStats(amount, saturation);
    }
}
