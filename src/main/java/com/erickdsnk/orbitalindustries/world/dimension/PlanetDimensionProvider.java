package com.erickdsnk.orbitalindustries.world.dimension;

import net.minecraft.world.chunk.IChunkProvider;

import com.erickdsnk.orbitalindustries.OrbitalIndustriesAPI;
import com.erickdsnk.orbitalindustries.dimension.SpaceWorldProvider;
import com.erickdsnk.orbitalindustries.planet.AtmosphereType;
import com.erickdsnk.orbitalindustries.planet.Planet;
import com.erickdsnk.orbitalindustries.world.gen.PlanetChunkProvider;

/**
 * Generic world provider for all data-driven planet dimensions. Looks up the
 * planet by dimension ID and delegates chunk generation to
 * {@link Planet#createChunkProvider(net.minecraft.world.World)}. One provider
 * class is used for
 * every planet loaded from JSON (Moon, future Mars, Europa, etc.).
 */
public class PlanetDimensionProvider extends SpaceWorldProvider {

    @Override
    public String getDimensionName() {
        Planet planet = getPlanet();
        return planet != null ? planet.getName() : "Planet";
    }

    @Override
    public IChunkProvider createChunkGenerator() {
        Planet planet = getPlanet();
        if (planet == null || planet.getTerrainGenerator() == null) {
            Planet empty = new Planet("unknown", "Unknown", dimensionId, null, null, 0.16, AtmosphereType.NONE);
            return new PlanetChunkProvider(worldObj, empty);
        }
        return planet.createChunkProvider(worldObj);
    }

    private Planet getPlanet() {
        if (OrbitalIndustriesAPI.planetRegistry == null) {
            return null;
        }
        return OrbitalIndustriesAPI.planetRegistry.getPlanetByDimension(dimensionId);
    }
}
