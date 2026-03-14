package com.erickdsnk.orbitalindustries.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

/**
 * Abstract base for mod packets. Serialization contract; subclasses implement fromBytes/toBytes.
 *
 * TODO: Concrete packet types (e.g. dimension sync, rocket state) extend this and register with PacketHandler.
 */
public abstract class BasePacket implements IMessage {

    @Override
    public void fromBytes(ByteBuf buf) {
        readPayload(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        writePayload(buf);
    }

    protected abstract void readPayload(ByteBuf buf);

    protected abstract void writePayload(ByteBuf buf);
}
