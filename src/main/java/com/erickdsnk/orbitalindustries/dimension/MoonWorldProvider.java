package com.erickdsnk.orbitalindustries.dimension;

import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

import com.erickdsnk.orbitalindustries.OrbitalIndustriesAPI;
import com.erickdsnk.orbitalindustries.core.OIModLogger;

/**
 * World provider for the Moon dimension. Extends SpaceWorldProvider for black
 * sky and no respawn; overrides chunk generator to use PlanetChunkProvider so
 * the Moon planet's MoonTerrainGenerator is used. Gravity is applied by
 * GravityTickHandler from Planet data (0.16 for Moon).
 */
public class MoonWorldProvider extends SpaceWorldProvider {

    private static final OIModLogger LOG = new OIModLogger("MoonWorldProvider");
    private static boolean loggedInit;

    @Override
    public String getDimensionName() {
        return "Moon";
    }

    @Override
    public IChunkProvider createChunkGenerator() {
        if (!loggedInit) {
            LOG.info("Moon dimension initializing (chunk generator created)");
            loggedInit = true;
        }
        World w = worldObj;
        if (w == null) {
            throw new IllegalStateException("World not set on MoonWorldProvider");
        }
        return new PlanetChunkProvider(w, OrbitalIndustriesAPI.planetManager);
    }

    @Override
    public Vec3 getFogColor(float celestialAngle, float partialTicks) {
        return Vec3.createVectorHelper(0.15, 0.15, 0.18);
    }
}
