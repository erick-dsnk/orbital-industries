package com.erickdsnk.orbitalindustries.planet;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * In-memory registry of Planet instances by ID. Register/lookup only.
 *
 * TODO: Load planet definitions from config/JSON later; no hardcoded planet definitions here.
 */
public final class PlanetRegistry {

    private final Map<String, Planet> byId = new LinkedHashMap<String, Planet>();
    private final Map<Integer, Planet> byDimensionId = new LinkedHashMap<Integer, Planet>();

    public void register(Planet planet) {
        if (planet == null) return;
        byId.put(planet.getId(), planet);
        byDimensionId.put(planet.getDimensionId(), planet);
    }

    public Planet getById(String id) {
        return byId.get(id);
    }

    public Planet getByDimensionId(int dimensionId) {
        return byDimensionId.get(dimensionId);
    }

    public Collection<Planet> getAll() {
        return Collections.unmodifiableCollection(byId.values());
    }
}
