package com.invadermonky.survivaltools.config.modules;

import net.minecraftforge.common.config.Config;

public class ConfigThaumcraft {
    @Config.Comment("Thaumcraft temperature regulation bauble settings.")
    public SurvivalItemTC thaumic_regulator = new SurvivalItemTC();

    public static class SurvivalItemTC {
        @Config.RequiresMcRestart
        @Config.Comment("Enables the Thaumic Regulator temperature control bauble.")
        public boolean enable = true;
        @Config.Comment("The delay between activations of the Thaumic Regulator.")
        public int delay = 100;
        @Config.RangeInt(min = 1, max = 60)
        @Config.Comment("How much energy consumed per operation. A value of 10 with a delay of 100 will consume 1 Vis every 30 seconds.")
        public int cost = 10;
    }
}
