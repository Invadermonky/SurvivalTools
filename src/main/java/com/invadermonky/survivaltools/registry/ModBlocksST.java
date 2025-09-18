package com.invadermonky.survivaltools.registry;

import com.invadermonky.survivaltools.SurvivalTools;
import com.invadermonky.survivaltools.api.IAddition;
import net.minecraft.block.Block;
import net.minecraftforge.fml.common.registry.GameRegistry;

@GameRegistry.ObjectHolder(SurvivalTools.MOD_ID)
public class ModBlocksST {
    public static final Block CENTRAL_AIR_UNIT = null;
    public static final Block OPEN_BARREL = null;
    public static final Block POWERED_PURIFIER = null;
    public static final Block SOLID_FUEL_PURIFIER = null;

    public static boolean isBlockEnabled(Block block) {
        return block != null && (!(block instanceof IAddition) || ((IAddition) block).isEnabled());
    }
}
