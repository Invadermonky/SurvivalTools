package com.invadermonky.survivaltools.compat.groovyscript;

import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;
import com.invadermonky.survivaltools.compat.groovyscript.handlers.HandheldPurifier;
import com.invadermonky.survivaltools.compat.groovyscript.handlers.Purifier;

public class GSContainer extends GroovyPropertyContainer {
    public final HandheldPurifier HandheldPurifier = new HandheldPurifier();
    public final Purifier Purifier = new Purifier();

    public GSContainer() {
        this.addProperty(HandheldPurifier);
        this.addProperty(Purifier);
    }
}
