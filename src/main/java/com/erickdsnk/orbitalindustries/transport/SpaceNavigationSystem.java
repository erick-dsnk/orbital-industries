package com.erickdsnk.orbitalindustries.transport;

import java.util.ArrayList;
import java.util.List;

import com.erickdsnk.orbitalindustries.planet.Planet;
import com.erickdsnk.orbitalindustries.planet.PlanetRegistry;
import com.erickdsnk.orbitalindustries.rocket.RocketStats;

/**
 * Determines which planets a rocket can reach from its stats, and provides fuel
 * costs. Dimension transfers are handled by {@link TeleportManager}; this class
 * only performs requirement and fuel checks.
 */
public final class SpaceNavigationSystem {

    private final PlanetRegistry planetRegistry;

    public SpaceNavigationSystem(PlanetRegistry planetRegistry) {
        this.planetRegistry = planetRegistry != null ? planetRegistry : null;
    }

    /**
     * Returns all planets that the given rocket stats can reach: navigation tier
     * meets the planet's requirement and max range (fuel budget) meets the
     * planet's fuel cost. Excludes dimension 0 (overworld) if desired; currently
     * includes all registered planets that pass the checks.
     */
    public List<Planet> getReachablePlanets(RocketStats stats) {
        if (planetRegistry == null || stats == null) {
            return new ArrayList<Planet>();
        }
        List<Planet> result = new ArrayList<Planet>();
        for (Planet planet : planetRegistry.getAll()) {
            if (planet.getDimensionId() == 0) {
                continue; // Earth/overworld is not a "destination" from orbit
            }
            if (canReach(stats, planet)) {
                result.add(planet);
            }
        }
        return result;
    }

    /** Fuel cost to travel to the given planet. */
    public double getFuelCostFor(Planet planet) {
        return planet != null ? planet.getFuelCost() : 0.0;
    }

    /**
     * True if the rocket has sufficient navigation tier and max range (fuel
     * budget) to reach the planet.
     */
    public boolean canReach(RocketStats stats, Planet planet) {
        if (stats == null || planet == null) {
            return false;
        }
        return stats.getNavigationTier() >= planet.getRequiredNavigationTier()
                && stats.getMaxRange() >= planet.getFuelCost();
    }
}
