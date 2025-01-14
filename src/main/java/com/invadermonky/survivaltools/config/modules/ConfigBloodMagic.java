package com.invadermonky.survivaltools.config.modules;

import com.invadermonky.survivaltools.config.generics.GenericCostConfig;
import net.minecraftforge.common.config.Config;

public class ConfigBloodMagic {
    @Config.RequiresMcRestart
    @Config.Comment("Blood Magic temperature control ritual.settings.")
    public ConfigRitualSoothingHearth soothing_hearth = new ConfigRitualSoothingHearth();

    @Config.RequiresMcRestart
    @Config.Comment("Blood Magic temperature regulation sigil settings.")
    public GenericCostConfig sigil_of_temperate_lands = new GenericCostConfig(150);

    @Config.RequiresMcRestart
    @Config.Comment("Blood Magic hyration sigil settings.")
    public GenericCostConfig sigil_of_hydration = new GenericCostConfig(150);

    public static class ConfigRitualSoothingHearth {
        @Config.Comment("The LP activation cost for the Ritual of the Soothing Hearth.")
        public int activationCost = 10000;
        @Config.Comment("The LP cost for each application of the temperature control effect of the Ritual of the Soothing Hearth.")
        public int refreshCost = 10;
        @Config.Comment("The time, in ticks, between each application of the ritual effect.")
        public int refreshInterval = 100;
        @Config.Comment("The maximum cooling limit for this ritual. If the environmental temperature is 30, the target temperature will be (30 - maxCooling). Setting this value to -1 will remove the limit.")
        public int maxCooling = -1;
        @Config.Comment("The maximum heating limit for this ritual. If the environmental temperature is 0, the target temperature will be (0 + maxHeating). Setting this value to -1 will remove the limit.")
        public int maxHeating = -1;
    }
}
