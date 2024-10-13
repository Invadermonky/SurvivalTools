package com.invadermonky.survivaltools.config.modules;

import com.invadermonky.survivaltools.config.generics.GenericCostConfig;
import net.minecraftforge.common.config.Config;

public class ConfigEmbers {
    @Config.Comment("Embers ember-powered temperature regulation bauble settings.")
    public GenericCostConfig mantle_cloak = new GenericCostConfig(60, 1);
}
