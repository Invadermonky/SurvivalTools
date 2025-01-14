package com.invadermonky.survivaltools.util.helpers;

import com.invadermonky.survivaltools.api.ISurvivalMod;
import com.invadermonky.survivaltools.survivalmods.SurvivalModVanilla;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SurvivalHelper implements ISurvivalMod {
    private static final List<ISurvivalMod> survivalMods = new ArrayList<>();
    private static final ISurvivalMod vanillaSurvivalMod = new SurvivalModVanilla();

    public static void registerSurvivalMod(ISurvivalMod survivalMod) {
        survivalMods.add(survivalMod);
    }

    public static final SurvivalHelper internal = new SurvivalHelper();

    @Override
    public ItemStack getPurifiedWaterBottleStack() {
        for(ISurvivalMod survivalMod : survivalMods) {
            return survivalMod.getPurifiedWaterBottleStack();
        }
        return vanillaSurvivalMod.getPurifiedWaterBottleStack();
    }

    @Override
    public ItemStack getWaterFilterStack() {
        for(ISurvivalMod survivalMod : survivalMods) {
            return survivalMod.getWaterFilterStack();
        }
        return vanillaSurvivalMod.getWaterFilterStack();
    }

    @Override
    public ItemStack getCoolerStack() {
        for(ISurvivalMod survivalMod : survivalMods) {
            return survivalMod.getCoolerStack();
        }
        return vanillaSurvivalMod.getCoolerStack();
    }

    @Override
    public ItemStack getHeaterStack() {
        for(ISurvivalMod survivalMod : survivalMods) {
            return survivalMod.getHeaterStack();
        }
        return vanillaSurvivalMod.getHeaterStack();
    }

    @Override
    public ItemStack getThermometerStack() {
        for(ISurvivalMod survivalMod : survivalMods) {
            return survivalMod.getThermometerStack();
        }
        return vanillaSurvivalMod.getThermometerStack();
    }

    @Override
    public Fluid getPurifiedWater() {
        for(ISurvivalMod survivalMod : survivalMods) {
            return survivalMod.getPurifiedWater();
        }
        return vanillaSurvivalMod.getPurifiedWater();
    }

    @Override
    public boolean isPurifiedWater(@Nullable Fluid fluid) {
        if(fluid != null) {
            if (survivalMods.isEmpty()) {
                return fluid == FluidRegistry.WATER;
            } else {
                for (ISurvivalMod survivalMod : survivalMods) {
                    if(survivalMod.isPurifiedWater(fluid)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean isPurifiedWaterBottle(ItemStack stack) {
        if(survivalMods.isEmpty()) {
            vanillaSurvivalMod.isPurifiedWaterBottle(stack);
        } else {
            for(ISurvivalMod survivalMod : survivalMods) {
                if(survivalMod.isPurifiedWaterBottle(stack)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isPlayerThirsty(EntityPlayer player) {
        for(ISurvivalMod survivalMod : survivalMods) {
            if(survivalMod.isPlayerThirsty(player)) {
                return true;
            }
        }
        return vanillaSurvivalMod.isPlayerThirsty(player);
    }

    @Override
    public boolean isTemperatureFeatureEnabled() {
        boolean enabled = false;
        for(ISurvivalMod survivalMod : survivalMods) {
            if(survivalMod.isTemperatureFeatureEnabled()) {
                enabled = true;
                break;
            }
        }
        return enabled || vanillaSurvivalMod.isTemperatureFeatureEnabled();
    }

    @Override
    public boolean isThirstFeatureEnabled() {
        boolean enabled = false;
        for(ISurvivalMod survivalMod : survivalMods) {
            if(survivalMod.isThirstFeatureEnabled()) {
                enabled = true;
                break;
            }
        }
        return enabled || vanillaSurvivalMod.isThirstFeatureEnabled();
    }

    @Override
    public boolean stabilizePlayerTemperature(EntityPlayer player, int maxCooling, int maxHeating) {
        boolean did = false;
        for(ISurvivalMod survivalMod : survivalMods) {
            if(survivalMod.stabilizePlayerTemperature(player, maxCooling, maxHeating)) {
                did = true;
            }
        }
        return did;
    }

    @Override
    public boolean clearTemperatureDebuffs(EntityPlayer player) {
        boolean did = false;
        for(ISurvivalMod survivalMod : survivalMods) {
            if(survivalMod.clearTemperatureDebuffs(player)) {
                did = true;
            }
        }
        return did;
    }

    @Override
    public int getMissingThirst(EntityPlayer player) {
        for(ISurvivalMod survivalMod : survivalMods) {
            return survivalMod.getMissingThirst(player);
        }
        return vanillaSurvivalMod.getMissingThirst(player);
    }

    @Override
    public void hydratePlayer(EntityPlayer player, int amount, float saturation) {
        for(ISurvivalMod survivalMod : survivalMods) {
            if(survivalMod.isPlayerThirsty(player))
                survivalMod.hydratePlayer(player, amount, saturation);
        }
    }
}
