package com.invadermonky.survivaltools.items;

import baubles.api.BaubleType;
import com.invadermonky.survivaltools.api.IAddition;
import com.invadermonky.survivaltools.api.SurvivalToolsAPI;
import com.invadermonky.survivaltools.api.items.AbstractEquipableBauble;
import com.invadermonky.survivaltools.config.ConfigHandlerST;
import com.invadermonky.survivaltools.util.helpers.StringHelper;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
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

import javax.annotation.Nullable;
import java.util.List;

public class ItemSeasonsRing extends AbstractEquipableBauble implements IManaUsingItem, IAddition {
    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase player) {
        super.onWornTick(stack, player);
        if(player.world.isRemote || !(player instanceof EntityPlayer) || ((EntityPlayer) player).isCreative())
            return;

        int manaCost = ConfigHandlerST.botania.ring_of_seasons.cost;
        boolean hasMana = ManaItemHandler.requestManaExact(stack, (EntityPlayer) player, manaCost, false);

        if(hasMana && player.ticksExisted % ConfigHandlerST.botania.ring_of_seasons.delay == 0) {
            SurvivalToolsAPI.stabilizePlayerTemperature((EntityPlayer) player, ConfigHandlerST.botania.ring_of_seasons.maxCooling, ConfigHandlerST.botania.ring_of_seasons.maxHeating);
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
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if(GuiScreen.isShiftKeyDown()) {
            int cooling = ConfigHandlerST.botania.ring_of_seasons.maxCooling;
            int heating = ConfigHandlerST.botania.ring_of_seasons.maxHeating;
            if(cooling > -1) {
                tooltip.add(I18n.format(StringHelper.getTranslationKey("max_cooling", "tooltip", "desc"), cooling));
            }
            if(heating > -1) {
                tooltip.add(I18n.format(StringHelper.getTranslationKey("max_heating", "tooltip", "desc"), heating));
            }
        }
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
        return ConfigHandlerST.botania.ring_of_seasons.enable && SurvivalToolsAPI.isTemperatureFeatureEnabled();
    }
}
