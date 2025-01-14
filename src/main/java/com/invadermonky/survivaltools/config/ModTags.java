package com.invadermonky.survivaltools.config;

import com.invadermonky.survivaltools.blocks.tile.TileOpenBarrel;
import gnu.trove.set.hash.THashSet;
import net.minecraft.block.state.IBlockState;

import java.util.Arrays;

public class ModTags {
    public static final THashSet<String> BOILER_HEATERS = new THashSet<>();

    public static boolean contains(THashSet<String> set, IBlockState state) {
        String blockName = state.getBlock().getRegistryName().toString();
        return set.contains(blockName) || set.contains(blockName + ":" + state.getBlock().getMetaFromState(state));
    }

    public static void syncConfig() {
        TileOpenBarrel.fluidMaxCapacity = ConfigHandlerST.open_barrel.openBarrelCapacity;
        TileOpenBarrel.rainCollectionAmount = ConfigHandlerST.open_barrel.rain_collector.purifiedWaterCollected;
        TileOpenBarrel.rainCollectionInterval = ConfigHandlerST.open_barrel.rain_collector.updateInterval;
        TileOpenBarrel.solarPurifierAmount = ConfigHandlerST.open_barrel.solar_purifier.purifiedWaterGenerated;
        TileOpenBarrel.solarPurifierInterval = ConfigHandlerST.open_barrel.solar_purifier.updateInterval;
        TileOpenBarrel.waterBoilerAmount = ConfigHandlerST.open_barrel.water_boiler.purifiedWaterGenerated;
        TileOpenBarrel.waterBoilerInterval = ConfigHandlerST.open_barrel.water_boiler.updateInterval;

        BOILER_HEATERS.addAll(Arrays.asList(ConfigHandlerST.open_barrel.water_boiler.heaterBlocks));
    }

    static {
        syncConfig();
    }
}
