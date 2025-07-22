package com.invadermonky.survivaltools.config;

import com.invadermonky.survivaltools.SurvivalTools;
import com.invadermonky.survivaltools.config.generics.GenericCostConfig;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = SurvivalTools.MOD_ID)
public class ConfigHandlerST {
    @Config.Name("General Options")
    public static GeneralCategory general = new GeneralCategory();
    @Config.Name("Mod Integrations")
    public static IntegrationsCategory integrations = new IntegrationsCategory();
    @Config.Name("Machines")
    public static MachinesCategory machines = new MachinesCategory();
    @Config.Name("Tools")
    public static ToolsCategory tools = new ToolsCategory();

    public static class GeneralCategory {
        @Config.RequiresMcRestart
        @Config.Comment("Enables creative menu filled variants of any applicable tools/times (such as the Hydration Pack).")
        public boolean enableFullVariants = true;

        @Config.RequiresMcRestart
        @Config.Comment("Force loads Survival Tools temperature items and machines even if TAN/SimpleDifficulty temperature feature is disabled.")
        public boolean forceLoadTemperatureFeatures = false;

        @Config.RequiresMcRestart
        @Config.Comment("Force loads Survival Tools thirst items and machines even if TAN/SimpleDifficulty thirst feature is disabled.")
        public boolean forceLoadThirstFeatures = false;
    }

    public static class IntegrationsCategory {
        @Config.Name("Blood Magic")
        public BloodMagicCategory blood_magic = new BloodMagicCategory();
        @Config.Name("Botania")
        public BotaniaCategory botania = new BotaniaCategory();
        @Config.Name("Embers")
        public EmbersCategory embers = new EmbersCategory();
        @Config.Name("Nature's Aura")
        public NaturesAuraCategory natures_aura = new NaturesAuraCategory();
        @Config.Name("Thaumcraft")
        public ThaumcraftCategory thaumcraft = new ThaumcraftCategory();

        public static class BloodMagicCategory {
            @Config.RequiresMcRestart
            @Config.Name("Ritual of the Soothing Hearth")
            @Config.Comment("Blood Magic temperature control ritual.settings.")
            public RitualSoothingHearthCategory soothing_hearth = new RitualSoothingHearthCategory();

            @Config.RequiresMcRestart
            @Config.Name("Sigil of Temperate Lands")
            @Config.Comment("Blood Magic temperature regulation sigil settings.")
            public GenericCostConfig sigil_of_temperate_lands = new GenericCostConfig(150);

            @Config.RequiresMcRestart
            @Config.Name("Sigil of Hydration")
            @Config.Comment("Blood Magic hyration sigil settings.")
            public GenericCostConfig sigil_of_hydration = new GenericCostConfig(150);

            public static class RitualSoothingHearthCategory {
                @Config.RequiresMcRestart
                @Config.RangeInt(min = 0, max = 10000000)
                @Config.Name("Activation Cost")
                @Config.Comment("The LP activation cost for the Ritual of the Soothing Hearth.")
                public int activationCost = 10000;

                @Config.RangeInt(min = 1, max = 10000000)
                @Config.Name("Refresh Cost")
                @Config.Comment("The LP cost for each application of the temperature control effect of the Ritual of the Soothing Hearth.")
                public int refreshCost = 10;

                @Config.RangeInt(min = 1, max = 6000)
                @Config.Name("Refresh Interval")
                @Config.Comment("The time, in ticks, between each application of the ritual effect.")
                public int refreshInterval = 100;

                @Config.RangeInt(min = -1, max = 50)
                @Config.Name("Max Heating Range")
                @Config.Comment("The maximum cooling limit for this ritual. If the environmental temperature is 30, the target temperature will be (30 - maxCooling). Setting this value to -1 will remove the limit.")
                public int maxCooling = -1;

                @Config.RangeInt(min = -1, max = 50)
                @Config.Name("Max Heating Range")
                @Config.Comment("The maximum heating limit for this ritual. If the environmental temperature is 0, the target temperature will be (0 + maxHeating). Setting this value to -1 will remove the limit.")
                public int maxHeating = -1;
            }
        }

        public static class BotaniaCategory {
            @Config.Name("Ring of Seasons")
            @Config.Comment("Botania mana-powered temperature control ring settings.")
            public GenericCostConfig ring_of_seasons = new GenericCostConfig(150);

            @Config.Name("Gryllzalia")
            @Config.Comment("Botania functional flower that uses mana to regulate player temperature.")
            public GenericCostConfig gryllzalia = new GenericCostConfig(20);

