package com.invadermonky.survivaltools.network.packets;

import com.invadermonky.survivaltools.tile.base.AbstractTileWaterPurifier;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketDrainPurifierTank implements IMessage {
    public BlockPos purifierPos;
    /** 0 is input tank 1 is output tank */
    public byte tankType;

    public PacketDrainPurifierTank(BlockPos pos, byte tankType) {
        this.purifierPos = pos;
        this.tankType = tankType;
    }

    public PacketDrainPurifierTank() {
        this.purifierPos = null;
        this.tankType = 0;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.purifierPos = BlockPos.fromLong(buf.readLong());
        this.tankType = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(this.purifierPos.toLong());
        buf.writeByte(this.tankType);
    }

    public static class Handler implements IMessageHandler<PacketDrainPurifierTank, IMessage> {

        @Override
        public IMessage onMessage(PacketDrainPurifierTank message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
                World world = ctx.getServerHandler().player.getEntityWorld();
                TileEntity tile = world.getTileEntity(message.purifierPos);
                if (tile instanceof AbstractTileWaterPurifier) {
                    if (message.tankType == (byte) 0) {
                        ((AbstractTileWaterPurifier) tile).emptyInputFluidTank();
                    } else if (message.tankType == (byte) 1) {
                        ((AbstractTileWaterPurifier) tile).emptyOutputFluidTank();
                    }
                }
            });
            return null;
        }
    }

}
