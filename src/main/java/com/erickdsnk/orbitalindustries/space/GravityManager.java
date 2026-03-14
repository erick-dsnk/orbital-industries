package com.erickdsnk.orbitalindustries.space;

import com.erickdsnk.orbitalindustries.planet.Planet;
import com.erickdsnk.orbitalindustries.planet.PlanetManager;

/**
 * Interface for gravity per dimension/planet. Implementations read from Planet data.
 *
 * TODO: Used for fall damage and entity movement scaling per dimension.
 */
public interface GravityManager {

    /**
     * Gravity multiplier for the dimension (1.0 = Overworld-like).
     */
    double getGravityMultiplier(int dimensionId);

    /**
     * Gravity multiplier for the given planet (1.0 = Earth-like).
     */
    double getGravityMultiplier(Planet planet);
}