            @Config.Name("Pure Pitcher")
            @Config.Comment("Botania functional flower that uses mana and water to generate purified water.")
            public PurePitcherCategory pure_pitcher = new PurePitcherCategory();

            public static class PurePitcherCategory {
                @Config.RequiresMcRestart
                @Config.Comment("Enables Botania mana-powered water purification flower.")
                public boolean enable = true;

                @Config.Comment("The amount of purified water, in mB, generated per second while the flower is active.")
                public int fluidGeneration = 5;
            }
        }

        public static class EmbersCategory {
            @Config.Name("Mantle Cloak")
            @Config.Comment("Embers ember-powered temperature regulation bauble settings.")
            public GenericCostConfig mantle_cloak = new GenericCostConfig(60, 1);
        }

        public static class NaturesAuraCategory {
            @Config.Name("Environmental Amulet")
            @Config.Comment("Nature's Aura aura-powered temperature regulation bauble settings.")
            public GenericCostConfig environmental_amulet = new GenericCostConfig(400);
        }

        public static class ThaumcraftCategory {
            @Config.Name("Thaumic Regulator")
            @Config.Comment("Thaumcraft temperature regulation bauble settings.")
            public ThaumicRegulatorCategory thaumic_regulator = new ThaumicRegulatorCategory();

            public static class ThaumicRegulatorCategory {
                @Config.RequiresMcRestart
                @Config.Comment("Enables the Thaumic Regulator temperature control bauble.")
                public boolean enable = true;

                @Config.RangeInt(min = 1, max = 6000)
                @Config.Name("Usage Delay")
                @Config.Comment("The delay between activations of the Thaumic Regulator.")
                public int delay = 100;

                @Config.RangeInt(min = 1, max = 60)
                @Config.Name("Energy Cost")
                @Config.Comment("How much energy consumed per operation. A value of 10 with a delay of 100 will consume 1 Vis every 30 seconds.")
                public int cost = 10;

                @Config.RangeInt(min = -1, max = 50)
                @Config.Name("Max Heating Range")
                @Config.Comment("The maximum cooling limit for this tool. If the environmental temperature is 30, the target temperature will be (30 - maxCooling). Setting this value to -1 will remove the limit.")
                public int maxCooling = -1;

                @Config.RangeInt(min = -1, max = 50)
                @Config.Name("Max Heating Range")
                @Config.Comment("The maximum heating limit for this tool. If the environmental temperature is 0, the target temperature will be (0 + maxHeating). Setting this value to -1 will remove the limit.")
                public int maxHeating = -1;
            }
        }
    }

    public static class MachinesCategory {
        @Config.Name("Central Air Unit")
        public CentralAirUnitCategory central_air_unit = new CentralAirUnitCategory();
        @Config.Name("Powered Purifier")
        public PoweredPurifierCategory powered_purifier = new PoweredPurifierCategory();
        @Config.Name("Rain Collector")
        public RainCollectorCategory rain_collector = new RainCollectorCategory();
        @Config.Name("Solid Fuel Purifier")
        public SolidFuelPurifierCategory solid_fuel_purifier = new SolidFuelPurifierCategory();

        public static class CentralAirUnitCategory {
            @Config.RequiresMcRestart
            @Config.Name("Enable Central Air Unit")
            @Config.Comment("Enables the RF powered area heating/cooling block.")
            public boolean enable = true;

            @Config.RangeInt(min = 1, max = 20000)
            @Config.Name("Energy Cost")
            @Config.Comment("The amount of RF/t the central air unit consumes while active.")
            public int energyCost = 512;

            @Config.RangeInt(min = 1, max = 256)
            @Config.Name("Heating/Cooling Range")
            @Config.Comment("The effect radius of the central air unit.")
            public int radius = 32;

            @Config.RangeInt(min = -1, max = 50)
            @Config.Name("Max Cooling Range")
            @Config.Comment("The maximum cooling limit for this tool. If the environmental temperature is 30, the target temperature will be (30 - maxCooling). Setting this value to -1 will remove the limit.")
            public int maxCooling = -1;

            @Config.RangeInt(min = -1, max = 50)
            @Config.Name("Max Heating Range")
            @Config.Comment("The maximum heating limit for this tool. If the environmental temperature is 0, the target temperature will be (0 + maxHeating). Setting this value to -1 will remove the limit.")
            public int maxHeating = -1;
        }

        public static class PoweredPurifierCategory {
            @Config.RequiresMcRestart
            @Config.Name("Enable Powered Purifier")
            @Config.Comment("Enables the Powered Purifier, an RF powered device that purifies liquids.")
            public boolean enable = true;

