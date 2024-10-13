package com.invadermonky.survivaltools.items;

import baubles.api.BaubleType;
import com.invadermonky.survivaltools.api.IAddition;
import com.invadermonky.survivaltools.api.items.AbstractEquipableBauble;
import com.invadermonky.survivaltools.config.ConfigHandlerST;
import com.invadermonky.survivaltools.util.helpers.SurvivalHelper;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.lexicon.LexiconCategory;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.mana.IManaUsingItem;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.common.lexicon.BasicLexiconEntry;
import vazkii.botania.common.lexicon.page.PageCraftingRecipe;
import vazkii.botania.common.lexicon.page.PageText;

public class ItemSeasonsRing extends AbstractEquipableBauble implements IManaUsingItem, IAddition {
    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase player) {
        super.onWornTick(stack, player);
        if(player.world.isRemote || !(player instanceof EntityPlayer) || ((EntityPlayer) player).isCreative())
            return;

        int manaCost = ConfigHandlerST.botania.ring_of_seasons.cost;
        boolean hasMana = ManaItemHandler.requestManaExact(stack, (EntityPlayer) player, manaCost, false);

        if(hasMana && player.ticksExisted % ConfigHandlerST.botania.ring_of_seasons.delay == 0) {
            SurvivalHelper.stabilizePlayerTemperature((EntityPlayer) player);
            ManaItemHandler.requestManaExact(stack, (EntityPlayer) player, manaCost, true);
        }
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.RING;
    }

    @Override
    public boolean usesMana(ItemStack itemStack) {
        return true;
    }

    @Override
    public void postInit() {
        LexiconCategory categoryBaubles = BotaniaAPI.categoryBaubles;
        LexiconEntry temperatureRing = new BasicLexiconEntry("seasonsRing", categoryBaubles);
        temperatureRing.setLexiconPages(new PageText("0"), new PageCraftingRecipe("1", this.getRegistryName()));
    }

    @Override
    public void registerModel(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
    }

    @Override
    public boolean isEnabled() {
        return ConfigHandlerST.botania.ring_of_seasons.enable && SurvivalHelper.isTemperatureFeatureEnabled();
    }
}
