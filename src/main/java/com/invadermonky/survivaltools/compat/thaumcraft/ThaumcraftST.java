package com.invadermonky.survivaltools.compat.thaumcraft;

import com.invadermonky.survivaltools.api.IProxy;
import com.invadermonky.survivaltools.items.ItemThaumicRegulator;
import com.invadermonky.survivaltools.registry.ModItemsST;
import com.invadermonky.survivaltools.util.libs.LibNames;

public class ThaumcraftST implements IProxy {
    public static ItemThaumicRegulator thaumic_regulator;

    static {
        ModItemsST.addItemToRegister(thaumic_regulator = new ItemThaumicRegulator(), LibNames.THAUMIC_REGULATOR);
    }
}
