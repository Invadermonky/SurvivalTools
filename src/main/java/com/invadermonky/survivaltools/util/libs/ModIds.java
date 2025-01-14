package com.invadermonky.survivaltools.util.libs;

import WayofTime.bloodmagic.BloodMagic;
import baubles.common.Baubles;
import com.charles445.simpledifficulty.SimpleDifficulty;
import com.invadermonky.magicultureintegrations.MagicultureIntegrations;
import com.invadermonky.survivaltools.util.helpers.ModHelper;
import de.ellpeck.naturesaura.NaturesAura;
import teamroots.embers.Embers;
import thaumcraft.Thaumcraft;
import toughasnails.core.ToughAsNails;
import vazkii.botania.common.lib.LibMisc;
import vazkii.patchouli.common.base.Patchouli;

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
    ;

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
        public static final String baubles = Baubles.MODID;
        public static final String bloodmagic = BloodMagic.MODID;
        public static final String botania = LibMisc.MOD_ID;
        public static final String embers = Embers.MODID;
        public static final String magicluture_integrations = MagicultureIntegrations.MOD_ID;
        public static final String natures_aura = NaturesAura.MOD_ID;
        public static final String patchouli = Patchouli.MOD_ID;
        public static final String simpledifficulty = SimpleDifficulty.MODID;
        public static final String thaumcraft = Thaumcraft.MODID;
        public static final String toughasnails = ToughAsNails.MOD_ID;
    }
}
