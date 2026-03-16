package com.erickdsnk.orbitalindustries.network;

import com.erickdsnk.orbitalindustries.OrbitalIndustries;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

/**
 * Registers Forge channel and packet types. Single entry point for network
 * registration.
 */
public final class PacketHandler {

    public static final SimpleNetworkWrapper CHANNEL = NetworkRegistry.INSTANCE
            .newSimpleChannel(OrbitalIndustries.MODID);

    private static int packetId = 0;

    public static void registerPackets() {
        CHANNEL.registerMessage(AssembleRocketPacket.Handler.class, AssembleRocketPacket.class, packetId++,
                Side.SERVER);
    }
}
