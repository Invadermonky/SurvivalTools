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
    public void preInit() {}

    @Override
    public void init() {}

    @Override
    public void postInit() {
        //Open Barrel
        addIAdditionConfigFlag(ModBlocksST.open_barrel);
        addModConfigFlag("rain_collector", ModBlocksST.open_barrel.isEnabled() && ConfigHandlerST.open_barrel.rain_collector._enable);
        addIAdditionConfigFlag(ModBlocksST.open_barrel_heater);
        addIAdditionConfigFlag(ModBlocksST.open_barrel_lid_solar);

        //Flux Tools
        addIAdditionConfigFlag(ModItemsST.portable_purifier);
        addIAdditionConfigFlag(ModItemsST.portable_regulator);
        addIAdditionConfigFlag(ModBlocksST.central_air_unit);

        //Mod Integrations
        if(ModIds.bloodmagic.isLoaded) {
            addModConfigFlag(LibNames.SIGIL_HYDRATION, ConfigHandlerST.blood_magic.sigil_of_hydration.enable && SurvivalToolsAPI.isThirstFeatureEnabled());
            addModConfigFlag(LibNames.SIGIL_TEMPERATURE, ConfigHandlerST.blood_magic.sigil_of_temperate_lands.enable && SurvivalToolsAPI.isTemperatureFeatureEnabled());
            addModConfigFlag(LibNames.RITUAL_SOOTHING_HEARTH, RitualSoothingHearth.isEnabled());
        }
        if(ModIds.botania.isLoaded) {
            addModConfigFlag(LibNames.GRYLLZALIA, ConfigHandlerST.botania.gryllzalia.enable && SurvivalToolsAPI.isTemperatureFeatureEnabled());
            addModConfigFlag(LibNames.PURE_PITCHER, ConfigHandlerST.botania.pure_pitcher.enable && SurvivalToolsAPI.isThirstFeatureEnabled());
            addModConfigFlag(LibNames.SEASONS_RING, ConfigHandlerST.botania.ring_of_seasons.enable && SurvivalToolsAPI.isTemperatureFeatureEnabled());
        }
        if(ModIds.embers.isLoaded) {
            addModConfigFlag(LibNames.MANTLE_CLOAK, ConfigHandlerST.embers.mantle_cloak.enable && SurvivalToolsAPI.isTemperatureFeatureEnabled());
        }
        if(ModIds.natures_aura.isLoaded) {
            addModConfigFlag(LibNames.ENVIRONMENTAL_AMULET, ConfigHandlerST.natures_aura.environmental_amulet.enable && SurvivalToolsAPI.isTemperatureFeatureEnabled());
        }
        if(ModIds.thaumcraft.isLoaded) {
            addModConfigFlag(LibNames.THAUMIC_REGULATOR, ConfigHandlerST.thaumcraft.thaumic_regulator.enable && SurvivalToolsAPI.isTemperatureFeatureEnabled());
        }

    }

    private <T extends Item & IAddition> void addIAdditionConfigFlag(T item) {
        PatchouliAPI.instance.setConfigFlag(item.getRegistryName().toString(), item.isEnabled());
    }

    private <T extends Block & IAddition> void addIAdditionConfigFlag(T block) {
        PatchouliAPI.instance.setConfigFlag(block.getRegistryName().toString(), block.isEnabled());
    }

    private void addModConfigFlag(String name, boolean flag) {
        PatchouliAPI.instance.setConfigFlag(SurvivalTools.MOD_ID + ":" + name, flag);
    }
}
