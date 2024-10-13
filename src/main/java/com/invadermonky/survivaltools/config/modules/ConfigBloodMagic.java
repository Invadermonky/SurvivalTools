package com.invadermonky.survivaltools.config.modules;

import com.invadermonky.survivaltools.config.generics.GenericCostConfig;
import net.minecraftforge.common.config.Config;

public class ConfigBloodMagic {
    @Config.RequiresMcRestart
    @Config.Comment("Blood Magic temperature control ritual.settings.")
    public ConfigRitualSoothingHearth soothing_hearth = new ConfigRitualSoothingHearth();
    @Config.Comment("Blood Magic temperature regulation sigil settings.")
    public GenericCostConfig sigil_of_temperate_lands = new GenericCostConfig(150);
    @Config.Comment("Blood Magic hyration sigil settings.")
    public GenericCostConfig sigil_of_hydration = new GenericCostConfig(150);

    public static class ConfigRitualSoothingHearth {
        public int activationCost = 10000;
        public int refreshCost = 10;
        public int refreshInterval = 100;
    }
}
