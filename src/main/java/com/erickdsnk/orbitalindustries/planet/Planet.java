package com.erickdsnk.orbitalindustries.planet;

import java.util.Collections;
import java.util.List;

import net.minecraft.world.World;

import com.erickdsnk.orbitalindustries.planet.biome.PlanetBiome;
import com.erickdsnk.orbitalindustries.world.gen.PlanetChunkProvider;

/**
 * Represents a planet definition loaded from JSON or registered in code. Holds
 * configuration and a resolved {@link PlanetTerrainGenerator}. Used by
 * PlanetRegistry, GravityManager, AtmosphereManager, and dimension/chunk
 * systems.
 * <p>
 * Optional fields (gravity, atmosphere) support per-planet tuning; when absent
 * from JSON, defaults keep existing systems working. New planets can be added
 * via JSON in config/orbitalindustries/planets/ without code changes.
 */
public final class Planet {

    private final String id;
    private final String name;
    private final int dimensionId;
    private final String terrainGeneratorId;
    private final PlanetTerrainGenerator terrainGenerator;
    private final double gravity;
    private final AtmosphereType atmosphere;
    private final double orbitalDistance;
    private final boolean hasSurface;

    /**
     * Build a planet from JSON-loaded or code-registered data. For JSON planets,
     * terrainGenerator is resolved via
     * {@link com.erickdsnk.orbitalindustries.planet.gen.PlanetTerrainRegistry}.
     *
     * @param gravity         default 0.16 if not specified in JSON
     * @param atmosphere      default NONE if not specified
     * @param orbitalDistance optional, default 0.0
     * @param hasSurface      optional, default true when terrainGenerator != null
     */
    public Planet(String id, String name, int dimensionId, String terrainGeneratorId,
            PlanetTerrainGenerator terrainGenerator, double gravity, AtmosphereType atmosphere,
            double orbitalDistance, boolean hasSurface) {
        this.id = id;
        this.name = name;
        this.dimensionId = dimensionId;
        this.terrainGeneratorId = terrainGeneratorId;
        this.terrainGenerator = terrainGenerator;
        this.gravity = gravity;
        this.atmosphere = atmosphere == null ? AtmosphereType.NONE : atmosphere;
        this.orbitalDistance = orbitalDistance;
        this.hasSurface = hasSurface;
    }

    /**
     * Shorthand for planets with no orbital/surface flags (e.g. from JSON).
     * hasSurface is true when terrainGenerator is non-null.
     */
    public Planet(String id, String name, int dimensionId, String terrainGeneratorId,
            PlanetTerrainGenerator terrainGenerator, double gravity, AtmosphereType atmosphere) {
        this(id, name, dimensionId, terrainGeneratorId, terrainGenerator, gravity, atmosphere, 0.0,
                terrainGenerator != null);
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

    public String getTerrainGeneratorId() {
        return terrainGeneratorId;
    }

    public PlanetTerrainGenerator getTerrainGenerator() {
        return terrainGenerator;
    }

    /**
     * Create the chunk provider for this planet's dimension. Used by
     * PlanetDimensionProvider when creating the chunk generator.
     */
    public PlanetChunkProvider createChunkProvider(World world) {
        return new PlanetChunkProvider(world, this);
    }

    /** Gravity multiplier (1.0 = Earth-like). Used by GravityManager. */
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
        return null;
    }

    /**
     * Unmodifiable list of biomes; empty for JSON-backed planets (generator may
     * define its own).
     */
    public List<PlanetBiome> getBiomes() {
        return Collections.emptyList();
    }
}
