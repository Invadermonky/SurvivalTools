package com.invadermonky.survivaltools.items;

import WayofTime.bloodmagic.api.impl.BloodMagicAPI;
import WayofTime.bloodmagic.core.RegistrarBloodMagicItems;
import WayofTime.bloodmagic.item.sigil.ItemSigilToggleableBase;
import WayofTime.bloodmagic.util.helper.PlayerHelper;
import com.invadermonky.survivaltools.SurvivalTools;
import com.invadermonky.survivaltools.api.IAddition;
import com.invadermonky.survivaltools.compat.bloodmagic.BloodMagicST;
import com.invadermonky.survivaltools.compat.bloodmagic.BloodMagicUtils;
import com.invadermonky.survivaltools.config.ConfigHandlerST;
import com.invadermonky.survivaltools.registry.ModItemsST;
import com.invadermonky.survivaltools.util.helpers.SurvivalHelper;
import com.invadermonky.survivaltools.util.helpers.SurvivalItemHelper;
import com.invadermonky.survivaltools.util.libs.LibNames;
import com.invadermonky.survivaltools.util.libs.ModIds;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.registries.IForgeRegistry;

public class ItemSigilHydration extends ItemSigilToggleableBase implements IAddition {
    public ItemSigilHydration() {
        super(LibNames.SIGIL_HYDRATION, ConfigHandlerST.blood_magic.sigil_of_hydration.cost);
        this.addPropertyOverride(new ResourceLocation(SurvivalTools.MOD_ID, "enabled"), (stack, world, entity) -> this.getActivated(stack) ? 1 : 0);
    }

    @Override
    public void onSigilUpdate(ItemStack stack, World world, EntityPlayer player, int itemSlot, boolean isSelected) {
        if(!PlayerHelper.isFakePlayer(player)) {
            if(player.ticksExisted % ConfigHandlerST.blood_magic.sigil_of_hydration.delay == 0)
                SurvivalHelper.hydratePlayer(player, 1, 0.6F);
        }
    }

    @Override
    public void postInit() {
        BloodMagicUtils.addGuideEntry("architect", LibNames.SIGIL_HYDRATION);
    }

    @Override
    public void registerRecipe(IForgeRegistry<IRecipe> registry) {
        ItemStack filterStack = SurvivalItemHelper.getCharcoalFilterStack();
        BloodMagicAPI.INSTANCE.getRecipeRegistrar().addTartaricForge(
                new ItemStack(BloodMagicST.reagent_hydration),
                600,
                50,
                new ItemStack(RegistrarBloodMagicItems.SIGIL_WATER),
                filterStack,
                filterStack,
                ModItemsST.hydration_pack_reinforced
        );

        BloodMagicAPI.INSTANCE.getRecipeRegistrar().addAlchemyArray(
                new ItemStack(BloodMagicST.reagent_hydration),
                new ItemStack(RegistrarBloodMagicItems.SLATE, 1, 2),
                new ItemStack(BloodMagicST.sigil_hydration),
                new ResourceLocation(ModIds.bloodmagic.modId, "textures/models/AlchemyArrays/WaterSigil.png")
        );
    }

    @Override
    public void registerModel(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
    }

    @Override
    public boolean isEnabled() {
        return ConfigHandlerST.blood_magic.sigil_of_hydration.enable && SurvivalHelper.isThirstFeatureEnabled();
    }
}
