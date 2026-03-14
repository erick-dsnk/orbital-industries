package com.erickdsnk.orbitalindustries.planet;

import net.minecraft.world.World;

/**
 * Facade that uses PlanetRegistry and ties into dimension/orbit logic.
 *
 * TODO: getCurrentPlanet(World) - resolve current planet from dimension ID.
 * TODO: listPlanets() - expose for UI or travel selection.
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

    /**
     * TODO: Return current planet for the given world (from world.provider.dimensionId).
     */
    public Planet getCurrentPlanet(World world) {
        if (world == null) return null;
        return registry.getByDimensionId(world.provider.dimensionId);
    }

    public java.util.Collection<Planet> getAllPlanets() {
        return registry.getAll();
    }
}
