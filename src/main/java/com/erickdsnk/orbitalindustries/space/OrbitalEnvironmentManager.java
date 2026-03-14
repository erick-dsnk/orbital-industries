package com.erickdsnk.orbitalindustries.space;

import net.minecraft.world.World;

/**
 * Coordinates orbital/space environment: which dimension is "space" vs "planet", current zone, etc.
 *
 * TODO: Current zone (orbit vs surface) for gameplay and rendering.
 * TODO: Radiation and temperature per zone; data-driven from Planet or config.
 */
public interface OrbitalEnvironmentManager {

    /**
     * Whether the given dimension is considered "space" (e.g. orbit) rather than a planet surface.
     */
    boolean isSpaceDimension(int dimensionId);

    /**
     * Whether the given world is currently in a space (orbital) environment.
     */
    boolean isInSpace(World world);
}
