package com.invadermonky.survivaltools.proxy;

import com.invadermonky.survivaltools.SurvivalTools;
import com.invadermonky.survivaltools.api.IModModule;
import com.invadermonky.survivaltools.api.IProxy;
import com.invadermonky.survivaltools.api.SurvivalToolsAPI;
import com.invadermonky.survivaltools.client.gui.GuiHandlerST;
import com.invadermonky.survivaltools.compat.bloodmagic.BloodMagicST;
import com.invadermonky.survivaltools.compat.botania.BotaniaST;
import com.invadermonky.survivaltools.compat.embers.EmbersST;
import com.invadermonky.survivaltools.compat.naturesaura.NaturesAuraST;
import com.invadermonky.survivaltools.compat.patchouli.PatchouliST;
import com.invadermonky.survivaltools.compat.survivaltools.SurvivalToolsST;
import com.invadermonky.survivaltools.compat.thaumcraft.ThaumcraftST;
import com.invadermonky.survivaltools.compat.waterskin.WaterskinSDST;
import com.invadermonky.survivaltools.compat.waterskin.WaterskinTaNST;
import com.invadermonky.survivaltools.registry.RegistrarST;
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
    protected final List<IModModule> MOD_MODULES = new ArrayList<>();

    public void preInit(FMLPreInitializationEvent event) {
        loadSurvivalMods(); //Survival mods have to be loaded first or the mod compat breaks.
        buildModules();
        MOD_MODULES.forEach(IModModule::preInit);
        RegistrarST.getProxyAdditions().forEach(IProxy::preInit);
    }

    public void init(FMLInitializationEvent event) {
        MOD_MODULES.forEach(IModModule::init);
        RegistrarST.getProxyAdditions().forEach(IProxy::init);
        NetworkRegistry.INSTANCE.registerGuiHandler(SurvivalTools.instance, new GuiHandlerST());
    }

    public void postInit(FMLPostInitializationEvent event) {
        MOD_MODULES.forEach(IModModule::postInit);
        RegistrarST.getProxyAdditions().forEach(IProxy::postInit);
    }

    private void loadSurvivalMods() {
        if (ModIds.simpledifficulty.isLoaded) SurvivalToolsAPI.registerSurvivalMod(new SurvivalModSD());
        if (ModIds.tough_as_nails.isLoaded) SurvivalToolsAPI.registerSurvivalMod(new SurvivalModTAN());
    }

    private void buildModules() {
        MOD_MODULES.add(new SurvivalToolsST());
        if (ModIds.bloodmagic.isLoaded) MOD_MODULES.add(new BloodMagicST());
        if (ModIds.botania.isLoaded) MOD_MODULES.add(new BotaniaST());
        if (ModIds.embers.isLoaded) MOD_MODULES.add(new EmbersST());
        if (ModIds.natures_aura.isLoaded) MOD_MODULES.add(new NaturesAuraST());
        if (ModIds.patchouli.isLoaded) MOD_MODULES.add(new PatchouliST());
        if (ModIds.thaumcraft.isLoaded) MOD_MODULES.add(new ThaumcraftST());
        if (ModIds.waterskin.isLoaded) {
            if (ModIds.simpledifficulty.isLoaded) MOD_MODULES.add(new WaterskinSDST());
            if (ModIds.tough_as_nails.isLoaded) MOD_MODULES.add(new WaterskinTaNST());
        }
    }
}
