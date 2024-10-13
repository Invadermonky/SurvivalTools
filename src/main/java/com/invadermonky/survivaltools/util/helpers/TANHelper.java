package com.invadermonky.survivaltools.util.helpers;

import toughasnails.api.config.GameplayOption;
import toughasnails.api.config.SyncedConfig;

public class TANHelper {
    public static boolean isTanTemperatureEnabled() {
        return SyncedConfig.getBooleanValue(GameplayOption.ENABLE_TEMPERATURE);
    }

    public static boolean isTanThirstEnabled() {
        return SyncedConfig.getBooleanValue(GameplayOption.ENABLE_THIRST);
    }
}
