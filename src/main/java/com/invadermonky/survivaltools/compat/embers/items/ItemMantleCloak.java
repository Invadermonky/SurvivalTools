package com.invadermonky.survivaltools.compat.embers.items;

import baubles.api.BaubleType;
import com.invadermonky.survivaltools.api.IProxy;
import com.invadermonky.survivaltools.api.SurvivalToolsAPI;
import com.invadermonky.survivaltools.api.items.AbstractEquipableBauble;
import com.invadermonky.survivaltools.config.ConfigHandlerST;
import com.invadermonky.survivaltools.registry.ModItemsST;
import com.invadermonky.survivaltools.util.helpers.StringHelper;
import com.invadermonky.survivaltools.util.libs.LibNames;
import com.invadermonky.survivaltools.util.libs.ModIds;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.NotNull;
import teamroots.embers.research.ResearchBase;
import teamroots.embers.research.ResearchManager;
import teamroots.embers.util.EmberInventoryUtil;

import javax.annotation.Nullable;
import java.util.List;

public class ItemMantleCloak extends AbstractEquipableBauble implements IProxy {
    public ItemMantleCloak() {
        super(LibNames.MANTLE_CLOAK);
    }

    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.BODY;
    }

    public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
        if (player.world.isRemote || !(player instanceof EntityPlayer) || ((EntityPlayer) player).isCreative())
            return;

        if (player.ticksExisted % ConfigHandlerST.integrations.embers.mantle_cloak.delay == 0) {
            int cost = ConfigHandlerST.integrations.embers.mantle_cloak.cost;
            if (EmberInventoryUtil.getEmberTotal((EntityPlayer) player) >= cost) {
                SurvivalToolsAPI.stabilizePlayerTemperature((EntityPlayer) player, ConfigHandlerST.integrations.embers.mantle_cloak.maxCooling, ConfigHandlerST.integrations.embers.mantle_cloak.maxHeating);
                EmberInventoryUtil.removeEmber((EntityPlayer) player, cost);
            }
        }
    }

    @Override
    public void addInformation(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<String> tooltip, @NotNull ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if (GuiScreen.isShiftKeyDown()) {
            int cooling = ConfigHandlerST.integrations.embers.mantle_cloak.maxCooling;
            int heating = ConfigHandlerST.integrations.embers.mantle_cloak.maxHeating;
            if (cooling > -1) {
                tooltip.add(I18n.format(StringHelper.getTranslationKey("max_cooling", "tooltip", "desc"), cooling));
            }
            if (heating > -1) {
                tooltip.add(I18n.format(StringHelper.getTranslationKey("max_heating", "tooltip", "desc"), heating));
            }
        }
    }

    /*
        IModAddition Methods
    */

    @SuppressWarnings("ConstantConditions")
    @Override
    public void postInit() {
        ResearchBase mantle_cloak = (new ResearchBase(LibNames.MANTLE_CLOAK, new ItemStack(ModItemsST.MANTLE_CLOAK), 6.0, 7.0)).addAncestor(ResearchManager.cost_reduction);
        ResearchManager.subCategoryBaubles.addResearch(mantle_cloak);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void registerRecipe(IForgeRegistry<IRecipe> registry) {
        CraftingHelper.ShapedPrimer primer = CraftingHelper.parseShaped(
                "HWC", "APA", "A A",
                'H', SurvivalToolsAPI.getHeaterStack(),
                'W', ForgeRegistries.ITEMS.getValue(new ResourceLocation(ModIds.embers.modId, "ember_cluster")),
                'C', SurvivalToolsAPI.getCoolerStack(),
                'A', ForgeRegistries.ITEMS.getValue(new ResourceLocation(ModIds.embers.modId, "ashen_cloth")),
                'P', "plateDawnstone"
        );
        IRecipe recipe = new ShapedRecipes(this.getRegistryName().toString(), primer.width, primer.height, primer.input, new ItemStack(this));
        recipe.setRegistryName(this.getRegistryName());
        registry.register(recipe);

    }

    @Override
    public boolean isEnabled() {
        return ConfigHandlerST.integrations.embers.mantle_cloak.enable && SurvivalToolsAPI.isTemperatureFeatureEnabled();
    }
}
