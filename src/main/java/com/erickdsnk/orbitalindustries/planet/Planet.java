package com.erickdsnk.orbitalindustries.planet;

import java.util.Collections;
import java.util.List;

import com.erickdsnk.orbitalindustries.planet.biome.PlanetBiome;

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
    private final PlanetTerrainGenerator terrainGenerator;
    private final List<PlanetBiome> biomes;

    public Planet(String id, String name, int dimensionId, double gravity, AtmosphereType atmosphere,
            double orbitalDistance, boolean hasSurface, Planet parent, PlanetTerrainGenerator terrainGenerator) {
        this(id, name, dimensionId, gravity, atmosphere, orbitalDistance, hasSurface, parent, terrainGenerator, null);
    }

    public Planet(String id, String name, int dimensionId, double gravity, AtmosphereType atmosphere,
            double orbitalDistance, boolean hasSurface, Planet parent, PlanetTerrainGenerator terrainGenerator,
            List<PlanetBiome> biomes) {
        this.id = id;
        this.name = name;
        this.dimensionId = dimensionId;
        this.gravity = gravity;
        this.atmosphere = atmosphere;
        this.orbitalDistance = orbitalDistance;
        this.hasSurface = hasSurface;
        this.parent = parent;
        this.terrainGenerator = terrainGenerator;
        this.biomes = biomes == null || biomes.isEmpty()
                ? Collections.<PlanetBiome>emptyList()
                : Collections.unmodifiableList(biomes);
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

    /**
     * Optional terrain generator for this planet's dimension. When non-null, the
     * dimension uses this to generate chunks; when null (e.g. overworld, orbit),
     * no custom terrain is applied.
     */
    public PlanetTerrainGenerator getTerrainGenerator() {
        return terrainGenerator;
    }

    /**
     * Unmodifiable list of biomes for this planet. Empty for planets that do not
     * define biomes (e.g. overworld, orbit). Planets register their biomes when
     * constructed (e.g. Moon with cratered_highlands and smooth_plains).
     */
    public List<PlanetBiome> getBiomes() {
        return biomes;
    }
}
