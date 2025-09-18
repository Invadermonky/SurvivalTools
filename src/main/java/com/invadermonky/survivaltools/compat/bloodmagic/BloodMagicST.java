package com.invadermonky.survivaltools.compat.bloodmagic;

import com.invadermonky.survivaltools.api.IModModule;
import com.invadermonky.survivaltools.compat.bloodmagic.items.ItemSigilHydration;
import com.invadermonky.survivaltools.compat.bloodmagic.items.ItemSigilTemperature;
import com.invadermonky.survivaltools.compat.bloodmagic.utils.BloodMagicUtils;
import com.invadermonky.survivaltools.compat.survivaltools.items.ItemAddition;
import com.invadermonky.survivaltools.registry.RegistrarST;
import com.invadermonky.survivaltools.util.libs.LibNames;

public class BloodMagicST implements IModModule {
    @Override
    public void preInit() {
        ItemSigilHydration sigil_hydration = new ItemSigilHydration();
        ItemSigilTemperature sigil_temperature = new ItemSigilTemperature();
        RegistrarST.addAdditionToRegister(new ItemAddition(LibNames.REAGENT_HYDRATION, sigil_hydration.isEnabled()));
        RegistrarST.addAdditionToRegister(new ItemAddition(LibNames.REAGENT_TEMPERATURE, sigil_temperature.isEnabled()));
        RegistrarST.addAdditionToRegister(sigil_hydration);
        RegistrarST.addAdditionToRegister(sigil_temperature);
    }

    @Override
    public void postInit() {
        BloodMagicUtils.buildGuideEntries();
    }
}
