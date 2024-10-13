package com.invadermonky.survivaltools.network;

import com.invadermonky.survivaltools.SurvivalTools;
import com.invadermonky.survivaltools.util.ChatUtils;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandlerST {
    public static final SimpleNetworkWrapper INSTANCE = new SimpleNetworkWrapper(SurvivalTools.MOD_ID);

    public static void init() {
        INSTANCE.registerMessage(ChatUtils.PacketNoSpamChat.Handler.class, ChatUtils.PacketNoSpamChat.class, 0, Side.CLIENT);
    }
}
