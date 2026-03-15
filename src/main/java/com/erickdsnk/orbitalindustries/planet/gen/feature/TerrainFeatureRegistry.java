package com.erickdsnk.orbitalindustries.planet.gen.feature;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Registry of terrain features by string id. Used by ModularTerrainGenerator to
 * look up features by id from generator options (e.g. "craters", "caves").
 */
public final class TerrainFeatureRegistry {

    private static final Map<String, TerrainFeature> BY_ID = new LinkedHashMap<String, TerrainFeature>();

    /** Register a terrain feature under the given id. */
    public static void register(String id, TerrainFeature feature) {
        if (id != null && feature != null) {
            BY_ID.put(id, feature);
        }
    }

    /** Get a feature by id; returns null if not registered. */
    public static TerrainFeature get(String id) {
        return id == null ? null : BY_ID.get(id);
    }

    /** All registered ids (unmodifiable). */
    public static Map<String, TerrainFeature> getAll() {
        return Collections.unmodifiableMap(new LinkedHashMap<String, TerrainFeature>(BY_ID));
    }
}
