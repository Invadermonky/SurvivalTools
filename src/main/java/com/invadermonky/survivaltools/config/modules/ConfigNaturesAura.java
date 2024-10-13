package com.invadermonky.survivaltools.config.modules;

import com.invadermonky.survivaltools.config.generics.GenericCostConfig;
import net.minecraftforge.common.config.Config;

public class ConfigNaturesAura {
    @Config.Comment("Nature's Aura aura-powered temperature regulation bauble settings.")
    public GenericCostConfig environmental_amulet = new GenericCostConfig(400);
}
