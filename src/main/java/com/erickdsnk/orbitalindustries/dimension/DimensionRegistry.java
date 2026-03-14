package com.erickdsnk.orbitalindustries.dimension;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.world.WorldProvider;

/**
 * Central registration for all space dimensions. Single place for dimension IDs
 * and provider types; no direct DimensionManager calls elsewhere.
 *
 * TODO: Data-driven registration (e.g. from Planet or config); register provider
 * types and dimension IDs via DimensionManager.registerDimension(int, Class).
 */
public final class DimensionRegistry {

    private final Map<Integer, Class<? extends WorldProvider>> dimensions = new LinkedHashMap<Integer, Class<? extends WorldProvider>>();

    /**
     * Register a dimension with the given ID and provider class. All space dimensions
     * should be registered through this method.
     */
    public void registerDimension(int dimensionId, Class<? extends WorldProvider> providerClass) {
        if (dimensions.containsKey(dimensionId)) return;
        dimensions.put(dimensionId, providerClass);
        // TODO: Call DimensionManager.registerDimension(dimensionId, providerClass) when
        // adding concrete dimensions; for scaffolding we only store the mapping.
    }

    public Class<? extends WorldProvider> getProviderClass(int dimensionId) {
        return dimensions.get(dimensionId);
    }

    public Map<Integer, Class<? extends WorldProvider>> getRegisteredDimensions() {
        return Collections.unmodifiableMap(dimensions);
    }
}
