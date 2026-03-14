package com.erickdsnk.orbitalindustries.planet;

/**
 * Data object representing a celestial body. No game logic; used by PlanetRegistry
 * and other systems (GravityManager, AtmosphereManager, dimension registration).
 *
 * TODO: Load from config/JSON later; no hardcoded planet definitions in logic.
 */
public final class Planet {

    private final String id;
    private final String name;
    private final int dimensionId;
    private final double gravityMultiplier;
    private final boolean hasAtmosphere;
    private final double orbitRadius;

    public Planet(String id, String name, int dimensionId, double gravityMultiplier, boolean hasAtmosphere,
        double orbitRadius) {
        this.id = id;
        this.name = name;
        this.dimensionId = dimensionId;
        this.gravityMultiplier = gravityMultiplier;
        this.hasAtmosphere = hasAtmosphere;
        this.orbitRadius = orbitRadius;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getDimensionId() {
        return dimensionId;
    }

    public double getGravityMultiplier() {
        return gravityMultiplier;
    }

    public boolean hasAtmosphere() {
        return hasAtmosphere;
    }

    public double getOrbitRadius() {
        return orbitRadius;
    }
}
