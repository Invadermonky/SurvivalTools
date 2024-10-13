package com.invadermonky.survivaltools.compat.bloodmagic;

import com.invadermonky.survivaltools.api.IProxy;
import com.invadermonky.survivaltools.items.ItemAddition;
import com.invadermonky.survivaltools.items.ItemSigilHydration;
import com.invadermonky.survivaltools.items.ItemSigilTemperature;
import com.invadermonky.survivaltools.registry.ModItemsST;
import com.invadermonky.survivaltools.util.libs.LibNames;

public class BloodMagicST implements IProxy {
    public static ItemAddition reagent_hydration;
    public static ItemAddition reagent_temperature;
    public static ItemSigilHydration sigil_hydration;
    public static ItemSigilTemperature sigil_temperature;

    @Override
    public void preInit() {

    }

    @Override
    public void init() {

    }

    @Override
    public void postInit() {
        if(RitualSoothingHearth.isEnabled()) {
            BloodMagicUtils.addGuideEntry("ritual", LibNames.RITUAL_SOOTHING_HEARTH);
        }
        BloodMagicUtils.buildGuideEntries();
    }

    static {
        sigil_hydration = new ItemSigilHydration();
        sigil_temperature = new ItemSigilTemperature();
        ModItemsST.addItemToRegister(reagent_hydration = new ItemAddition(sigil_hydration.isEnabled()), LibNames.REAGENT_HYDRATION);
        ModItemsST.addItemToRegister(reagent_temperature = new ItemAddition(sigil_temperature.isEnabled()), LibNames.REAGENT_TEMPERATURE);
        ModItemsST.addItemToRegister(sigil_hydration, LibNames.SIGIL_HYDRATION);
        ModItemsST.addItemToRegister(sigil_temperature, LibNames.SIGIL_TEMPERATURE);
    }
}
