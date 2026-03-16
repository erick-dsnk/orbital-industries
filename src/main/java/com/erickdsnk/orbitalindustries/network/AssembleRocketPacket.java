package com.erickdsnk.orbitalindustries.network;

import java.util.List;

import io.netty.buffer.ByteBuf;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.erickdsnk.orbitalindustries.registry.ItemRegistry;
import com.erickdsnk.orbitalindustries.tile.rocket.RocketAssemblerTileEntity;
import com.erickdsnk.orbitalindustries.item.rocket.ItemRocket;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

/**
 * Client-to-server packet: assemble rocket at the given tile position. Server
 * validates, consumes part stacks, creates ItemRocket with part ids in output
 * slot.
 */
public class AssembleRocketPacket extends BasePacket {

    private int x, y, z;

    public AssembleRocketPacket() {
    }

    public AssembleRocketPacket(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    protected void readPayload(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
    }

    @Override
    protected void writePayload(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
    }

    public static class Handler implements IMessageHandler<AssembleRocketPacket, IMessage> {
        @Override
        public IMessage onMessage(AssembleRocketPacket message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            if (player == null)
                return null;
            World world = player.worldObj;
            if (world == null)
                return null;
            if (!(world.getTileEntity(message.x, message.y, message.z) instanceof RocketAssemblerTileEntity))
                return null;
            RocketAssemblerTileEntity tile = (RocketAssemblerTileEntity) world.getTileEntity(message.x, message.y,
                    message.z);
            if (tile.getBlueprint() == null)
                return null;
            if (tile.getStackInSlot(9) != null)
                return null;
            List<String> partIds = tile.getPartIds();
            if (partIds.isEmpty())
                return null;
            for (int i = 0; i < 9; i++) {
                if (tile.getStackInSlot(i) != null)
                    tile.decrStackSize(i, 1);
            }
            ItemStack rocket = new ItemStack(ItemRegistry.itemRocket, 1, 0);
            ItemRocket.setPartIds(rocket, partIds);
            tile.setInventorySlotContents(9, rocket);
            return null;
        }
    }
}
