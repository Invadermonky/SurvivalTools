package com.invadermonky.survivaltools.items;

import baubles.api.BaubleType;
import com.invadermonky.survivaltools.SurvivalTools;
import com.invadermonky.survivaltools.api.IAddition;
import com.invadermonky.survivaltools.api.items.AbstractEquipableBauble;
import com.invadermonky.survivaltools.compat.thaumcraft.ThaumcraftST;
import com.invadermonky.survivaltools.config.ConfigHandlerST;
import com.invadermonky.survivaltools.util.helpers.SurvivalHelper;
import com.invadermonky.survivaltools.util.helpers.SurvivalItemHelper;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.registries.IForgeRegistry;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.api.items.RechargeHelper;

import static com.invadermonky.survivaltools.util.libs.LibTags.TAG_ENERGY;

public class ItemThaumicRegulator extends AbstractEquipableBauble implements IRechargable, IAddition {
    public ItemThaumicRegulator() {
        this.setMaxStackSize(1);
    }

    @Override
    public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
        if(player.world.isRemote || !(player instanceof EntityPlayer) || ((EntityPlayer) player).isCreative())
            return;

        if(player.ticksExisted % ConfigHandlerST.thaumcraft.thaumic_regulator.delay == 0) {
            boolean hasCharge = RechargeHelper.getCharge(itemstack) > 0;
            boolean used = false;
            int cost = ConfigHandlerST.thaumcraft.thaumic_regulator.cost;

            int e = itemstack.hasTagCompound() ? itemstack.getTagCompound().getInteger(TAG_ENERGY) : 0;

            if(hasCharge && e > 0) {
                e -= cost;
                used = true;
            } else if (e <= 0 && RechargeHelper.consumeCharge(itemstack, player, 1)) {
                e += 60 - cost;
                used = true;
            }
            itemstack.setTagInfo(TAG_ENERGY, new NBTTagInt(e));


            if(used) {
                SurvivalHelper.stabilizePlayerTemperature((EntityPlayer) player);
            }
        }
    }

    @Override
    public boolean willAutoSync(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.CHARM;
    }

    @Override
    public int getMaxCharge(ItemStack itemStack, EntityLivingBase entityLivingBase) {
        return 200;
    }

    @Override
    public EnumChargeDisplay showInHud(ItemStack itemStack, EntityLivingBase entityLivingBase) {
        return EnumChargeDisplay.NORMAL;
    }

    @Override
    public void preInit() {
        ThaumcraftApi.registerResearchLocation(new ResourceLocation(SurvivalTools.MOD_ID, "research/thaumic_regulator"));
    }

    @Override
    public void registerRecipe(IForgeRegistry<IRecipe> registry) {
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(SurvivalTools.MOD_ID, "ThaumicRegulator"), new InfusionRecipe(
                "THAUMICREGULATOR",
                new ItemStack(ThaumcraftST.thaumic_regulator),
                5,
                (new AspectList()).add(Aspect.FIRE, 60).add(Aspect.COLD, 60).add(Aspect.MAN, 30),
                new ItemStack(ItemsTC.baubles, 1, 4),
                SurvivalItemHelper.getCoolerStack(),
                ThaumcraftApiHelper.makeCrystal(Aspect.FIRE),
                SurvivalItemHelper.getHeaterStack(),
                ThaumcraftApiHelper.makeCrystal(Aspect.COLD)
        ));
    }

    @Override
    public void registerModel(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
    }

    @Override
    public boolean isEnabled() {
        return ConfigHandlerST.thaumcraft.thaumic_regulator.enable && SurvivalHelper.isTemperatureFeatureEnabled();
    }
}
