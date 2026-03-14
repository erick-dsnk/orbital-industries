package com.erickdsnk.orbitalindustries.space.impl;

import com.erickdsnk.orbitalindustries.planet.Planet;
import com.erickdsnk.orbitalindustries.planet.PlanetManager;
import com.erickdsnk.orbitalindustries.space.GravityManager;

/**
 * Default GravityManager: looks up planet by dimension ID and returns its gravity multiplier.
 *
 * TODO: Fallback when dimension has no planet (e.g. space dimension = zero or low gravity).
 */
public final class GravityManagerImpl implements GravityManager {

    private static final double DEFAULT_GRAVITY = 1.0;

    private final PlanetManager planetManager;

    public GravityManagerImpl(PlanetManager planetManager) {
        this.planetManager = planetManager;
    }

    @Override
    public double getGravityMultiplier(int dimensionId) {
        Planet planet = planetManager.getPlanetByDimensionId(dimensionId);
        return planet != null ? planet.getGravityMultiplier() : DEFAULT_GRAVITY;
    }

    @Override
    public double getGravityMultiplier(Planet planet) {
        return planet != null ? planet.getGravityMultiplier() : DEFAULT_GRAVITY;
    }
}
