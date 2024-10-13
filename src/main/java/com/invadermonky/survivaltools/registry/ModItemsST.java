package com.invadermonky.survivaltools.registry;

import com.invadermonky.survivaltools.SurvivalTools;
import com.invadermonky.survivaltools.api.IAddition;
import com.invadermonky.survivaltools.config.ConfigHandlerST;
import com.invadermonky.survivaltools.items.*;
import com.invadermonky.survivaltools.util.libs.CreativeTabST;
import com.invadermonky.survivaltools.util.libs.LibNames;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = SurvivalTools.MOD_ID)
public class ModItemsST {
    public static ItemCanteen canteen;
    public static ItemCanteen canteen_reinforced;
    public static ItemHandheldPurifier handheld_purifier;
    public static ItemHydrationPack hydration_pack;
    public static ItemHydrationPack hydration_pack_reinforced;
    public static ItemPortablePurifier portable_purifier;
    public static ItemPortableRegulator portable_regulator;

    public static List<Item> allItems = new ArrayList<>();

    public static <T extends Item & IAddition> void addItemToRegister(T item, String itemId) {
        if(item != null && item.isEnabled()) {
            item.setRegistryName(new ResourceLocation(SurvivalTools.MOD_ID, itemId))
                    .setTranslationKey(item.getRegistryName().toString())
                    .setCreativeTab(CreativeTabST.TAB_ST);
            allItems.add(item);
        }
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        allItems.forEach(registry::register);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerItemModels(ModelRegistryEvent event) {
        ModItemsST.allItems.forEach(item -> {
            if(item instanceof IAddition) {
                ((IAddition) item).registerModel(event);
            }
        });
    }

    static {
        addItemToRegister(canteen = new ItemCanteen(ConfigHandlerST.simple_tools.canteen.fluidCapacityBasic), LibNames.CANTEEN);
        addItemToRegister(canteen_reinforced = new ItemCanteen(ConfigHandlerST.simple_tools.canteen.fluidCapacityReinforced), LibNames.CANTEEN_REINFORCED);
        addItemToRegister(hydration_pack = new ItemHydrationPack(ConfigHandlerST.simple_tools.hydration_pack.fluidCapacityBasic), LibNames.HYDRATION_PACK);
        addItemToRegister(hydration_pack_reinforced = new ItemHydrationPack(ConfigHandlerST.simple_tools.hydration_pack.fluidCapacityReinforced), LibNames.HYDRATION_PACK_REINFORCED);
        addItemToRegister(handheld_purifier = new ItemHandheldPurifier(), LibNames.HANDHELD_PURIFIER);
        addItemToRegister(portable_purifier = new ItemPortablePurifier(), LibNames.PORTABLE_PURIFIER);
        addItemToRegister(portable_regulator = new ItemPortableRegulator(), LibNames.PORTABLE_REGULATOR);
    }
}
