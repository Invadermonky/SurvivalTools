package com.invadermonky.survivaltools.registry;

import com.invadermonky.survivaltools.SurvivalTools;
import com.invadermonky.survivaltools.api.IAddition;
import com.invadermonky.survivaltools.blocks.BlockCentralAirUnit;
import com.invadermonky.survivaltools.blocks.BlockOpenBarrel;
import com.invadermonky.survivaltools.blocks.BlockPoweredPurifier;
import com.invadermonky.survivaltools.blocks.BlockSolidFuelPurifier;
import com.invadermonky.survivaltools.tile.TileCentralAirUnit;
import com.invadermonky.survivaltools.tile.TileOpenBarrel;
import com.invadermonky.survivaltools.tile.TilePoweredPurifier;
import com.invadermonky.survivaltools.tile.TileSolidFuelPurifier;
import com.invadermonky.survivaltools.util.libs.CreativeTabST;
import com.invadermonky.survivaltools.util.libs.LibNames;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = SurvivalTools.MOD_ID)
public class ModBlocksST {
    public static BlockCentralAirUnit central_air_unit;
    public static BlockOpenBarrel open_barrel;
    public static BlockPoweredPurifier powered_purifier;
    public static BlockSolidFuelPurifier solid_fuel_purifier;

    public static Map<Block, Class<? extends TileEntity>> allBlocks = new LinkedHashMap<>();

    public static <T extends Block & IAddition> void addBlockToRegister(T block, String blockId) {
        addBlockToRegister(block, blockId, null);
    }

    public static <T extends Block & IAddition> void addBlockToRegister(T block, String blockId, @Nullable Class<? extends TileEntity> clazz) {
        if (block != null && block.isEnabled()) {
            block.setRegistryName(SurvivalTools.MOD_ID, blockId)
                    .setTranslationKey(block.getRegistryName().toString())
                    .setCreativeTab(CreativeTabST.TAB_ST);
            allBlocks.put(block, clazz);
        }
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();
        allBlocks.keySet().forEach(registry::register);
    }

    @SubscribeEvent
    public static void registerBlockItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        allBlocks.keySet().forEach(block -> registry.register(new ItemBlock(block).setRegistryName(block.getRegistryName())));

        registerTileEntities();
    }

    @SubscribeEvent
    public static void registerBlockModels(ModelRegistryEvent event) {
        allBlocks.keySet().forEach(block -> {
            if (block instanceof IAddition) {
                ((IAddition) block).registerModel(event);
            }
        });
    }

    private static void registerTileEntities() {
        allBlocks.forEach((block, teClass) -> {
            if (teClass != null) {
                GameRegistry.registerTileEntity(teClass, block.getRegistryName());
            }
        });
    }

    static {
        addBlockToRegister(central_air_unit = new BlockCentralAirUnit(), LibNames.CENTRAL_AIR_UNIT, TileCentralAirUnit.class);
        addBlockToRegister(open_barrel = new BlockOpenBarrel(), LibNames.OPEN_BARREL, TileOpenBarrel.class);
        addBlockToRegister(powered_purifier = new BlockPoweredPurifier(), LibNames.POWERED_PURIFIER, TilePoweredPurifier.class);
        addBlockToRegister(solid_fuel_purifier = new BlockSolidFuelPurifier(), LibNames.SOLID_FUEL_PURIFIER, TileSolidFuelPurifier.class);
    }
}
