package com.invadermonky.survivaltools.items;

import WayofTime.bloodmagic.api.impl.BloodMagicAPI;
import WayofTime.bloodmagic.core.RegistrarBloodMagicItems;
import WayofTime.bloodmagic.item.sigil.ItemSigilToggleableBase;
import WayofTime.bloodmagic.util.helper.PlayerHelper;
import com.invadermonky.survivaltools.SurvivalTools;
import com.invadermonky.survivaltools.api.IAddition;
import com.invadermonky.survivaltools.api.IProxy;
import com.invadermonky.survivaltools.api.SurvivalToolsAPI;
import com.invadermonky.survivaltools.compat.bloodmagic.BloodMagicST;
import com.invadermonky.survivaltools.compat.bloodmagic.BloodMagicUtils;
import com.invadermonky.survivaltools.config.ConfigHandlerST;
import com.invadermonky.survivaltools.util.helpers.StringHelper;
import com.invadermonky.survivaltools.util.libs.LibNames;
import com.invadermonky.survivaltools.util.libs.ModIds;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.List;

public class ItemSigilTemperature extends ItemSigilToggleableBase implements IAddition, IProxy {
    public static final ItemSigilTemperature TEMPERATURE_SIGIL = new ItemSigilTemperature();
    private static final String NAME = "sigil_temperature";

    public ItemSigilTemperature() {
        super(LibNames.SIGIL_TEMPERATURE, ConfigHandlerST.integrations.blood_magic.sigil_of_temperate_lands.cost);
        this.addPropertyOverride(new ResourceLocation(SurvivalTools.MOD_ID, "enabled"), ((stack, world, entity) -> this.getActivated(stack) ? 1 : 0));
    }

    @Override
    public void onSigilUpdate(ItemStack stack, World world, EntityPlayer player, int itemSlot, boolean isSelected) {
        if (!PlayerHelper.isFakePlayer(player)) {
            if (player.ticksExisted % ConfigHandlerST.integrations.blood_magic.sigil_of_temperate_lands.delay == 0) {
                SurvivalToolsAPI.stabilizePlayerTemperature(player, ConfigHandlerST.integrations.blood_magic.sigil_of_temperate_lands.maxCooling, ConfigHandlerST.integrations.blood_magic.sigil_of_temperate_lands.maxHeating);
            }
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if (GuiScreen.isShiftKeyDown()) {
            int cooling = ConfigHandlerST.integrations.blood_magic.sigil_of_temperate_lands.maxCooling;
            int heating = ConfigHandlerST.integrations.blood_magic.sigil_of_temperate_lands.maxHeating;
            if (cooling > -1) {
                tooltip.add(I18n.format(StringHelper.getTranslationKey("max_cooling", "tooltip", "desc"), cooling));
            }
            if (heating > -1) {
                tooltip.add(I18n.format(StringHelper.getTranslationKey("max_heating", "tooltip", "desc"), heating));
            }
        }
    }

    @Override
    public void postInit() {
        BloodMagicUtils.addGuideEntry("architect", LibNames.SIGIL_TEMPERATURE);
    }

    @Override
    public void registerRecipe(IForgeRegistry<IRecipe> registry) {
        BloodMagicAPI.INSTANCE.getRecipeRegistrar().addTartaricForge(
                new ItemStack(BloodMagicST.reagent_temperature),
                300,
                30,
                new ItemStack(RegistrarBloodMagicItems.SIGIL_WATER),
                new ItemStack(RegistrarBloodMagicItems.SIGIL_AIR),
                new ItemStack(RegistrarBloodMagicItems.SIGIL_LAVA),
                "ingotGold"
        );
        BloodMagicAPI.INSTANCE.getRecipeRegistrar().addAlchemyArray(
                new ItemStack(BloodMagicST.reagent_temperature),
                new ItemStack(RegistrarBloodMagicItems.SLATE, 1, 2),
                new ItemStack(BloodMagicST.sigil_temperature),
                new ResourceLocation(ModIds.bloodmagic.modId, "textures/models/AlchemyArrays/ElementalAffinitySigil.png")
        );
    }

    @Override
    public void registerModel(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
    }

    @Override
    public boolean isEnabled() {
        return ConfigHandlerST.integrations.blood_magic.sigil_of_temperate_lands.enable;
    }
}
