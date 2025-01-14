package com.invadermonky.survivaltools.config.modules;

import net.minecraftforge.common.config.Config;

public class ConfigOpenBarrel {
    @Config.RequiresMcRestart
    @Config.Comment("Enables the Open Barrel fluid container block. Disabling this will also disable all associated features.")
    public boolean _enable = true;

    @Config.RequiresMcRestart
    @Config.Comment("The capacity, in mB, of the open barrel.")
    public int openBarrelCapacity = 4000;

    public RainCollectorConfig rain_collector = new RainCollectorConfig();
    public SolarPurifierConfig solar_purifier = new SolarPurifierConfig();
    public WaterBoilerConfig water_boiler = new WaterBoilerConfig();

    public static class RainCollectorConfig {
        @Config.Comment("Enables the Open Barrel to collect Purified Water when exposed to the sky while it is raining.")
        public boolean _enable = true;

        @Config.RangeInt(min = 1, max = 24000)
        @Config.Comment("The delay, in ticks, between each fluid collection operation performed by the Open Barrel.")
        public int updateInterval = 20;

        @Config.RangeInt(min = 1, max = 1000)
        @Config.Comment("The amount of purified water, in mB, collected per update interval while it is raining.")
        public int purifiedWaterCollected = 15;
    }

    public static class SolarPurifierConfig {
        @Config.Comment("Enables the solar purifier Open Barrel multiblock.")
        public boolean _enable = true;

        @Config.RequiresWorldRestart
        @Config.Comment("Allows the Solar Purifier to work faster in hot biomes and slower in cold biomes.")
        public boolean enableBiomeBonuses = true;

        @Config.RangeInt(min = 1, max = 24000)
        @Config.Comment("The delay, in ticks, between each fluid collection operation performed by the Open Barrel.")
        public int updateInterval = 100;

        @Config.RangeInt(min = 1, max = 1000)
        @Config.Comment("The amount of purified water, in mB, collected per update interval while multiblock conditions are met.")
        public int purifiedWaterGenerated = 5;
    }

    public static class WaterBoilerConfig {
        @Config.Comment("Enables the water boiler Open Barrel multiblock.")
        public boolean _enable = true;

        @Config.RangeInt(min = 1, max = 24000)
        @Config.Comment("The delay, in ticks, between each fluid collection operation performed by the Open Barrel.")
        public int updateInterval = 20;

        @Config.RangeInt(min = 1, max = 1000)
        @Config.Comment("The amount of purified water, in mB, collected per update interval while multiblock conditions are met.")
        public int purifiedWaterGenerated = 20;

        @Config.Comment("A list of blocks that are considered heaters for the Water Boiler multiblock. Format: minecraft:fire or minecraft:fire:0.")
        public String[] heaterBlocks = new String[] {};

        @Config.Comment("Configuration settings for the burnable fuel powered Barrel Heater.")
        public FurnaceHeaterConfig barrel_heater = new FurnaceHeaterConfig();

        @Config.Comment("Configuration settings for the RF powered Barrel Heater.")
        public PoweredHeaterConfig powered_heater = new PoweredHeaterConfig();

        public static class FurnaceHeaterConfig {
            @Config.RangeDouble(min = 0.1, max = 10.0)
            @Config.Comment("Fuel efficiency multiplier for the Furnace Barrel Heater.")
            public double fuelEfficiency = 1.5;
        }

        public static class PoweredHeaterConfig {
            @Config.RequiresMcRestart
            @Config.Comment("Enables the RF powered barrel heater used in the water boiler multiblock. Disabling the Water Boiler will also disable this block.")
            public boolean _enable = true;
            @Config.Comment("The maximum energy capacity of the powered tank heater.")
            public int energyCapacity = 200000;
            @Config.Comment("The amount of RF/t the powered tank heater consumes while active.")
            public int energyCost = 100;
        }

    }
}
