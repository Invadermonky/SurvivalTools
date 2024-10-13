package com.invadermonky.survivaltools.util.helpers;

import com.charles445.simpledifficulty.api.SDBlocks;
import com.charles445.simpledifficulty.api.SDItems;
import com.invadermonky.survivaltools.util.libs.ModIds;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import toughasnails.api.TANBlocks;
import toughasnails.api.item.TANItems;

public class SurvivalItemHelper {
    public static ItemStack getPurifiedWaterBottleStack() {
        if(ModIds.simpledifficulty.isLoaded) {
            return new ItemStack(SDItems.purifiedWaterBottle);
        } else if(ModIds.tough_as_nails.isLoaded) {
            return new ItemStack(TANItems.purified_water_bottle);
        } else {
            return PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.WATER);
        }
    }

    public static boolean isPurifiedWaterBottle(ItemStack stack) {
        boolean isPurifiedBottle = false;
        if(ModIds.simpledifficulty.isLoaded) {
            isPurifiedBottle = stack.getItem() == SDItems.purifiedWaterBottle;
        }
        if(ModIds.tough_as_nails.isLoaded && !isPurifiedBottle) {
            isPurifiedBottle = stack.getItem() == TANItems.purified_water_bottle;
        }
        if(!ModIds.simpledifficulty.isLoaded && !ModIds.tough_as_nails.isLoaded) {
            isPurifiedBottle = PotionUtils.getPotionFromItem(stack) == PotionTypes.WATER;
        }
        return isPurifiedBottle;
    }

    public static ItemStack getCharcoalFilterStack() {
        if(ModIds.simpledifficulty.isLoaded) {
            return new ItemStack(SDItems.charcoalFilter);
        } else if(ModIds.tough_as_nails.isLoaded) {
            return new ItemStack(TANItems.charcoal_filter);
        }
        return new ItemStack(Items.COAL, 1, 1);
    }

    public static ItemStack getCoolerStack() {
        if(ModIds.simpledifficulty.isLoaded) {
            return new ItemStack(SDBlocks.chiller);
        } else if(ModIds.tough_as_nails.isLoaded) {
            return new ItemStack(TANBlocks.temperature_coil, 1, 0);
        }
        return new ItemStack(Blocks.PACKED_ICE);
    }

    public static ItemStack getHeaterStack() {
        if(ModIds.simpledifficulty.isLoaded) {
            return new ItemStack(SDBlocks.heater);
        } else if(ModIds.tough_as_nails.isLoaded) {
            return new ItemStack(TANBlocks.temperature_coil, 1, 1);
        }
        return new ItemStack(Blocks.MAGMA);
    }

    public static ItemStack getThermometerStack() {
        if(ModIds.simpledifficulty.isLoaded) {
            return new ItemStack(SDItems.thermometer);
        } else if(ModIds.tough_as_nails.isLoaded) {
            return new ItemStack(TANItems.thermometer);
        }
        return new ItemStack(Items.COMPARATOR);
    }
}
