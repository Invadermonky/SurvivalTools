package com.invadermonky.survivaltools.config.modules;

import net.minecraftforge.common.config.Config;

public class ConfigSimpleTools {
    public CanteenConfig canteen = new CanteenConfig();
    public HydrationPackConfig hydration_pack = new HydrationPackConfig();

    public static class CanteenConfig {
        @Config.RangeInt(min = 1000, max = 32000)
        @Config.Comment("The internal liquid capacity of the Canteen.")
        public int fluidCapacityBasic = 1000;
        @Config.RangeInt(min = 1000, max = 32000)
        @Config.Comment("The internal liquid capacity of the Canteen.")
        public int fluidCapacityReinforced = 2000;
        @Config.RangeInt(min = 1, max = 32000)
        @Config.Comment("The amount of liquid that is drained from the Canteen each time it is used.")
        public int fluidCost = 250;
        @Config.RangeInt(min = 1, max = 20)
        @Config.Comment("The amount of thirst restored each time the Canteen is used.")
        public int restoredThirst = 6;
        @Config.RangeDouble(min = 0, max = 10.0)
        @Config.Comment("The amount of hydration restored each time the Canteen is used.")
        public double restoredHydration = 0.7f;
    }

    public static class HydrationPackConfig {
        @Config.Comment("Allows the Hydration Pack to be attached to chest armor.")
        public boolean attachmentRecipe = true;
        @Config.RangeInt(min = 1000, max = 32000)
        @Config.Comment("The internal liquid capacity of the Hydration Pack.")
        public int fluidCapacityBasic = 4000;
        @Config.RangeInt(min = 1000, max = 32000)
        @Config.Comment("The internal liquid capacity of the Hydration Pack.")
        public int fluidCapacityReinforced = 8000;
        @Config.RangeInt(min = 1, max = 32000)
        @Config.Comment("The amount of liquid that is drained from the Hydration Pack each time it is used.")
        public int fluidCost = 100;
        @Config.RangeInt(min = 1, max = 20)
        @Config.Comment("The amount of thirst restored each time the Hydration Pack is used.")
        public int restoredThirst = 4;
        @Config.RangeDouble(min = 0, max = 10.0)
        @Config.Comment("The amount of hydration restored each time the Hydration Pack is used.")
        public double restoredHydration = 0.7f;
    }
}
