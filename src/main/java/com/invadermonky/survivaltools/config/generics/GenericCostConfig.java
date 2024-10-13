package com.invadermonky.survivaltools.config.generics;

import net.minecraftforge.common.config.Config;

public class GenericCostConfig {
    @Config.RequiresMcRestart
    @Config.Comment("Enables this tool.")
    public boolean enable = true;
    @Config.Comment("The delay, in ticks, between each operation.")
    public int delay;
    @Config.Comment("The cost of each operation. This cost will be applied for as long as the tool is active or equipped.")
    public int cost;

    /**
     * Creates a Survival Item config with specified cooldown and cost.
     */
    public GenericCostConfig(int delay, int cost) {
        this.delay = delay;
        this.cost = cost;
    }

    /**
     * Creates a Survival Item config with a cooldown of 100 ticks.
     */
    public GenericCostConfig(int cost) {
        this(100, cost);
    }
}
