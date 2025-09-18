package com.invadermonky.survivaltools.compat.thaumcraft;

import com.invadermonky.survivaltools.api.IModModule;
import com.invadermonky.survivaltools.compat.thaumcraft.items.ItemThaumicRegulator;
import com.invadermonky.survivaltools.registry.RegistrarST;

public class ThaumcraftST implements IModModule {
    @Override
    public void preInit() {
        RegistrarST.addAdditionToRegister(new ItemThaumicRegulator());
    }
}
