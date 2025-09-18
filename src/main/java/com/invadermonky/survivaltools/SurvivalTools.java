package com.invadermonky.survivaltools;

import com.invadermonky.survivaltools.network.PacketHandlerST;
import com.invadermonky.survivaltools.proxy.CommonProxy;
import com.invadermonky.survivaltools.util.helpers.LogHelper;
import com.invadermonky.survivaltools.util.libs.ModIds;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(
        modid = SurvivalTools.MOD_ID,
        name = SurvivalTools.MOD_NAME,
        version = SurvivalTools.VERSION,
        acceptedMinecraftVersions = SurvivalTools.MC_VERSION,
        dependencies = SurvivalTools.DEPENDENCIES
)
public class SurvivalTools {
    public static final String MOD_ID = "survivaltools";
    public static final String MOD_NAME = "Survival Tools";
    public static final String VERSION = "1.2.1";
    public static final String MC_VERSION = "[1.12.2]";
    public static final String DEPENDENCIES =
            "required-after:" + ModIds.ConstIds.baubles +
                    ";after:" + ModIds.ConstIds.bloodmagic +
                    ";after:" + ModIds.ConstIds.botania +
                    ";after:" + ModIds.ConstIds.embers +
                    ";after:" + ModIds.ConstIds.natures_aura +
                    ";after:" + ModIds.ConstIds.patchouli +
                    ";after:" + ModIds.ConstIds.simpledifficulty +
                    ";after:" + ModIds.ConstIds.thaumcraft +
                    ";after:" + ModIds.ConstIds.toughasnails;

    public static final String ProxyClientClass = "com.invadermonky." + MOD_ID + ".proxy.ClientProxy";
    public static final String ProxyServerClass = "com.invadermonky." + MOD_ID + ".proxy.CommonProxy";

    @Mod.Instance(MOD_ID)
    public static SurvivalTools instance;

    @SidedProxy(clientSide = ProxyClientClass, serverSide = ProxyServerClass)
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LogHelper.info("Starting " + MOD_NAME);
        proxy.preInit(event);
        LogHelper.debug("Finished preInit phase.");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
        PacketHandlerST.init();
        LogHelper.debug("Finished init phase.");
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
        LogHelper.debug("Finished postInit phase.");
    }
}
