package com.invadermonky.survivaltools.items;

import baubles.api.BaubleType;
import com.invadermonky.survivaltools.api.IAddition;
import com.invadermonky.survivaltools.api.items.AbstractEquipableBauble;
import com.invadermonky.survivaltools.compat.embers.EmbersST;
import com.invadermonky.survivaltools.config.ConfigHandlerST;
import com.invadermonky.survivaltools.util.helpers.SurvivalHelper;
import com.invadermonky.survivaltools.util.helpers.SurvivalItemHelper;
import com.invadermonky.survivaltools.util.libs.LibNames;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.IForgeRegistry;
import teamroots.embers.RegistryManager;
import teamroots.embers.research.ResearchBase;
import teamroots.embers.research.ResearchManager;
import teamroots.embers.util.EmberInventoryUtil;

public class ItemMantleCloak extends AbstractEquipableBauble implements IAddition {
    public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
        if(player.world.isRemote || !(player instanceof EntityPlayer) || ((EntityPlayer) player).isCreative())
            return;

        if(player.ticksExisted % ConfigHandlerST.embers.mantle_cloak.delay == 0) {
            int cost = ConfigHandlerST.embers.mantle_cloak.cost;
            if(EmberInventoryUtil.getEmberTotal((EntityPlayer) player) >= cost) {
                SurvivalHelper.stabilizePlayerTemperature((EntityPlayer) player);
                EmberInventoryUtil.removeEmber((EntityPlayer) player, cost);
            }
        }
    }

    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.BODY;
    }

    /*
        IModAddition Methods
    */

    @Override
    public void postInit() {
        ResearchBase mantle_cloak = (new ResearchBase(LibNames.MANTLE_CLOAK, new ItemStack(EmbersST.mantle_cloak), 6.0, 7.0)).addAncestor(ResearchManager.cost_reduction);
        ResearchManager.subCategoryBaubles.addResearch(mantle_cloak);
    }

    @Override
    public void registerRecipe(IForgeRegistry<IRecipe> registry) {
        CraftingHelper.ShapedPrimer primer = CraftingHelper.parseShaped(
                "HWC", "APA", "A A",
                'H', SurvivalItemHelper.getHeaterStack(),
                'W', RegistryManager.ember_cluster,
                'C', SurvivalItemHelper.getCoolerStack(),
                'A', RegistryManager.ashen_cloth,
                'P', "plateDawnstone"
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
        return ConfigHandlerST.embers.mantle_cloak.enable && SurvivalHelper.isTemperatureFeatureEnabled();
    }
}
