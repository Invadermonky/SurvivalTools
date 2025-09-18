package com.invadermonky.survivaltools.compat.embers;

import com.invadermonky.survivaltools.api.IModModule;
import com.invadermonky.survivaltools.compat.embers.items.ItemMantleCloak;
import com.invadermonky.survivaltools.registry.RegistrarST;

public class EmbersST implements IModModule {
    @Override
    public void preInit() {
        RegistrarST.addAdditionToRegister(new ItemMantleCloak());
    }
}
