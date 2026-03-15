package com.erickdsnk.orbitalindustries.dimension;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.DimensionManager;

/**
 * Central registration for all space dimensions. Single place for dimension IDs
 * and provider types; no direct DimensionManager calls elsewhere. Registered
 * dimensions will later be driven by planet/moon/station data.
 */
public final class DimensionRegistry {

    private static final int FIRST_PROVIDER_TYPE_ID = 10;

    private final Map<Integer, Class<? extends WorldProvider>> dimensions = new LinkedHashMap<Integer, Class<? extends WorldProvider>>();
    private final Map<Class<? extends WorldProvider>, Integer> providerTypeIds = new LinkedHashMap<Class<? extends WorldProvider>, Integer>();
    private int nextProviderTypeId = FIRST_PROVIDER_TYPE_ID;

    /**
     * Register a dimension with the given ID and provider class. All space
     * dimensions
     * should be registered through this method. Also registers with Forge
     * DimensionManager.
     * In 1.7.10 we register a provider type per class, then register the dimension
     * with that type.
     */
    public void registerDimension(int dimensionId, Class<? extends WorldProvider> providerClass) {
        if (dimensionId == 0 || providerClass == null)
            return;
        if (dimensions.containsKey(dimensionId))
            return;
        dimensions.put(dimensionId, providerClass);
        Integer typeId = providerTypeIds.get(providerClass);
        if (typeId == null) {
            typeId = Integer.valueOf(nextProviderTypeId++);
            DimensionManager.registerProviderType(typeId.intValue(), providerClass, true);
            providerTypeIds.put(providerClass, typeId);
        }
        if (!DimensionManager.isDimensionRegistered(dimensionId)) {
            DimensionManager.registerDimension(dimensionId, typeId.intValue());
        }
    }

    public Class<? extends WorldProvider> getProviderClass(int dimensionId) {
        return dimensions.get(dimensionId);
    }

    public Map<Integer, Class<? extends WorldProvider>> getRegisteredDimensions() {
        return Collections.unmodifiableMap(dimensions);
    }
}
