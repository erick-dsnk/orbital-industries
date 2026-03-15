package com.erickdsnk.orbitalindustries.planet.gen;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.erickdsnk.orbitalindustries.planet.PlanetTerrainGenerator;
import com.erickdsnk.orbitalindustries.planet.biome.PlanetBiome;

/**
 * Registry of terrain generator factories by string ID. JSON planet configs
 * reference a generator by id (e.g. "moon", "mars"); the loader calls
 * {@link #createGenerator(String, List, Map)} to obtain a per-dimension
 * instance
 * with that dimension's biomes and options.
 */
public final class PlanetTerrainRegistry {

    private static final Map<String, PlanetTerrainGeneratorFactory> BY_ID = new LinkedHashMap<String, PlanetTerrainGeneratorFactory>();

    /** Register a terrain generator factory under the given id. */
    public static void registerFactory(String id, PlanetTerrainGeneratorFactory factory) {
        if (id != null && factory != null) {
            BY_ID.put(id, factory);
        }
    }

    /** Get a factory by id; returns null if not registered. */
    public static PlanetTerrainGeneratorFactory getFactory(String id) {
        return id == null ? null : BY_ID.get(id);
    }

    /**
     * Create a new terrain generator instance for the given id with the
     * dimension's biomes and options. Returns null if the id is not registered.
     */
    public static PlanetTerrainGenerator createGenerator(String id, List<PlanetBiome> biomes,
            Map<String, Object> options) {
        PlanetTerrainGeneratorFactory factory = getFactory(id);
        if (factory == null) {
            return null;
        }
        return factory.create(biomes != null ? biomes : Collections.<PlanetBiome>emptyList(),
                options != null ? options : Collections.<String, Object>emptyMap());
    }

    /** All registered ids (unmodifiable). */
    public static Map<String, PlanetTerrainGeneratorFactory> getFactories() {
        return Collections.unmodifiableMap(new LinkedHashMap<String, PlanetTerrainGeneratorFactory>(BY_ID));
    }
}
