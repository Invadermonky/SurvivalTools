package com.invadermonky.survivaltools.proxy;

import com.invadermonky.survivaltools.api.IModModule;
import com.invadermonky.survivaltools.api.IProxy;
import com.invadermonky.survivaltools.registry.RegistrarST;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@SuppressWarnings("unused")
public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        MOD_MODULES.forEach(IModModule::preInitClient);
        RegistrarST.getProxyAdditions().forEach(IProxy::preInitClient);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        MOD_MODULES.forEach(IModModule::initClient);
        RegistrarST.getProxyAdditions().forEach(IProxy::initClient);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
        MOD_MODULES.forEach(IModModule::postInitClient);
        RegistrarST.getProxyAdditions().forEach(IProxy::postInitClient);
    }
}