            @Config.RangeInt(min = 0, max = 20000)
            @Config.Name("Energy Cost")
            @Config.Comment("The energy cost of the Powered Purifier in RF per tick.")
            public int energyCost = 64;
        }

        public static class RainCollectorCategory {
            @Config.RequiresMcRestart
            @Config.Name("Enable Rain Collector")
            @Config.Comment("Enables the Rain Collector fluid container block used to collect rainwater during storms. Can also be used as a fluid tank.")
            public boolean enable = true;

            @Config.RangeInt(min = 1, max = 1000)
            @Config.Name("Rainwater Collected")
            @Config.Comment("The amount of purified water, in mB, collected per second while it is raining.")
            public int waterCollected = 15;

        }

        public static class SolidFuelPurifierCategory {
            @Config.RequiresMcRestart
            @Config.Name("Enable Solid Fuel Purifier")
            @Config.Comment("Enables the Solid Fuel Purifier, an burnable fuel powered device that purifies liquids.")
            public boolean enable = true;
        }

    }

    public static class ToolsCategory {
        @Config.Name("Canteens")
        public CanteenCategory canteens = new CanteenCategory();
        @Config.Name("Hydration Packs")
        public HydrationPackCategory hydration_packs = new HydrationPackCategory();
        @Config.Name("Portable Purifier")
        public PortablePurifierCategory portable_purifier = new PortablePurifierCategory();
        @Config.Name("Portable Regulator")
        public PortableRegulatorCategory portable_regulator = new PortableRegulatorCategory();

        public static class CanteenCategory {
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

        public static class HydrationPackCategory {
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

        public static class PortablePurifierCategory {
            @Config.RequiresMcRestart
            @Config.Name("Enable Portable Purifier")
            @Config.Comment("Enables the portable water purifier item.")
            public boolean enable = true;

            @Config.RangeInt(min = 1, max = 10000000)
            @Config.Name("Energy Capacity")
            @Config.Comment("The total energy capacity of the Portable Purifier.")
            public int energyCapacity = 200000;

            @Config.RangeInt(min = 1, max = 10000)
            @Config.Name("Energy Cost")
            @Config.Comment("The amount of RF/t the Portable Purifier will drain while active.")
            public int energyCost = 5;

            @Config.RangeInt(min = 1000, max = 32000)
            @Config.Name("Fluid Capacity")
            @Config.Comment("The total fluid capacity of the Portable Purifier.")
            public int fluidCapacity = 16000;

            @Config.RangeInt(min = 1, max = 32000)
            @Config.Name("Fluid Cost")
            @Config.Comment("The amount of liquid drained from the Portable Purifier each time it activates. This amount will restore 1 thirst.")
            public int fluidCost = 100;

            @Config.Name("Shorten Tooltip")
            @Config.Comment("Should the tooltip energy values be shortened? (400,000 -> 400k)")
            public boolean shortenEnergyTooltip = true;
        }

        public static class PortableRegulatorCategory {
            @Config.Name("Enable Portable Regulator")
            @Config.Comment("Enables the portable temperature regulator item.")
            public boolean enable = true;

            @Config.RangeInt(min = 1, max = 10000000)
            @Config.Name("Energy Capacity")
            @Config.Comment("The total energy capacity of the Portable Regulator.")
            public int energyCapacity = 200000;

            @Config.RangeInt(min = 1, max = 10000)
            @Config.Name("Energy Cost")
            @Config.Comment("The amount of RF/t the Portable Regulator will drain while active.")
            public int energyCost = 5;

            @Config.Name("Shorten Tooltip")
            @Config.Comment("Should the tooltip energy values be shortened? (400,000 -> 400k)")
            public boolean shortenEnergyTooltip = true;

            @Config.RangeInt(min = -1, max = 50)
            @Config.Name("Max Cooling Range")
            @Config.Comment("The maximum cooling limit for this tool. If the environmental temperature is 30, the target temperature will be (30 - maxCooling). Setting this value to -1 will remove the limit.")
            public int maxCooling = -1;

            @Config.RangeInt(min = -1, max = 50)
            @Config.Name("Max Heating Range")
            @Config.Comment("The maximum heating limit for this tool. If the environmental temperature is 0, the target temperature will be (0 + maxHeating). Setting this value to -1 will remove the limit.")
            public int maxHeating = -1;
        }
    }

    @Mod.EventBusSubscriber(modid = SurvivalTools.MOD_ID)
    public static class ConfigChangeListener {
        @SubscribeEvent
        public static void onConfigChange(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(SurvivalTools.MOD_ID)) {
                ConfigManager.sync(SurvivalTools.MOD_ID, Config.Type.INSTANCE);
            }
        }
    }
}
