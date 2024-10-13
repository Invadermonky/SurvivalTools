package com.invadermonky.survivaltools.events;

import com.invadermonky.survivaltools.SurvivalTools;
import com.invadermonky.survivaltools.items.ItemHydrationPack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(modid = SurvivalTools.MOD_ID)
public class ClientEventHandler {
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onTooltipEvent(ItemTooltipEvent event) {
        ItemHydrationPack.onTooltipEvent(event);
    }
}
