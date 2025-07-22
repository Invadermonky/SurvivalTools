package com.invadermonky.survivaltools.items;

import baubles.api.BaubleType;
import com.invadermonky.survivaltools.api.IAddition;
import com.invadermonky.survivaltools.api.SurvivalToolsAPI;
import com.invadermonky.survivaltools.config.ConfigHandlerST;
import com.invadermonky.survivaltools.items.base.AbstractEquipableBauble;
import com.invadermonky.survivaltools.util.helpers.StringHelper;
import com.invadermonky.survivaltools.util.libs.LibNames;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.items.ModItems;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ItemEnvironmentalAmulet extends AbstractEquipableBauble implements IAddition {
    @Override
    public void addInformation(@NotNull ItemStack stack, @Nullable World worldIn, List<String> tooltip, @NotNull ITooltipFlag flagIn) {
        tooltip.add(StringHelper.getTranslatedString(LibNames.ENVIRONMENTAL_AMULET, "tooltip", "desc"));
        if (GuiScreen.isShiftKeyDown()) {
            int cooling = ConfigHandlerST.integrations.natures_aura.environmental_amulet.maxCooling;
            int heating = ConfigHandlerST.integrations.natures_aura.environmental_amulet.maxHeating;
            if (cooling > -1) {
                tooltip.add(I18n.format(StringHelper.getTranslationKey("max_cooling", "tooltip", "desc"), cooling));
            }
            if (heating > -1) {
                tooltip.add(I18n.format(StringHelper.getTranslationKey("max_heating", "tooltip", "desc"), heating));
            }
        }
    }

    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.AMULET;
    }

    public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
        if (player.world.isRemote || !(player instanceof EntityPlayer) || ((EntityPlayer) player).isCreative())
            return;

        if (player.ticksExisted % ConfigHandlerST.integrations.natures_aura.environmental_amulet.delay == 0) {
            int cost = ConfigHandlerST.integrations.natures_aura.environmental_amulet.cost;

            if (NaturesAuraAPI.instance().extractAuraFromPlayer((EntityPlayer) player, cost, false)) {
                SurvivalToolsAPI.stabilizePlayerTemperature((EntityPlayer) player, ConfigHandlerST.integrations.natures_aura.environmental_amulet.maxCooling, ConfigHandlerST.integrations.natures_aura.environmental_amulet.maxHeating);
            }
        }
    }

    /*
        IModAddition Methods
    */

    @Override
    public void registerRecipe(IForgeRegistry<IRecipe> registry) {
        CraftingHelper.ShapedPrimer primer = CraftingHelper.parseShaped(
                " E ", "HGC", "RTR",
                'E', ModItems.TOKEN_EUPHORIA,
                'H', SurvivalToolsAPI.getHeaterStack(),
                'G', Items.GOLDEN_CHESTPLATE,
                'C', SurvivalToolsAPI.getCoolerStack(),
                'R', "dustRedstone",
                'T', SurvivalToolsAPI.getThermometerStack()
        );
        IRecipe recipe = new ShapedRecipes(this.getRegistryName().toString(), primer.width, primer.height, primer.input, new ItemStack(this));
        recipe.setRegistryName(this.getRegistryName());
        registry.register(recipe);
    }

    @Override
    public void registerModel(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
    }

    @Override
    public boolean isEnabled() {
        return ConfigHandlerST.integrations.natures_aura.environmental_amulet.enable && SurvivalToolsAPI.isTemperatureFeatureEnabled();
    }
}
