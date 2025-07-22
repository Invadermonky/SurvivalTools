package com.invadermonky.survivaltools.compat.groovyscript;

import com.cleanroommc.groovyscript.documentation.linkgenerator.BasicLinkGenerator;
import com.invadermonky.survivaltools.SurvivalTools;

public class GSLinkGenerator extends BasicLinkGenerator {
    @Override
    public String id() {
        return SurvivalTools.MOD_ID;
    }

    @Override
    protected String domain() {
        return "https://github.com/Invadermonky/SurvivalTools/";
    }

    @Override
    protected String version() {
        return SurvivalTools.VERSION;
    }
}
