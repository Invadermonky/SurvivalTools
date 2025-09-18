package com.invadermonky.survivaltools.compat.naturesaura;

import com.invadermonky.survivaltools.api.IModModule;
import com.invadermonky.survivaltools.compat.naturesaura.items.ItemEnvironmentalAmulet;
import com.invadermonky.survivaltools.registry.RegistrarST;

public class NaturesAuraST implements IModModule {

    @Override
    public void preInit() {
        RegistrarST.addAdditionToRegister(new ItemEnvironmentalAmulet());
    }
}
