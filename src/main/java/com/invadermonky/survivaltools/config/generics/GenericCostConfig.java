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
    @Config.Comment("The maximum cooling limit for this tool. If the environmental temperature is 30, the target temperature will be (30 - maxCooling). Setting this value to -1 will remove the limit.")
    public int maxCooling;
    @Config.Comment("The maximum heating limit for this tool. If the environmental temperature is 0, the target temperature will be (0 + maxHeating). Setting this value to -1 will remove the limit.")
    public int maxHeating;

    /**
     * Creates a Survival Item config with specified cooldown and cost.
     */
    public GenericCostConfig(int delay, int cost) {
        this.delay = delay;
        this.cost = cost;
        this.maxCooling = -1;
        this.maxHeating = -1;
    }

    /**
     * Creates a Survival Item config with a cooldown of 100 ticks.
     */
    public GenericCostConfig(int cost) {
        this(100, cost);
    }
}
