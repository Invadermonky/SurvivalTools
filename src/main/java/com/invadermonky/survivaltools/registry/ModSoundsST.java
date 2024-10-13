package com.invadermonky.survivaltools.registry;

import com.invadermonky.survivaltools.SurvivalTools;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = SurvivalTools.MOD_ID)
public class ModSoundsST {
    public static final SoundEvent easter_egg;

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        event.getRegistry().register(easter_egg);
    }

    private static SoundEvent makeSoundEvent(String unloc) {
        ResourceLocation loc = new ResourceLocation(SurvivalTools.MOD_ID, unloc);
        return new SoundEvent(loc).setRegistryName(loc);
    }

    static {
        easter_egg = makeSoundEvent("easter_egg");
    }
}
