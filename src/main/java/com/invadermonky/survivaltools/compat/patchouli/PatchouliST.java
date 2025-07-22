package com.invadermonky.survivaltools.compat.patchouli;

import com.invadermonky.survivaltools.SurvivalTools;
import com.invadermonky.survivaltools.api.IAddition;
import com.invadermonky.survivaltools.api.IProxy;
import com.invadermonky.survivaltools.api.SurvivalToolsAPI;
import com.invadermonky.survivaltools.compat.bloodmagic.RitualSoothingHearth;
import com.invadermonky.survivaltools.config.ConfigHandlerST;
import com.invadermonky.survivaltools.registry.ModBlocksST;
import com.invadermonky.survivaltools.registry.ModItemsST;
import com.invadermonky.survivaltools.util.libs.LibNames;
import com.invadermonky.survivaltools.util.libs.ModIds;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import vazkii.patchouli.api.PatchouliAPI;

public class PatchouliST implements IProxy {

    @Override
    public void postInit() {
        //Open Barrel
        addIAdditionConfigFlag(ModBlocksST.open_barrel);

        //Flux Tools
        addIAdditionConfigFlag(ModItemsST.portable_purifier);
        addIAdditionConfigFlag(ModItemsST.portable_regulator);
        addIAdditionConfigFlag(ModBlocksST.central_air_unit);
        addIAdditionConfigFlag(ModBlocksST.powered_purifier);

        //Mod Integrations
        if (ModIds.bloodmagic.isLoaded) {
            addModConfigFlag(LibNames.SIGIL_HYDRATION, ConfigHandlerST.integrations.blood_magic.sigil_of_hydration.enable && SurvivalToolsAPI.isThirstFeatureEnabled());
            addModConfigFlag(LibNames.SIGIL_TEMPERATURE, ConfigHandlerST.integrations.blood_magic.sigil_of_temperate_lands.enable && SurvivalToolsAPI.isTemperatureFeatureEnabled());
            addModConfigFlag(LibNames.RITUAL_SOOTHING_HEARTH, RitualSoothingHearth.isEnabled());
        }
        if (ModIds.botania.isLoaded) {
            addModConfigFlag(LibNames.GRYLLZALIA, ConfigHandlerST.integrations.botania.gryllzalia.enable && SurvivalToolsAPI.isTemperatureFeatureEnabled());
            addModConfigFlag(LibNames.PURE_PITCHER, ConfigHandlerST.integrations.botania.pure_pitcher.enable && SurvivalToolsAPI.isThirstFeatureEnabled());
            addModConfigFlag(LibNames.SEASONS_RING, ConfigHandlerST.integrations.botania.ring_of_seasons.enable && SurvivalToolsAPI.isTemperatureFeatureEnabled());
        }
        if (ModIds.embers.isLoaded) {
            addModConfigFlag(LibNames.MANTLE_CLOAK, ConfigHandlerST.integrations.embers.mantle_cloak.enable && SurvivalToolsAPI.isTemperatureFeatureEnabled());
        }
        if (ModIds.natures_aura.isLoaded) {
            addModConfigFlag(LibNames.ENVIRONMENTAL_AMULET, ConfigHandlerST.integrations.natures_aura.environmental_amulet.enable && SurvivalToolsAPI.isTemperatureFeatureEnabled());
        }
        if (ModIds.thaumcraft.isLoaded) {
            addModConfigFlag(LibNames.THAUMIC_REGULATOR, ConfigHandlerST.integrations.thaumcraft.thaumic_regulator.enable && SurvivalToolsAPI.isTemperatureFeatureEnabled());
        }

    }

    private <T extends Item & IAddition> void addIAdditionConfigFlag(T item) {
        if (item != null && item.isEnabled()) {
            PatchouliAPI.instance.setConfigFlag(item.getRegistryName().toString(), item.isEnabled());
        }
    }

    private <T extends Block & IAddition> void addIAdditionConfigFlag(T block) {
        if (block != null && block.isEnabled()) {
            PatchouliAPI.instance.setConfigFlag(block.getRegistryName().toString(), block.isEnabled());
        }
    }

    private void addModConfigFlag(String name, boolean flag) {
        PatchouliAPI.instance.setConfigFlag(SurvivalTools.MOD_ID + ":" + name, flag);
    }
}
