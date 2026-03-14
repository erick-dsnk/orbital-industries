package com.erickdsnk.orbitalindustries.space;

import com.erickdsnk.orbitalindustries.planet.Planet;

/**
 * Interface for atmosphere/breathability per dimension or planet. Used by OxygenSystem
 * and VacuumDamageHandler.
 *
 * TODO: Integrate with OxygenSystem (sealed areas override) and VacuumDamageHandler.
 */
public interface AtmosphereManager {

    /**
     * Whether the dimension has breathable atmosphere at large (without sealed structures).
     */
    boolean hasAtmosphere(int dimensionId);

    /**
     * Whether the planet has breathable atmosphere.
     */
    boolean hasAtmosphere(Planet planet);
}
