package com.erickdsnk.orbitalindustries.planet.gen;

import java.util.List;
import java.util.Map;

import com.erickdsnk.orbitalindustries.planet.PlanetTerrainGenerator;
import com.erickdsnk.orbitalindustries.planet.biome.PlanetBiome;

/**
 * Factory that creates a {@link PlanetTerrainGenerator} instance for a
 * dimension
 * using that dimension's biomes and generator options from JSON.
 */
public interface PlanetTerrainGeneratorFactory {

    /**
     * Create a new terrain generator instance.
     *
     * @param biomes  biomes for this dimension (may be empty; implementation may
     *                use defaults)
     * @param options generator-specific options from JSON (e.g. baseSurfaceY,
     *                craterChancePerChunk)
     * @return a new generator instance
     */
    PlanetTerrainGenerator create(List<PlanetBiome> biomes, Map<String, Object> options);
}
