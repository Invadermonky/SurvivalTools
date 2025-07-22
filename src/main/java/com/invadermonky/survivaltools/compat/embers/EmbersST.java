package com.invadermonky.survivaltools.compat.embers;

import com.invadermonky.survivaltools.api.IProxy;
import com.invadermonky.survivaltools.items.ItemMantleCloak;
import com.invadermonky.survivaltools.registry.ModItemsST;
import com.invadermonky.survivaltools.util.libs.LibNames;

public class EmbersST implements IProxy {
    public static ItemMantleCloak mantle_cloak;

    static {
        ModItemsST.addItemToRegister(mantle_cloak = new ItemMantleCloak(), LibNames.MANTLE_CLOAK);
    }
}
