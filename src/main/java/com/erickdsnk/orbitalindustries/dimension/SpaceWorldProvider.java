package com.erickdsnk.orbitalindustries.dimension;

import net.minecraft.util.Vec3;
import net.minecraft.world.WorldProvider;

/**
 * Base WorldProvider for space dimensions. Uses dimension ID from registration;
 * no hardcoded dimension IDs. Subclasses or a single implementation can be
 * registered per dimension via DimensionRegistry.
 *
 * Sky is black, no weather, no clouds, respawn disabled. Actual gravity scaling
 * is applied by GravityManager in the player tick, not here.
 *
 * TODO: Sky renderer registration for custom space sky (stars, etc.).
 * TODO: Weather suppression (1.7.10 WorldProvider has no direct API; use world
 * flags or events later).
 * TODO: Spawn point and world gen; delegate to SpaceDimensionProvider or
 * config.
 */
public class SpaceWorldProvider extends WorldProvider {

    public SpaceWorldProvider() {
        super();
        // Black/void sky: renderer treats sky as having no celestial body.
        hasNoSky = true;
    }

    @Override
    public String getDimensionName() {
        return "Space";
    }

    @Override
    public Vec3 getFogColor(float celestialAngle, float partialTicks) {
        // Black/dark fog for space.
        return Vec3.createVectorHelper(0.0, 0.0, 0.0);
    }

    @Override
    public float getCloudHeight() {
        // Hide clouds by placing them far above the world (effectively off-screen).
        return Float.MAX_VALUE;
    }

    @Override
    public boolean canRespawnHere() {
        return false;
    }

    @Override
    public boolean isSkyColored() {
        return false;
    }
}
