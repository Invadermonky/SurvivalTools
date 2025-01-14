package com.invadermonky.survivaltools.config;

import com.invadermonky.survivaltools.SurvivalTools;
import com.invadermonky.survivaltools.config.modules.*;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = SurvivalTools.MOD_ID)
public class ConfigHandlerST {
    public static ConfigBloodMagic blood_magic = new ConfigBloodMagic();
    public static ConfigBotania botania = new ConfigBotania();
    public static ConfigEmbers embers = new ConfigEmbers();
    public static ConfigFluxTools flux_tools = new ConfigFluxTools();
    public static ConfigNaturesAura natures_aura = new ConfigNaturesAura();
    public static ConfigOpenBarrel open_barrel = new ConfigOpenBarrel();
    public static ConfigSimpleTools simple_tools = new ConfigSimpleTools();
    public static ConfigThaumcraft thaumcraft = new ConfigThaumcraft();

    @Config.Comment("Enables creative menu filled variants of any applicable tools/times (such as the Hydration Pack).")
    public static boolean enableFullVariants = true;

    @Config.Comment("Force loads Survival Tools temperature items and machines even if TAN/SimpleDifficulty temperature feature is disabled.")
    public static boolean forceLoadTemperatureFeatures = false;

    @Config.Comment("Force loads Survival Tools thirst items and machines even if TAN/SimpleDifficulty thirst feature is disabled.")
    public static boolean forceLoadThirstFeatures = false;

    @Mod.EventBusSubscriber(modid = SurvivalTools.MOD_ID)
    public static class ConfigChangeListener {
        @SubscribeEvent
        public static void onConfigChange(ConfigChangedEvent.OnConfigChangedEvent event) {
            if(event.getModID().equals(SurvivalTools.MOD_ID)) {
                ConfigManager.sync(SurvivalTools.MOD_ID, Config.Type.INSTANCE);
                ModTags.syncConfig();
            }
        }
    }
}
