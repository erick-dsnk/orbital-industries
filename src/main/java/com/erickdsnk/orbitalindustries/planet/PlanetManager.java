package com.erickdsnk.orbitalindustries.planet;

import net.minecraft.world.World;

/**
 * Runtime interface for planet systems. Uses PlanetRegistry and ties into
 * dimension/orbit
 * logic. Gravity is exposed via GravityManager, which reads from Planet data;
 * lookup
 * utilities (getById, getByDimensionId, getCurrentPlanet) support future travel
 * systems.
 *
 * TODO: Integrate with DimensionRegistry and OrbitalEnvironmentManager.
 */
public final class PlanetManager {

    private final PlanetRegistry registry;

    public PlanetManager(PlanetRegistry registry) {
        this.registry = registry;
    }

    public Planet getPlanetByDimensionId(int dimensionId) {
        return registry.getByDimensionId(dimensionId);
    }

    public Planet getPlanetById(String id) {
        return registry.getById(id);
    }

    /** Resolves the planet for the given world from world.provider.dimensionId. */
    public Planet getCurrentPlanet(World world) {
        if (world == null)
            return null;
        return registry.getByDimensionId(world.provider.dimensionId);
    }

    public java.util.Collection<Planet> getAllPlanets() {
        return registry.getAll();
    }
}
