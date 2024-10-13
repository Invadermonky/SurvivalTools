package com.invadermonky.survivaltools.proxy;

import com.invadermonky.survivaltools.api.IAddition;
import com.invadermonky.survivaltools.api.IProxy;
import com.invadermonky.survivaltools.compat.bloodmagic.BloodMagicST;
import com.invadermonky.survivaltools.compat.botania.BotaniaST;
import com.invadermonky.survivaltools.compat.embers.EmbersST;
import com.invadermonky.survivaltools.compat.naturesaura.NaturesAuraST;
import com.invadermonky.survivaltools.compat.thaumcraft.ThaumcraftST;
import com.invadermonky.survivaltools.registry.ModBlocksST;
import com.invadermonky.survivaltools.registry.ModItemsST;
import com.invadermonky.survivaltools.util.libs.ModIds;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.ArrayList;
import java.util.List;

public class CommonProxy {
    protected List<IProxy> modCompat = new ArrayList<>();

    public void preInit(FMLPreInitializationEvent event) {
        buildCompat();
        modCompat.forEach(IProxy::preInit);
        ModItemsST.allItems.forEach(item -> {if(item instanceof IAddition) ((IAddition) item).preInit();});
        ModBlocksST.allBlocks.keySet().forEach(block -> {if(block instanceof IAddition) ((IAddition) block).preInit();});
    }

    public void init(FMLInitializationEvent event) {
        modCompat.forEach(IProxy::init);
        ModItemsST.allItems.forEach(item -> {if(item instanceof IAddition) ((IAddition) item).init();});
        ModBlocksST.allBlocks.keySet().forEach(block -> {if(block instanceof IAddition) ((IAddition) block).init();});
    }

    public void postInit(FMLPostInitializationEvent event) {
        modCompat.forEach(IProxy::postInit);
        ModItemsST.allItems.forEach(item -> {if(item instanceof IAddition) ((IAddition) item).postInit();});
        ModBlocksST.allBlocks.keySet().forEach(block -> {if(block instanceof IAddition) ((IAddition) block).postInit();});
    }

    private void buildCompat() {
        if(ModIds.bloodmagic.isLoaded) modCompat.add(new BloodMagicST());
        if(ModIds.botania.isLoaded) modCompat.add(new BotaniaST());
        if(ModIds.embers.isLoaded) modCompat.add(new EmbersST());
        if(ModIds.natures_aura.isLoaded) modCompat.add(new NaturesAuraST());
        if(ModIds.thaumcraft.isLoaded) modCompat.add(new ThaumcraftST());
    }
}
