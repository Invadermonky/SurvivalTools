package com.invadermonky.survivaltools.events;

import com.invadermonky.survivaltools.SurvivalTools;
import com.invadermonky.survivaltools.compat.survivaltools.items.ItemHydrationPack;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = SurvivalTools.MOD_ID)
public class CommonEventHandler {
    @SubscribeEvent
    public static void onEntityUpdate(LivingEvent.LivingUpdateEvent event) {
        ItemHydrationPack.onEntityUpdate(event);
    }
}
