package com.erickdsnk.orbitalindustries.environment;

import net.minecraft.world.World;

import com.erickdsnk.orbitalindustries.planet.AtmosphereType;
import com.erickdsnk.orbitalindustries.planet.Planet;
import com.erickdsnk.orbitalindustries.planet.PlanetManager;

/**
 * Central query system for environmental conditions per world. Used by vacuum
 * damage, oxygen, and future mechanics (pressure, temperature, radiation).
 * Determines conditions from the current planet via PlanetManager; unregistered
 * dimensions are treated as vacuum.
 */
public final class EnvironmentManager {

    private final PlanetManager planetManager;

    public EnvironmentManager(PlanetManager planetManager) {
        this.planetManager = planetManager;
    }

    /** Resolves the planet for the given world from its dimension. */
    public Planet getCurrentPlanet(World world) {
        return planetManager == null ? null : planetManager.getCurrentPlanet(world);
    }

    /**
     * Atmosphere type for the world; NONE if no planet is registered for the
     * dimension.
     */
    public AtmosphereType getAtmosphereType(World world) {
        if (world == null)
            return AtmosphereType.NONE;
        Planet planet = getCurrentPlanet(world);
        return planet == null ? AtmosphereType.NONE : planet.getAtmosphereType();
    }

    /** True when the world has some atmosphere (not vacuum). */
    public boolean hasAtmosphere(World world) {
        return getAtmosphereType(world) != AtmosphereType.NONE;
    }

    /** True when the world is vacuum (no atmosphere). */
    public boolean isVacuum(World world) {
        return getAtmosphereType(world) == AtmosphereType.NONE;
    }
}
