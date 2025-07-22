package com.invadermonky.survivaltools.proxy;

import com.invadermonky.survivaltools.SurvivalTools;
import com.invadermonky.survivaltools.api.IProxy;
import com.invadermonky.survivaltools.api.SurvivalToolsAPI;
import com.invadermonky.survivaltools.client.gui.GuiHandlerST;
import com.invadermonky.survivaltools.compat.bloodmagic.BloodMagicST;
import com.invadermonky.survivaltools.compat.botania.BotaniaST;
import com.invadermonky.survivaltools.compat.embers.EmbersST;
import com.invadermonky.survivaltools.compat.naturesaura.NaturesAuraST;
import com.invadermonky.survivaltools.compat.patchouli.PatchouliST;
import com.invadermonky.survivaltools.compat.thaumcraft.ThaumcraftST;
import com.invadermonky.survivaltools.compat.waterskin.WaterskinSDST;
import com.invadermonky.survivaltools.compat.waterskin.WaterskinTaNST;
import com.invadermonky.survivaltools.registry.ModBlocksST;
import com.invadermonky.survivaltools.registry.ModItemsST;
import com.invadermonky.survivaltools.survivalmods.SurvivalModSD;
import com.invadermonky.survivaltools.survivalmods.SurvivalModTAN;
import com.invadermonky.survivaltools.util.libs.ModIds;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.util.ArrayList;
import java.util.List;

public class CommonProxy {
    protected List<IProxy> modCompat = new ArrayList<>();

    public void preInit(FMLPreInitializationEvent event) {
        loadSurvivalMods(); //Survival mods have to be loaded first or the mod compat breaks.
        buildCompat();
        modCompat.forEach(IProxy::preInit);
        ModItemsST.allItems.stream().filter(item -> item instanceof IProxy).forEach(item -> ((IProxy) item).preInit());
        ModBlocksST.allBlocks.keySet().stream().filter(block -> block instanceof IProxy).forEach(block -> ((IProxy) block).preInit());
    }

    public void init(FMLInitializationEvent event) {
        modCompat.forEach(IProxy::init);
        ModItemsST.allItems.stream().filter(item -> item instanceof IProxy).forEach(item -> ((IProxy) item).init());
        ModBlocksST.allBlocks.keySet().stream().filter(block -> block instanceof IProxy).forEach(block -> ((IProxy) block).init());
        NetworkRegistry.INSTANCE.registerGuiHandler(SurvivalTools.instance, new GuiHandlerST());
    }

    public void postInit(FMLPostInitializationEvent event) {
        modCompat.forEach(IProxy::postInit);
        ModItemsST.allItems.stream().filter(item -> item instanceof IProxy).forEach(item -> ((IProxy) item).postInit());
        ModBlocksST.allBlocks.keySet().stream().filter(block -> block instanceof IProxy).forEach(block -> ((IProxy) block).postInit());
    }

    private void loadSurvivalMods() {
        if (ModIds.simpledifficulty.isLoaded)
            SurvivalToolsAPI.registerSurvivalMod(new SurvivalModSD());
        if (ModIds.tough_as_nails.isLoaded)
            SurvivalToolsAPI.registerSurvivalMod(new SurvivalModTAN());
    }

    private void buildCompat() {
        if (ModIds.bloodmagic.isLoaded)
            modCompat.add(new BloodMagicST());
        if (ModIds.botania.isLoaded)
            modCompat.add(new BotaniaST());
        if (ModIds.embers.isLoaded)
            modCompat.add(new EmbersST());
        if (ModIds.natures_aura.isLoaded)
            modCompat.add(new NaturesAuraST());
        if (ModIds.patchouli.isLoaded)
            modCompat.add(new PatchouliST());
        if (ModIds.thaumcraft.isLoaded)
            modCompat.add(new ThaumcraftST());
        if (ModIds.waterskin.isLoaded) {
            if (ModIds.simpledifficulty.isLoaded)
                modCompat.add(new WaterskinSDST());
            if (ModIds.tough_as_nails.isLoaded)
                modCompat.add(new WaterskinTaNST());
        }
    }
}
