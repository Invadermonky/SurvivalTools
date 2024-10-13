package com.invadermonky.survivaltools.util.helpers;

import com.charles445.simpledifficulty.api.config.QuickConfig;

public class SDHelper {
    public static boolean isSDTemperatureEnabled() {
        return QuickConfig.isTemperatureEnabled();
    }

    public static boolean isSDThirstEnabled() {
        return QuickConfig.isThirstEnabled();
    }
}
