package com.erickdsnk.orbitalindustries.planet;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import com.erickdsnk.orbitalindustries.OrbitalIndustriesAPI;
import com.erickdsnk.orbitalindustries.core.OIModLogger;
import com.erickdsnk.orbitalindustries.planet.gen.PlanetTerrainRegistry;

import cpw.mods.fml.common.Loader;

/**
 * Loads planet definitions from JSON files in
 * config/orbitalindustries/planets/.
 * Each JSON file defines one planet; terrain generator is resolved via
 * {@link PlanetTerrainRegistry}. New planets (e.g. Mars, Europa, asteroid belt)
 * can be added by dropping a JSON file and registering a generator—no code
 * changes required beyond implementing and registering the generator.
 */
public final class PlanetLoader {

    private static final OIModLogger LOG = new OIModLogger("PlanetLoader");
    private static final double DEFAULT_GRAVITY = 0.16;
    private static final Gson GSON = new Gson();

    /**
     * Scan config/orbitalindustries/planets/, parse each JSON file, resolve
     * terrain generator, and register planets with {@link PlanetRegistry}.
     * Creates the directory if missing. Skips invalid or duplicate entries.
     */
    public static void loadPlanets() {
        if (OrbitalIndustriesAPI.planetRegistry == null) {
            LOG.warn("PlanetRegistry not initialized; skipping planet load");
            return;
        }
        File configDir = Loader.instance().getConfigDir();
        File planetsDir = new File(new File(configDir, "orbitalindustries"), "planets");
        if (!planetsDir.exists()) {
            if (!planetsDir.mkdirs()) {
                LOG.warn("Could not create planets config dir: " + planetsDir.getAbsolutePath());
                return;
            }
        }
        writeDefaultMoonJsonIfMissing(planetsDir);
        File[] files = planetsDir.listFiles();
        if (files == null) {
            return;
        }
        Set<String> seenIds = new HashSet<String>();
        Set<Integer> seenDimensions = new HashSet<Integer>();
        for (File file : files) {
            if (!file.getName().toLowerCase().endsWith(".json")) {
                continue;
            }
            try {
                PlanetConfig config = GSON.fromJson(new FileReader(file), PlanetConfig.class);
                if (config == null || config.id == null || config.id.isEmpty()) {
                    LOG.warn("Skipping " + file.getName() + ": missing or empty id");
                    continue;
                }
                if (config.name == null || config.name.isEmpty()) {
                    config.name = config.id;
                }
                if (config.terrainGenerator == null || config.terrainGenerator.isEmpty()) {
                    LOG.warn("Skipping " + config.id + ": missing terrainGenerator");
                    continue;
                }
                PlanetTerrainGenerator generator = PlanetTerrainRegistry.getGenerator(config.terrainGenerator);
                if (generator == null) {
                    LOG.warn("Skipping " + config.id + ": unknown terrainGenerator '" + config.terrainGenerator + "'");
                    continue;
                }
                if (seenIds.contains(config.id)) {
                    LOG.warn("Skipping " + file.getName() + ": duplicate planet id " + config.id);
                    continue;
                }
                if (seenDimensions.contains(config.dimensionId)) {
                    LOG.warn("Skipping " + config.id + ": duplicate dimensionId " + config.dimensionId);
                    continue;
                }
                double gravity = config.gravity != null ? config.gravity.doubleValue() : DEFAULT_GRAVITY;
                AtmosphereType atmosphere = parseAtmosphere(config.atmosphere);
                Planet planet = new Planet(config.id, config.name, config.dimensionId, config.terrainGenerator,
                        generator, gravity, atmosphere);
                OrbitalIndustriesAPI.planetRegistry.registerPlanet(planet);
                seenIds.add(config.id);
                seenDimensions.add(config.dimensionId);
                LOG.info("Loaded planet from " + file.getName() + ": id=" + config.id + ", dimensionId="
                        + config.dimensionId);
            } catch (JsonSyntaxException e) {
                LOG.warn("Invalid JSON in " + file.getName() + ": " + e.getMessage());
            } catch (IOException e) {
                LOG.warn("Could not read " + file.getName() + ": " + e.getMessage());
            }
        }
    }

    private static void writeDefaultMoonJsonIfMissing(File planetsDir) {
        File moonJson = new File(planetsDir, "moon.json");
        if (moonJson.exists()) {
            return;
        }
        try {
            FileWriter w = new FileWriter(moonJson);
            w.write("{\n  \"id\": \"moon\",\n  \"name\": \"Moon\",\n  \"dimensionId\": -40,\n  \"terrainGenerator\": \"moon\"\n}\n");
            w.close();
            LOG.info("Created default config/orbitalindustries/planets/moon.json");
        } catch (IOException e) {
            LOG.warn("Could not write default moon.json: " + e.getMessage());
        }
    }

    private static AtmosphereType parseAtmosphere(String s) {
        if (s == null || s.isEmpty()) {
            return AtmosphereType.NONE;
        }
        try {
            return AtmosphereType.valueOf(s.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return AtmosphereType.NONE;
        }
    }

    /** DTO for JSON parsing. */
    @SuppressWarnings("unused")
    private static class PlanetConfig {
        String id;
        String name;
        int dimensionId;
        String terrainGenerator;
        Double gravity;
        String atmosphere;
    }
}
