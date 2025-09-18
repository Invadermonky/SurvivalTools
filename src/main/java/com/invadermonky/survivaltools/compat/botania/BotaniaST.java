package com.invadermonky.survivaltools.compat.botania;

import com.invadermonky.survivaltools.api.IModModule;
import com.invadermonky.survivaltools.compat.botania.blocks.BlockSpecialFlowerST;
import com.invadermonky.survivaltools.compat.botania.items.ItemSeasonsRing;
import com.invadermonky.survivaltools.compat.botania.tiles.subtiles.SubTileGryllzalia;
import com.invadermonky.survivaltools.compat.botania.tiles.subtiles.SubTilePurePitcher;
import com.invadermonky.survivaltools.registry.RegistrarST;
import com.invadermonky.survivaltools.util.libs.LibNames;

public class BotaniaST implements IModModule {
    public static BlockSpecialFlowerST<SubTileGryllzalia> gryllzalia;
    public static BlockSpecialFlowerST<SubTilePurePitcher> pure_pitcher;

    @Override
    public void preInit() {
        RegistrarST.addAdditionToRegister(gryllzalia = new BlockSpecialFlowerST<>(new SubTileGryllzalia(), LibNames.GRYLLZALIA));
        RegistrarST.addAdditionToRegister(pure_pitcher = new BlockSpecialFlowerST<>(new SubTilePurePitcher(), LibNames.PURE_PITCHER));
        RegistrarST.addAdditionToRegister(new ItemSeasonsRing());
    }
}
