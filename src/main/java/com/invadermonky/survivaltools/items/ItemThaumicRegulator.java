package com.invadermonky.survivaltools.items;

import baubles.api.BaubleType;
import com.invadermonky.survivaltools.SurvivalTools;
import com.invadermonky.survivaltools.api.IAddition;
import com.invadermonky.survivaltools.api.IProxy;
import com.invadermonky.survivaltools.api.SurvivalToolsAPI;
import com.invadermonky.survivaltools.compat.thaumcraft.ThaumcraftST;
import com.invadermonky.survivaltools.config.ConfigHandlerST;
import com.invadermonky.survivaltools.items.base.AbstractEquipableBauble;
import com.invadermonky.survivaltools.util.helpers.StringHelper;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.NotNull;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.api.items.RechargeHelper;

import javax.annotation.Nullable;
import java.util.List;

import static com.invadermonky.survivaltools.util.libs.LibTags.TAG_ENERGY;

public class ItemThaumicRegulator extends AbstractEquipableBauble implements IRechargable, IAddition, IProxy {
    public ItemThaumicRegulator() {
        this.setMaxStackSize(1);
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.CHARM;
    }

    @Override
    public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
        if (player.world.isRemote || !(player instanceof EntityPlayer) || ((EntityPlayer) player).isCreative())
            return;

        if (player.ticksExisted % ConfigHandlerST.integrations.thaumcraft.thaumic_regulator.delay == 0) {
            boolean hasCharge = RechargeHelper.getCharge(itemstack) > 0;
            boolean used = false;
            int cost = ConfigHandlerST.integrations.thaumcraft.thaumic_regulator.cost;

            int e = itemstack.hasTagCompound() ? itemstack.getTagCompound().getInteger(TAG_ENERGY) : 0;

            if (hasCharge && e > 0) {
                e -= cost;
                used = true;
            } else if (e <= 0 && RechargeHelper.consumeCharge(itemstack, player, 1)) {
                e += 60 - cost;
                used = true;
            }
            itemstack.setTagInfo(TAG_ENERGY, new NBTTagInt(e));


            if (used) {
                SurvivalToolsAPI.stabilizePlayerTemperature((EntityPlayer) player, ConfigHandlerST.integrations.thaumcraft.thaumic_regulator.maxCooling, ConfigHandlerST.integrations.thaumcraft.thaumic_regulator.maxHeating);
            }
        }
    }

    @Override
    public boolean willAutoSync(ItemStack itemstack, EntityLivingBase player) {
        return true;
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
    public void addInformation(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<String> tooltip, @NotNull ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if (GuiScreen.isShiftKeyDown()) {
            int cooling = ConfigHandlerST.integrations.thaumcraft.thaumic_regulator.maxCooling;
            int heating = ConfigHandlerST.integrations.thaumcraft.thaumic_regulator.maxHeating;
            if (cooling > -1) {
                tooltip.add(I18n.format(StringHelper.getTranslationKey("max_cooling", "tooltip", "desc"), cooling));
            }
            if (heating > -1) {
                tooltip.add(I18n.format(StringHelper.getTranslationKey("max_heating", "tooltip", "desc"), heating));
            }
        }
    }

    @Override
    public @NotNull IRarity getForgeRarity(@NotNull ItemStack stack) {
        return EnumRarity.RARE;
    }

    /*
        IAddition
    */

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
                SurvivalToolsAPI.getCoolerStack(),
                ThaumcraftApiHelper.makeCrystal(Aspect.FIRE),
                SurvivalToolsAPI.getHeaterStack(),
                ThaumcraftApiHelper.makeCrystal(Aspect.COLD)
        ));
    }

    @Override
    public void registerModel(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
    }

    @Override
    public boolean isEnabled() {
        return ConfigHandlerST.integrations.thaumcraft.thaumic_regulator.enable && SurvivalToolsAPI.isTemperatureFeatureEnabled();
    }
}
