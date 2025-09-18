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
import com.invadermonky.survivaltools.util.libs.CreativeTabST;
import com.invadermonky.survivaltools.util.libs.LibNames;
import com.invadermonky.survivaltools.util.libs.ModIds;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistry;

public class ItemSigilHydration extends ItemSigilToggleableBase implements IItemAddition, IProxy {
    public ItemSigilHydration() {
        super(LibNames.SIGIL_HYDRATION, ConfigHandlerST.integrations.blood_magic.sigil_of_hydration.cost);
        this.setRegistryName(SurvivalTools.MOD_ID, LibNames.SIGIL_HYDRATION);
        this.setTranslationKey(this.getRegistryName().toString());
        this.setCreativeTab(CreativeTabST.TAB_ST);
        this.addPropertyOverride(new ResourceLocation(SurvivalTools.MOD_ID, "enabled"), (stack, world, entity) -> this.getActivated(stack) ? 1 : 0);
    }

    @Override
    public void onSigilUpdate(ItemStack stack, World world, EntityPlayer player, int itemSlot, boolean isSelected) {
        if (!PlayerHelper.isFakePlayer(player)) {
            if (player.ticksExisted % ConfigHandlerST.integrations.blood_magic.sigil_of_hydration.delay == 0)
                SurvivalToolsAPI.hydratePlayer(player, 1, 0.6F);
        }
    }


    @Override
    public void init() {
        BloodMagicUtils.addGuideEntry("architect", LibNames.SIGIL_HYDRATION);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void registerRecipe(IForgeRegistry<IRecipe> registry) {
        ItemStack filterStack = SurvivalToolsAPI.getWaterFilterStack();
        BloodMagicAPI.INSTANCE.getRecipeRegistrar().addTartaricForge(
                new ItemStack(ModItemsST.REAGENT_HYDRATION),
                600,
                50,
                new ItemStack(RegistrarBloodMagicItems.SIGIL_WATER),
                filterStack,
                filterStack,
                ModItemsST.HYDRATION_PACK_REINFORCED
        );

        BloodMagicAPI.INSTANCE.getRecipeRegistrar().addAlchemyArray(
                new ItemStack(ModItemsST.REAGENT_HYDRATION),
                new ItemStack(RegistrarBloodMagicItems.SLATE, 1, 2),
                new ItemStack(ModItemsST.SIGIL_HYDRATION),
                new ResourceLocation(ModIds.bloodmagic.modId, "textures/models/AlchemyArrays/WaterSigil.png")
        );
    }

    @Override
    public boolean isEnabled() {
        return ConfigHandlerST.integrations.blood_magic.sigil_of_hydration.enable && SurvivalToolsAPI.isThirstFeatureEnabled();
    }
}
