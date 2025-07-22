package com.invadermonky.survivaltools.compat.botania;

import com.invadermonky.survivaltools.api.IProxy;
import com.invadermonky.survivaltools.blocks.BlockSpecialFlowerST;
import com.invadermonky.survivaltools.items.ItemSeasonsRing;
import com.invadermonky.survivaltools.registry.ModItemsST;
import com.invadermonky.survivaltools.tile.subtile.SubTileGryllzalia;
import com.invadermonky.survivaltools.tile.subtile.SubTilePurePitcher;
import com.invadermonky.survivaltools.util.libs.LibNames;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BotaniaST implements IProxy {
    public static ItemSeasonsRing seasons_ring;
    public static BlockSpecialFlowerST<SubTileGryllzalia> gryllzalia;
    public static BlockSpecialFlowerST<SubTilePurePitcher> pure_pitcher;
    private static BotaniaST instance;

    @Override
    public void preInit() {
        if (gryllzalia.isEnabled()) {
            gryllzalia.preInit();
        }
        if (pure_pitcher.isEnabled()) {
            pure_pitcher.preInit();
        }
    }

    @Override
    public void init() {
        if (gryllzalia.isEnabled()) {
            gryllzalia.init();
        }
        if (pure_pitcher.isEnabled()) {
            pure_pitcher.init();
        }
    }

    @Override
    public void postInit() {
        if (gryllzalia.isEnabled()) {
            gryllzalia.postInit();
        }
        if (pure_pitcher.isEnabled()) {
            pure_pitcher.postInit();
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void preInitClient() {
        if (gryllzalia.isEnabled()) {
            gryllzalia.registerModel(null);
        }
        if (pure_pitcher.isEnabled()) {
            pure_pitcher.registerModel(null);
        }
    }

    static {
        ModItemsST.addItemToRegister(seasons_ring = new ItemSeasonsRing(), LibNames.SEASONS_RING);
        gryllzalia = new BlockSpecialFlowerST<>(new SubTileGryllzalia(), LibNames.GRYLLZALIA);
        pure_pitcher = new BlockSpecialFlowerST<>(new SubTilePurePitcher(), LibNames.PURE_PITCHER);
    }
}
