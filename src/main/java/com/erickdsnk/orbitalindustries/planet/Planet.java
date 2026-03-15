package com.erickdsnk.orbitalindustries.planet;

/**
 * Immutable data object representing a celestial body. No game logic; used by
 * PlanetRegistry,
 * GravityManager, AtmosphereManager, and dimension/transport systems.
 * <p>
 * This model supports: <b>moons</b> (optional parent reference); <b>planetary
 * travel</b> (lookup
 * by id or dimension id); <b>solar systems</b> (parent hierarchy and listing
 * all bodies); and
 * <b>space progression</b> (data-driven registration so new planets can be
 * added without code).
 * TODO: Load from config/JSON later; no hardcoded planet definitions in logic.
 */
public final class Planet {

    private final String id;
    private final String name;
    private final int dimensionId;
    private final double gravity;
    private final AtmosphereType atmosphere;
    private final double orbitalDistance;
    private final boolean hasSurface;
    private final Planet parent;

    public Planet(String id, String name, int dimensionId, double gravity, AtmosphereType atmosphere,
            double orbitalDistance, boolean hasSurface, Planet parent) {
        this.id = id;
        this.name = name;
        this.dimensionId = dimensionId;
        this.gravity = gravity;
        this.atmosphere = atmosphere;
        this.orbitalDistance = orbitalDistance;
        this.hasSurface = hasSurface;
        this.parent = parent;
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

    /**
     * Gravity multiplier (1.0 = Earth-like). Used by GravityManager for
     * fall/movement scaling.
     */
    public double getGravityMultiplier() {
        return gravity;
    }

    public AtmosphereType getAtmosphereType() {
        return atmosphere;
    }

    public double getOrbitalDistance() {
        return orbitalDistance;
    }

    public boolean hasSurface() {
        return hasSurface;
    }

    /** Optional parent planet for moons; null for primary bodies. */
    public Planet getParent() {
        return parent;
    }
}
