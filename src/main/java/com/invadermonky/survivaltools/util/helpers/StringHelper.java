package com.invadermonky.survivaltools.util.helpers;

import com.invadermonky.survivaltools.SurvivalTools;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class StringHelper {
    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();

    public static String getTranslationKey(String name, String type, String... params) {
        StringBuilder s = new StringBuilder(type + "." + SurvivalTools.MOD_ID + ":" + name);
        for (String p : params) {
            s.append(".").append(p);
        }
        return s.toString();
    }

    public static String getTranslatedString(String name, String type, String... params) {
        return I18n.format(getTranslationKey(name, type, params));
    }

    public static ITextComponent getTranslatedComponent(String name, String type, String... params) {
        return new TextComponentTranslation(getTranslationKey(name, type, params));
    }

    public static String getCleanNumber(long value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE)
            return getCleanNumber(Long.MIN_VALUE + 1);
        if (value < 0)
            return "-" + getCleanNumber(-value);
        if (value < 1000)
            return Long.toString(value); //deal with easy case

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != ((double) truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }

    static {
        suffixes.put(1_000L, "k");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "G");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "P");
        suffixes.put(1_000_000_000_000_000_000L, "E");
    }
}
