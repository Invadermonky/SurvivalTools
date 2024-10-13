package com.invadermonky.survivaltools.config.modules;

import net.minecraftforge.common.config.Config;

public class ConfigFluxTools {
    @Config.RequiresMcRestart
    public CentralAirUnitConfig central_air_unit = new CentralAirUnitConfig();
    @Config.RequiresMcRestart
    public PortablePurifierConfig portable_purifier = new PortablePurifierConfig();
    @Config.RequiresMcRestart
    public PortableRegulatorConfig portable_regulator = new PortableRegulatorConfig();

    public static class CentralAirUnitConfig {
        @Config.Comment("Enables the RF powered area heating/cooling block.")
        public boolean enable = true;
        @Config.Comment("The maximum energy capacity of the central air unit.")
        public int energyCapacity = 200000;
        @Config.Comment("The amount of RF/t the central air unit consumes while active.")
        public int energyCost = 100;
        @Config.Comment("The effect radius of the central air unit.")
        public int radius = 32;
    }

    public static class PortablePurifierConfig {
        @Config.Comment("Enables the portable water purifier item.")
        public boolean enable = true;
        @Config.RangeInt(min = 1, max = 10000000)
        @Config.Comment("The total energy capacity of the Portable Purifier.")
        public int energyCapacity = 200000;
        @Config.RangeInt(min = 1, max = 10000)
        @Config.Comment("The amount of RF/t the Portable Purifier will drain while active.")
        public int energyCost = 5;
        @Config.RangeInt(min = 1000, max = 32000)
        @Config.Comment("The total fluid capacity of the Portable Purifier.")
        public int fluidCapacity = 16000;
        @Config.RangeInt(min = 1, max = 64000)
        @Config.Comment("The amount of liquid drained from the Portable Purifier each time it activates. This amount will restore 1 thirst.")
        public int fluidCost = 100;
        @Config.Comment("Should the tooltip energy values be shortened? (400,000 -> 400k)")
        public boolean shortenEnergyTooltip = true;
    }

    public static class PortableRegulatorConfig {
        @Config.Comment("Enables the portable temperature regulator item.")
        public boolean enable = true;
        @Config.RangeInt(min = 1, max = 10000000)
        @Config.Comment("The total energy capacity of the Portable Regulator.")
        public int energyCapacity = 200000;
        @Config.RangeInt(min = 1, max = 10000)
        @Config.Comment("The amount of RF/t the Portable Regulator will drain while active.")
        public int energyCost = 5;
        @Config.Comment("Should the tooltip energy values be shortened? (400,000 -> 400k)")
        public boolean shortenEnergyTooltip = true;
    }
}
