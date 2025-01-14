package com.invadermonky.survivaltools.config.modules;

import com.invadermonky.survivaltools.config.generics.GenericCostConfig;
import net.minecraftforge.common.config.Config;

public class ConfigBotania {
    @Config.Comment("Botania mana-powered temperature control ring settings.")
    public GenericCostConfig ring_of_seasons = new GenericCostConfig(150);

    @Config.Comment("Botania functional flower. Uses mana to regulate player temperature.")
    public GenericCostConfig gryllzalia = new GenericCostConfig(20);

    public PurePitcherConfig pure_pitcher = new PurePitcherConfig();

    public static class PurePitcherConfig {
        @Config.RequiresMcRestart
        @Config.Comment("Enables Botania mana-powered water purification flower.")
        public boolean enable = true;

        @Config.Comment("The amount of purified water, in mB, generated per second while the flower is active.")
        public int fluidGeneration = 5;
    }
}
