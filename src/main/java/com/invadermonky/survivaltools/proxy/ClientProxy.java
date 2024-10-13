package com.invadermonky.survivaltools.proxy;

import com.invadermonky.survivaltools.api.IProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        modCompat.forEach(IProxy::preInitClient);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        modCompat.forEach(IProxy::initClient);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
        modCompat.forEach(IProxy::postInitClient);
    }
}
