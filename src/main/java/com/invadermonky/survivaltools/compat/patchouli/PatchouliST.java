package com.invadermonky.survivaltools.compat.patchouli;

import com.invadermonky.survivaltools.SurvivalTools;
import com.invadermonky.survivaltools.api.IAddition;
import com.invadermonky.survivaltools.api.IProxy;
import com.invadermonky.survivaltools.compat.bloodmagic.BloodMagicST;
import com.invadermonky.survivaltools.compat.bloodmagic.RitualSoothingHearth;
import com.invadermonky.survivaltools.compat.botania.BotaniaST;
import com.invadermonky.survivaltools.compat.embers.EmbersST;
import com.invadermonky.survivaltools.compat.naturesaura.NaturesAuraST;
import com.invadermonky.survivaltools.compat.thaumcraft.ThaumcraftST;
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
            addIAdditionConfigFlag(BloodMagicST.sigil_hydration);
            addIAdditionConfigFlag(BloodMagicST.sigil_temperature);
            addModConfigFlag(LibNames.RITUAL_SOOTHING_HEARTH, RitualSoothingHearth.isEnabled());
        }
        if(ModIds.botania.isLoaded) {
            addModConfigFlag(LibNames.GRYLLZALIA, BotaniaST.gryllzalia.isEnabled());
            addModConfigFlag(LibNames.PURE_PITCHER, BotaniaST.pure_pitcher.isEnabled());
            addIAdditionConfigFlag(BotaniaST.seasons_ring);
        }
        if(ModIds.embers.isLoaded) {
            addIAdditionConfigFlag(EmbersST.mantle_cloak);
        }
        if(ModIds.natures_aura.isLoaded) {
            addIAdditionConfigFlag(NaturesAuraST.environmental_amulet);
        }
        if(ModIds.thaumcraft.isLoaded) {
            addIAdditionConfigFlag(ThaumcraftST.thaumic_regulator);
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
