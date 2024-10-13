package com.invadermonky.survivaltools.compat.naturesaura;

import com.invadermonky.survivaltools.api.IProxy;
import com.invadermonky.survivaltools.items.ItemEnvironmentalAmulet;
import com.invadermonky.survivaltools.registry.ModItemsST;
import com.invadermonky.survivaltools.util.libs.LibNames;

public class NaturesAuraST implements IProxy {
    public static ItemEnvironmentalAmulet environmental_amulet;

    @Override
    public void preInit() {

    }

    @Override
    public void init() {

    }

    @Override
    public void postInit() {

    }

    static {
        ModItemsST.addItemToRegister(environmental_amulet = new ItemEnvironmentalAmulet(), LibNames.ENVIRONMENTAL_AMULET);
    }
}
