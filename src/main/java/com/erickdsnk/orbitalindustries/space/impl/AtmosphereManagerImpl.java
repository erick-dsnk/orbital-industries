package com.erickdsnk.orbitalindustries.space.impl;

import com.erickdsnk.orbitalindustries.planet.AtmosphereType;
import com.erickdsnk.orbitalindustries.planet.Planet;
import com.erickdsnk.orbitalindustries.planet.PlanetManager;
import com.erickdsnk.orbitalindustries.space.AtmosphereManager;

/**
 * Default AtmosphereManager: looks up planet by dimension ID and returns
 * whether
 * atmosphere is breathable (AtmosphereType.BREATHABLE).
 *
 * TODO: Space dimension (no planet) = no atmosphere. Overworld = breathable
 * when not in space.
 */
public final class AtmosphereManagerImpl implements AtmosphereManager {

    private final PlanetManager planetManager;

    public AtmosphereManagerImpl(PlanetManager planetManager) {
        this.planetManager = planetManager;
    }

    @Override
    public boolean hasAtmosphere(int dimensionId) {
        Planet planet = planetManager.getPlanetByDimensionId(dimensionId);
        return planet != null && planet.getAtmosphereType() == AtmosphereType.BREATHABLE;
    }

    @Override
    public boolean hasAtmosphere(Planet planet) {
        return planet != null && planet.getAtmosphereType() == AtmosphereType.BREATHABLE;
    }
}
