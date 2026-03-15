package com.erickdsnk.orbitalindustries.dimension;

import net.minecraft.entity.Entity;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

/**
 * Teleporter for space dimensions that does not create or use nether portals.
 * Used when transferring players to orbit/moon so no obsidian portal structure
 * is generated in the destination dimension.
 */
public class SpaceTeleporter extends Teleporter {

    public SpaceTeleporter(WorldServer world) {
        super(world);
    }

    /**
     * Override to avoid creating a nether portal in space dimensions.
     * Vanilla would call placeInExistingPortal then makePortal; we do neither.
     * Caller (TeleportManager) sets final position after transfer.
     */
    @Override
    public void placeInPortal(Entity entity, double x, double y, double z, float rotationYaw) {
        // No-op: do not build or use any portal.
    }
}
