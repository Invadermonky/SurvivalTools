package com.invadermonky.survivaltools.survivalmods;

import com.invadermonky.survivaltools.api.ISurvivalMod;
import com.invadermonky.survivaltools.config.ConfigHandlerST;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class SurvivalModVanilla implements ISurvivalMod {
    @Override
    public ItemStack getPurifiedWaterBottleStack() {
        return PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.WATER);
    }

    @Override
    public ItemStack getWaterFilterStack() {
        return new ItemStack(Items.COAL, 1, 1);
    }

    @Override
    public ItemStack getCoolerStack() {
        return new ItemStack(Blocks.PACKED_ICE);
    }

    @Override
    public ItemStack getHeaterStack() {
        return new ItemStack(Blocks.MAGMA);
    }

    @Override
    public ItemStack getThermometerStack() {
        return new ItemStack(Items.COMPARATOR);
    }

    @Override
    public Fluid getPurifiedWater() {
        return FluidRegistry.WATER;
    }

    @Override
    public boolean isPurifiedWaterBottle(ItemStack stack) {
        ItemStack bottleStack = this.getPurifiedWaterBottleStack();
        return ItemStack.areItemsEqual(stack, bottleStack) && stack.hasTagCompound() && bottleStack.hasTagCompound() && stack.getTagCompound() == bottleStack.getTagCompound();
    }

    @Override
    public boolean isTemperatureFeatureEnabled() {
        return ConfigHandlerST.general.forceLoadTemperatureFeatures;
    }

    @Override
    public boolean stabilizePlayerTemperature(EntityPlayer player, int maxCooling, int maxHeating) {
        return false;
    }

    @Override
    public boolean clearTemperatureDebuffs(EntityPlayer player) {
        return false;
    }

    @Override
    public boolean isThirstFeatureEnabled() {
        return ConfigHandlerST.general.forceLoadThirstFeatures;
    }

    @Override
    public boolean isPlayerThirsty(EntityPlayer player) {
        return false;
    }

    @Override
    public int getMissingThirst(EntityPlayer player) {
        return 0;
    }

    @Override
    public void hydratePlayer(EntityPlayer player, int amount, float saturation) {

    }
}
