package com.invadermonky.survivaltools.proxy;

import com.invadermonky.survivaltools.api.IProxy;
import com.invadermonky.survivaltools.registry.ModBlocksST;
import com.invadermonky.survivaltools.registry.ModItemsST;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        modCompat.forEach(IProxy::preInitClient);
        ModItemsST.allItems.stream().filter(item -> item instanceof IProxy).forEach(item -> ((IProxy) item).preInitClient());
        ModBlocksST.allBlocks.keySet().stream().filter(block -> block instanceof IProxy).forEach(block -> ((IProxy) block).preInitClient());
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        modCompat.forEach(IProxy::initClient);
        ModItemsST.allItems.stream().filter(item -> item instanceof IProxy).forEach(item -> ((IProxy) item).initClient());
        ModBlocksST.allBlocks.keySet().stream().filter(block -> block instanceof IProxy).forEach(block -> ((IProxy) block).initClient());
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
        modCompat.forEach(IProxy::postInitClient);
        ModItemsST.allItems.stream().filter(item -> item instanceof IProxy).forEach(item -> ((IProxy) item).postInitClient());
        ModBlocksST.allBlocks.keySet().stream().filter(block -> block instanceof IProxy).forEach(block -> ((IProxy) block).postInitClient());
    }
}
