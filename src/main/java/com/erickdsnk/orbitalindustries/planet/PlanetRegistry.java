package com.erickdsnk.orbitalindustries.planet;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.erickdsnk.orbitalindustries.OrbitalIndustries;

/**
 * In-memory registry of Planet instances by ID. Register/lookup only. Uses a
 * static registry structure (Map by id and by dimension id) so celestial bodies
 * can be registered and queried by other systems. Planets are loaded from JSON
 * via {@link PlanetLoader}; new planets (e.g. Mars, Europa, asteroid belt) can
 * be added by adding a JSON file and registering a terrain generator—no code
 * changes required beyond implementing and registering the generator.
 */
public final class PlanetRegistry {

    private final Map<String, Planet> byId = new LinkedHashMap<String, Planet>();
    private final Map<Integer, Planet> byDimensionId = new LinkedHashMap<Integer, Planet>();

    public void register(Planet planet) {
        if (planet == null)
            return;
        byId.put(planet.getId(), planet);
        byDimensionId.put(planet.getDimensionId(), planet);
        OrbitalIndustries.LOG.info("Planet registered: id={}, name={}, dimensionId={}",
                planet.getId(), planet.getName(), planet.getDimensionId());
    }

    /** Register a planet (alias for register for data-driven API). */
    public void registerPlanet(Planet planet) {
        register(planet);
    }

    public Planet getById(String id) {
        return byId.get(id);
    }

    /** Get planet by id (alias for getById). */
    public Planet getPlanet(String id) {
        return getById(id);
    }

    public Planet getByDimensionId(int dimensionId) {
        return byDimensionId.get(dimensionId);
    }

    /** Get planet by dimension id (alias for getByDimensionId). */
    public Planet getPlanetByDimension(int dimensionId) {
        return getByDimensionId(dimensionId);
    }

    public Collection<Planet> getAll() {
        return Collections.unmodifiableCollection(byId.values());
    }

    /** Get all registered planets (alias for getAll). */
    public Collection<Planet> getPlanets() {
        return getAll();
    }
}
