package com.erickdsnk.orbitalindustries.dimension;

/**
 * Test "orbit" space dimension. Extends SpaceWorldProvider for black sky, no
 * weather,
 * no clouds, no respawn. Later this can be driven by config or planet data for
 * orbital stations, moons, etc.
 */
public class OrbitWorldProvider extends SpaceWorldProvider {

    @Override
    public String getDimensionName() {
        return "Orbit";
    }
}
