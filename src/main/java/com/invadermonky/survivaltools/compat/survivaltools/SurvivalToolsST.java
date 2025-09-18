package com.invadermonky.survivaltools.compat.survivaltools;

import com.invadermonky.survivaltools.api.IModModule;
import com.invadermonky.survivaltools.compat.survivaltools.blocks.BlockCentralAirUnit;
import com.invadermonky.survivaltools.compat.survivaltools.blocks.BlockOpenBarrel;
import com.invadermonky.survivaltools.compat.survivaltools.blocks.BlockPoweredPurifier;
import com.invadermonky.survivaltools.compat.survivaltools.blocks.BlockSolidFuelPurifier;
import com.invadermonky.survivaltools.compat.survivaltools.items.*;
import com.invadermonky.survivaltools.config.ConfigHandlerST;
import com.invadermonky.survivaltools.registry.RegistrarST;
import com.invadermonky.survivaltools.util.libs.LibNames;

public class SurvivalToolsST implements IModModule {
    @Override
    public void preInit() {
        //Blocks
        RegistrarST.addAdditionToRegister(new BlockCentralAirUnit());
        RegistrarST.addAdditionToRegister(new BlockOpenBarrel());
        RegistrarST.addAdditionToRegister(new BlockPoweredPurifier());
        RegistrarST.addAdditionToRegister(new BlockSolidFuelPurifier());
        //Items
        RegistrarST.addAdditionToRegister(new ItemCanteen(LibNames.CANTEEN, ConfigHandlerST.tools.canteens.fluidCapacityBasic));
        RegistrarST.addAdditionToRegister(new ItemCanteen(LibNames.CANTEEN_REINFORCED, ConfigHandlerST.tools.canteens.fluidCapacityReinforced));
        RegistrarST.addAdditionToRegister(new ItemHydrationPack(LibNames.HYDRATION_PACK, ConfigHandlerST.tools.hydration_packs.fluidCapacityBasic));
        RegistrarST.addAdditionToRegister(new ItemHydrationPack(LibNames.HYDRATION_PACK_REINFORCED, ConfigHandlerST.tools.hydration_packs.fluidCapacityReinforced));
        RegistrarST.addAdditionToRegister(new ItemHandheldPurifier());
        RegistrarST.addAdditionToRegister(new ItemPortablePurifier());
        RegistrarST.addAdditionToRegister(new ItemPortableRegulator());
    }
}
