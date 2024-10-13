package com.invadermonky.survivaltools.util.helpers;

import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.SDFluids;
import com.charles445.simpledifficulty.api.SDPotions;
import com.charles445.simpledifficulty.api.temperature.ITemperatureCapability;
import com.charles445.simpledifficulty.api.thirst.IThirstCapability;
import com.invadermonky.survivaltools.config.ConfigHandlerST;
import com.invadermonky.survivaltools.registry.ModSoundsST;
import com.invadermonky.survivaltools.util.libs.ModIds;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import toughasnails.api.TANBlocks;
import toughasnails.api.TANPotions;
import toughasnails.api.stat.capability.ITemperature;
import toughasnails.api.stat.capability.IThirst;
import toughasnails.api.temperature.Temperature;
import toughasnails.api.temperature.TemperatureHelper;
import toughasnails.api.thirst.ThirstHelper;

import javax.annotation.Nullable;

public class SurvivalHelper {
    public static boolean stabilizePlayerTemperature(EntityPlayer player) {
        boolean did = false;
        if(ModIds.simpledifficulty.isLoaded) {
            if(SDHelper.isSDTemperatureEnabled()) {
                ITemperatureCapability tempData = SDCapabilities.getTemperatureData(player);
                if (tempData.getTemperatureLevel() < 12) {
                    tempData.addTemperatureLevel(2);
                    did = true;
                } else if (tempData.getTemperatureLevel() > 13) {
                    tempData.addTemperatureLevel(-2);
                    did = true;
                }
            }
        }

        if(ModIds.tough_as_nails.isLoaded) {
            if (TANHelper.isTanTemperatureEnabled()) {
                ITemperature temperature = TemperatureHelper.getTemperatureData(player);
                int curTemp = temperature.getTemperature().getRawValue();
                if (curTemp < 12) {
                    temperature.setTemperature(new Temperature(curTemp + 1));
                    did = true;
                } else if (curTemp > 13) {
                    temperature.setTemperature(new Temperature(curTemp - 1));
                    did = true;
                }
            }
        }
        return did;
    }

    public static void clearTemperatureDebuffs(EntityPlayer player) {
        if(ModIds.simpledifficulty.isLoaded) {
            if(player.isPotionActive(SDPotions.hyperthermia)) {
                player.removePotionEffect(SDPotions.hyperthermia);
            }
            if(player.isPotionActive(SDPotions.hypothermia)) {
                player.removePotionEffect(SDPotions.hypothermia);
            }
        }
        if(ModIds.tough_as_nails.isLoaded) {
            if (player.isPotionActive(TANPotions.hyperthermia)) {
                player.removePotionEffect(TANPotions.hyperthermia);
            }
            if (player.isPotionActive(TANPotions.hypothermia)) {
                player.removePotionEffect(TANPotions.hypothermia);
            }
        }
    }

    public static boolean hydratePlayer(EntityPlayer player, int amount, float saturation) {
        boolean did = false;
        if(ModIds.simpledifficulty.isLoaded) {
            if(SDHelper.isSDThirstEnabled()) {
                IThirstCapability thirstData = SDCapabilities.getThirstData(player);
                thirstData.addThirstLevel(amount);
                thirstData.addThirstSaturation(saturation);
                did = true;
            }
        }
        if(ModIds.tough_as_nails.isLoaded) {
            if(TANHelper.isTanThirstEnabled()) {
                IThirst thirst = ThirstHelper.getThirstData(player);
                thirst.addStats(amount, saturation);
                did = true;
            }
        }
        if(did && player.world.rand.nextInt(200) == 0) {
            player.world.playSound(null, player.posX, player.posY, player.posZ, ModSoundsST.easter_egg, SoundCategory.PLAYERS, 1.0f, 1.0f);
            player.sendMessage(new TextComponentTranslation(StringHelper.getTranslationKey("drink", "chat", "easter_egg0")));
            player.sendMessage(new TextComponentTranslation(StringHelper.getTranslationKey("drink", "chat", "easter_egg1")));
            player.sendMessage(new TextComponentTranslation(StringHelper.getTranslationKey("drink", "chat", "easter_egg2")));
        }
        return did;
    }

    public static int getMissingThirst(EntityPlayer player) {
        int missingThirst = 20;
        if(ModIds.simpledifficulty.isLoaded) {
            missingThirst = 20 - SDCapabilities.getThirstData(player).getThirstLevel();
        }
        if(ModIds.tough_as_nails.isLoaded) {
            missingThirst = Math.min(missingThirst, 20 - ThirstHelper.getThirstData(player).getThirst());
        }
        return missingThirst;
    }

    public static boolean isThirsty(EntityPlayer player) {
        boolean isThirsty = false;
        if(ModIds.simpledifficulty.isLoaded) {
            IThirstCapability thirstData = SDCapabilities.getThirstData(player);
            isThirsty = thirstData.isThirsty();
        }
        if(ModIds.tough_as_nails.isLoaded && !isThirsty) {
            IThirst thirst = ThirstHelper.getThirstData(player);
            isThirsty = thirst.getThirst() < 20;
        }
        return isThirsty;
    }

    public static Fluid getPurifiedWater() {
        if(ModIds.simpledifficulty.isLoaded) {
            return SDFluids.purifiedWater;
        } else if(ModIds.tough_as_nails.isLoaded) {
            return TANBlocks.purified_water_fluid;
        }
        return FluidRegistry.WATER;
    }

    public static boolean isPurifiedWater(@Nullable Fluid fluid) {
        if(fluid == null) return false;
        boolean isPurifiedWater = false;
        if(ModIds.simpledifficulty.isLoaded) {
            isPurifiedWater = fluid == SDFluids.purifiedWater;
        }
        if(ModIds.tough_as_nails.isLoaded && !isPurifiedWater) {
            isPurifiedWater = fluid == TANBlocks.purified_water_fluid;
        }
        if(!ModIds.simpledifficulty.isLoaded && !ModIds.tough_as_nails.isLoaded) {
            isPurifiedWater = fluid == FluidRegistry.WATER;
        }
        return isPurifiedWater;
    }

    public static boolean isTemperatureFeatureEnabled() {
        boolean isEnabled = false;
        if(ModIds.simpledifficulty.isLoaded) {
            isEnabled = SDHelper.isSDTemperatureEnabled();
        }
        if(ModIds.tough_as_nails.isLoaded && !isEnabled) {
            isEnabled = TANHelper.isTanTemperatureEnabled();
        }

        return isEnabled || ConfigHandlerST.forceLoadTemperatureFeatures;
    }

    public static boolean isThirstFeatureEnabled() {
        boolean isEnabled = false;
        if(ModIds.simpledifficulty.isLoaded) {
            isEnabled = SDHelper.isSDThirstEnabled();
        }
        if(ModIds.tough_as_nails.isLoaded && !isEnabled) {
            isEnabled = TANHelper.isTanThirstEnabled();
        }

        return isEnabled || ConfigHandlerST.forceLoadThirstFeatures;
    }
}
