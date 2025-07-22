package com.invadermonky.survivaltools.network;

import com.invadermonky.survivaltools.SurvivalTools;
import com.invadermonky.survivaltools.network.packets.PacketDrainPurifierTank;
import com.invadermonky.survivaltools.util.ChatUtils;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandlerST {
    public static final SimpleNetworkWrapper INSTANCE = new SimpleNetworkWrapper(SurvivalTools.MOD_ID);

    private static int packetId = 0;

    private static int nextId() {
        return packetId++;
    }

    public static void init() {
        INSTANCE.registerMessage(ChatUtils.PacketNoSpamChat.Handler.class, ChatUtils.PacketNoSpamChat.class, nextId(), Side.CLIENT);
        INSTANCE.registerMessage(PacketDrainPurifierTank.Handler.class, PacketDrainPurifierTank.class, nextId(), Side.SERVER);
    }
}
