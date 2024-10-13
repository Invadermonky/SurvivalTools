package com.invadermonky.survivaltools.compat.bloodmagic;

import com.invadermonky.survivaltools.api.IProxy;
import com.invadermonky.survivaltools.config.ConfigHandlerST;
import com.invadermonky.survivaltools.items.ItemAddition;
import com.invadermonky.survivaltools.items.ItemSigilHydration;
import com.invadermonky.survivaltools.items.ItemSigilTemperature;
import com.invadermonky.survivaltools.registry.ModItemsST;
import com.invadermonky.survivaltools.util.helpers.SurvivalHelper;
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
        ModItemsST.addItemToRegister(reagent_hydration = new ItemAddition(ConfigHandlerST.blood_magic.sigil_of_hydration.enable && SurvivalHelper.isThirstFeatureEnabled()), LibNames.REAGENT_HYDRATION);
        ModItemsST.addItemToRegister(reagent_temperature = new ItemAddition(ConfigHandlerST.blood_magic.sigil_of_temperate_lands.enable && SurvivalHelper.isTemperatureFeatureEnabled()), LibNames.REAGENT_TEMPERATURE);
        ModItemsST.addItemToRegister(sigil_hydration = new ItemSigilHydration(), LibNames.SIGIL_HYDRATION);
        ModItemsST.addItemToRegister(sigil_temperature = new ItemSigilTemperature(), LibNames.SIGIL_TEMPERATURE);
    }
}
