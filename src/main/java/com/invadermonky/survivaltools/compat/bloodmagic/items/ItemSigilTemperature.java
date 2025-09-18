package com.invadermonky.survivaltools.compat.bloodmagic.items;

import WayofTime.bloodmagic.api.impl.BloodMagicAPI;
import WayofTime.bloodmagic.core.RegistrarBloodMagicItems;
import WayofTime.bloodmagic.item.sigil.ItemSigilToggleableBase;
import WayofTime.bloodmagic.util.helper.PlayerHelper;
import com.invadermonky.survivaltools.SurvivalTools;
import com.invadermonky.survivaltools.api.IProxy;
import com.invadermonky.survivaltools.api.SurvivalToolsAPI;
import com.invadermonky.survivaltools.api.items.IItemAddition;
import com.invadermonky.survivaltools.compat.bloodmagic.utils.BloodMagicUtils;
import com.invadermonky.survivaltools.config.ConfigHandlerST;
import com.invadermonky.survivaltools.registry.ModItemsST;
import com.invadermonky.survivaltools.util.helpers.StringHelper;
import com.invadermonky.survivaltools.util.libs.CreativeTabST;
import com.invadermonky.survivaltools.util.libs.LibNames;
import com.invadermonky.survivaltools.util.libs.ModIds;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.List;

public class ItemSigilTemperature extends ItemSigilToggleableBase implements IItemAddition, IProxy {
    public ItemSigilTemperature() {
        super(LibNames.SIGIL_TEMPERATURE, ConfigHandlerST.integrations.blood_magic.sigil_of_temperate_lands.cost);
        this.setRegistryName(SurvivalTools.MOD_ID, LibNames.SIGIL_TEMPERATURE);
        this.setTranslationKey(this.getRegistryName().toString());
        this.setCreativeTab(CreativeTabST.TAB_ST);
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
    public void init() {
        BloodMagicUtils.addGuideEntry("architect", LibNames.SIGIL_TEMPERATURE);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void registerRecipe(IForgeRegistry<IRecipe> registry) {
        BloodMagicAPI.INSTANCE.getRecipeRegistrar().addTartaricForge(
                new ItemStack(ModItemsST.REAGENT_TEMPERATURE),
                300,
                30,
                new ItemStack(RegistrarBloodMagicItems.SIGIL_WATER),
                new ItemStack(RegistrarBloodMagicItems.SIGIL_AIR),
                new ItemStack(RegistrarBloodMagicItems.SIGIL_LAVA),
                "ingotGold"
        );
        BloodMagicAPI.INSTANCE.getRecipeRegistrar().addAlchemyArray(
                new ItemStack(ModItemsST.REAGENT_TEMPERATURE),
                new ItemStack(RegistrarBloodMagicItems.SLATE, 1, 2),
                new ItemStack(ModItemsST.SIGIL_TEMPERATURE),
                new ResourceLocation(ModIds.bloodmagic.modId, "textures/models/AlchemyArrays/ElementalAffinitySigil.png")
        );
    }

    @Override
    public boolean isEnabled() {
        return ConfigHandlerST.integrations.blood_magic.sigil_of_temperate_lands.enable;
    }
}
