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
import com.invadermonky.survivaltools.util.helpers.SurvivalHelper;
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

public class ItemSigilTemperature extends ItemSigilToggleableBase implements IAddition {
    public static final ItemSigilTemperature TEMPERATURE_SIGIL = new ItemSigilTemperature();
    private static final String NAME = "sigil_temperature";

    public ItemSigilTemperature() {
        super(LibNames.SIGIL_TEMPERATURE, ConfigHandlerST.blood_magic.sigil_of_temperate_lands.cost);
        this.addPropertyOverride(new ResourceLocation(SurvivalTools.MOD_ID, "enabled"), ((stack, world, entity) -> this.getActivated(stack) ? 1 : 0));
    }

    @Override
    public void onSigilUpdate(ItemStack stack, World world, EntityPlayer player, int itemSlot, boolean isSelected) {
        if(!PlayerHelper.isFakePlayer(player)) {
            if(player.ticksExisted % ConfigHandlerST.blood_magic.sigil_of_temperate_lands.delay == 0) {
                SurvivalHelper.stabilizePlayerTemperature(player);
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
        return ConfigHandlerST.blood_magic.sigil_of_temperate_lands.enable;
    }
}
