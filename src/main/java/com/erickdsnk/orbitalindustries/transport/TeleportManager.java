package com.erickdsnk.orbitalindustries.transport;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import com.erickdsnk.orbitalindustries.util.PositionUtils;

import cpw.mods.fml.common.FMLCommonHandler;

/**
 * Handles teleportation between dimensions (e.g. orbit to planet surface).
 * Target validation and position calculation use PositionUtils.findSafeSpawnY.
 * Later will integrate with DimensionRegistry and PlanetManager for valid
 * targets
 * (planets, moons, orbital stations) and space travel.
 */
public final class TeleportManager {

    /**
     * Teleport a player to the given dimension at (x, y, z). Only EntityPlayerMP
     * is supported; other entities are no-op. Uses Forge transfer then sets
     * position so no portal is required. Later will support planets, moons,
     * orbital stations via target resolution and safe spawn.
     */
    public void teleportToDimension(Entity entity, int dimensionId, double x, double y, double z) {
        if (entity == null || !(entity instanceof EntityPlayerMP))
            return;
        EntityPlayerMP player = (EntityPlayerMP) entity;
        net.minecraft.server.MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null)
            return;
        WorldServer toWorld = server.worldServerForDimension(dimensionId);
        if (toWorld == null)
            return;

        server.getConfigurationManager().transferPlayerToDimension(player, dimensionId);
        player.setPositionAndUpdate(x, y, z);
    }

    /**
     * Find safe spawn in target world and teleport entity there.
     */
    public void teleportToDimension(Entity entity, World targetWorld, int x, int z) {
        if (entity == null || targetWorld == null)
            return;
        int y = PositionUtils.findSafeSpawnY(targetWorld, x, z);
        teleportToDimension(entity, targetWorld.provider.dimensionId, x, y, z);
    }
}
