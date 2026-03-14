package com.erickdsnk.orbitalindustries.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import com.erickdsnk.orbitalindustries.OrbitalIndustries;

/**
 * Registers Forge channel and packet types. Single entry point for network registration.
 *
 * TODO: Register concrete packet types (e.g. dimension sync, rocket state) with registerMessage.
 */
public final class PacketHandler {

    public static final SimpleNetworkWrapper CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(OrbitalIndustries.MODID);

    private static int packetId = 0;

    public static void registerPackets() {
        // TODO: CHANNEL.registerMessage(SomePacket.Handler.class, SomePacket.class, packetId++, Side.SERVER);
        // TODO: CHANNEL.registerMessage(SomePacket.Handler.class, SomePacket.class, packetId++, Side.CLIENT);
    }
}
