package com.erickdsnk.orbitalindustries.util;

import net.minecraft.world.World;

/**
 * Static position and coordinate utilities for dimensions and teleportation.
 *
 * TODO: Used by TeleportManager for target position and safe spawn calculation.
 * TODO: Used by dimension code for spawn point and chunk-related logic.
 */
public final class PositionUtils {

    private PositionUtils() {}

    /** Default spawn height when no safe block is found (e.g. in void). */
    public static final int DEFAULT_SPAWN_HEIGHT = 64;

    /**
     * Converts block position to chunk coordinates (chunk X).
     */
    public static int blockToChunkX(int blockX) {
        return blockX >> 4;
    }

    /**
     * Converts block position to chunk coordinates (chunk Z).
     */
    public static int blockToChunkZ(int blockZ) {
        return blockZ >> 4;
    }

    /**
     * Converts chunk coordinates to block position (minimum X of chunk).
     */
    public static int chunkToBlockX(int chunkX) {
        return chunkX << 4;
    }

    /**
     * Converts chunk coordinates to block position (minimum Z of chunk).
     */
    public static int chunkToBlockZ(int chunkZ) {
        return chunkZ << 4;
    }

    /**
     * Finds a safe spawn Y in the given world at (x, z), or DEFAULT_SPAWN_HEIGHT if none found.
     * Does not modify world; callers should use this as a hint for teleport/spawn.
     *
     * TODO: TeleportManager will use this when sending players to a dimension.
     */
    public static int findSafeSpawnY(World world, int x, int z) {
        if (world == null) return DEFAULT_SPAWN_HEIGHT;
        int maxY = Math.min(world.getActualHeight() - 1, 256);
        for (int y = maxY; y >= 0; y--) {
            if (world.getBlock(x, y, z)
                .getMaterial()
                .blocksMovement()
                && !world.getBlock(x, y + 1, z)
                    .getMaterial()
                    .blocksMovement()
                && !world.getBlock(x, y + 2, z)
                    .getMaterial()
                    .blocksMovement()) {
                return y + 1;
            }
        }
        return DEFAULT_SPAWN_HEIGHT;
    }
}
