package com.invadermonky.survivaltools.util.libs;

import com.invadermonky.survivaltools.util.helpers.ModHelper;

import javax.annotation.Nullable;

public enum ModIds {
    baubles(ConstIds.baubles),
    bloodmagic(ConstIds.bloodmagic),
    botania(ConstIds.botania),
    embers(ConstIds.embers),
    magiculture_integrations(ConstIds.magicluture_integrations),
    natures_aura(ConstIds.natures_aura),
    patchouli(ConstIds.patchouli),
    simpledifficulty(ConstIds.simpledifficulty),
    thaumcraft(ConstIds.thaumcraft),
    tough_as_nails(ConstIds.toughasnails),
    waterskin(ConstIds.waterskin);

    public final String modId;
    public final String version;
    public final boolean isLoaded;

    ModIds(String modId) {
        this(modId, null);
    }

    ModIds(String modId, @Nullable String version) {
        this.modId = modId;
        this.version = version;
        this.isLoaded = ModHelper.isModLoaded(modId, version);
    }

    ModIds(String modId, @Nullable String version, boolean isMinVersion, boolean isMaxVersion) {
        this.modId = modId;
        this.version = version;
        this.isLoaded = ModHelper.isModLoaded(modId, version, isMinVersion, isMaxVersion);
    }

    @Override
    public String toString() {
        return this.modId;
    }

    public static class ConstIds {
        public static final String baubles = "baubles";
        public static final String bloodmagic = "bloodmagic";
        public static final String botania = "botania";
        public static final String embers = "embers";
        public static final String magicluture_integrations = "magicultureintegrations";
        public static final String natures_aura = "naturesaura";
        public static final String patchouli = "patchouli";
        public static final String simpledifficulty = "simpledifficulty";
        public static final String thaumcraft = "thaumcraft";
        public static final String toughasnails = "toughasnails";
        public static final String waterskin = "waterskin";
    }
}
