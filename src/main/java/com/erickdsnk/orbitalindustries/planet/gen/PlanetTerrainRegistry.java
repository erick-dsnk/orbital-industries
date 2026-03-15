package com.erickdsnk.orbitalindustries.planet.gen;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.erickdsnk.orbitalindustries.planet.PlanetTerrainGenerator;

/**
 * Registry of terrain generators by string ID. Used so JSON planet configs can
 * reference a generator by id (e.g. "moon", "mars", "ice_moon"). Register
 * generators before loading planet JSON; then
 * {@link com.erickdsnk.orbitalindustries.planet.PlanetLoader}
 * resolves terrainGeneratorId via getGenerator(id).
 */
public final class PlanetTerrainRegistry {

    private static final Map<String, PlanetTerrainGenerator> BY_ID = new LinkedHashMap<String, PlanetTerrainGenerator>();

    /** Register a terrain generator under the given id. */
    public static void registerGenerator(String id, PlanetTerrainGenerator generator) {
        if (id != null && generator != null) {
            BY_ID.put(id, generator);
        }
    }

    /** Get a generator by id; returns null if not registered. */
    public static PlanetTerrainGenerator getGenerator(String id) {
        return id == null ? null : BY_ID.get(id);
    }

    /** All registered ids (unmodifiable). */
    public static Map<String, PlanetTerrainGenerator> getGenerators() {
        return Collections.unmodifiableMap(new LinkedHashMap<String, PlanetTerrainGenerator>(BY_ID));
    }
}
