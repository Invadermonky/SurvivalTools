package com.invadermonky.survivaltools.util.helpers;

import com.invadermonky.survivaltools.SurvivalTools;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogHelper {
    private static final Logger LOG = LogManager.getLogger(SurvivalTools.MOD_NAME);
    public static void debug(Object obj) { LOG.debug(obj); }
    public static void error(Object obj) { LOG.error(obj); }
    public static void fatal(Object obj) { LOG.fatal(obj); }
    public static void info(Object obj) { LOG.info(obj); }
    public static void trace(Object obj) { LOG.trace(obj); }
    public static void warn(Object obj) { LOG.warn(obj); }
}
