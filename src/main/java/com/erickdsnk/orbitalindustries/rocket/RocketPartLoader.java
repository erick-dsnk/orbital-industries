package com.erickdsnk.orbitalindustries.rocket;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import com.erickdsnk.orbitalindustries.core.OIModLogger;
import com.erickdsnk.orbitalindustries.rocket.part.DataDrivenRocketPart;

import cpw.mods.fml.common.Loader;

/**
 * Loads rocket part definitions from JSON. Defaults from
 * {@code orbitalindustries/rocket_parts/*.json}; overrides/adds from
 * {@code config/orbitalindustries/rocket_parts/*.json}. Registers parts with
 * {@link RocketPartRegistry}.
 */
public final class RocketPartLoader {

    private static final OIModLogger LOG = new OIModLogger("RocketPartLoader");
    private static final String ROCKET_PARTS_RESOURCE_PATH = "orbitalindustries/rocket_parts/";
    private static final String[] DEFAULT_PART_RESOURCES = {
            "basic_engine.json",
            "basic_fuel_tank.json",
            "basic_guidance.json"
    };
    private static final Gson GSON = new Gson();

    /**
     * Load part configs from resources then config directory, then register each
     * with the given registry.
     */
    public static void loadParts(RocketPartRegistry registry) {
        if (registry == null) {
            LOG.warn("RocketPartRegistry is null; skipping rocket part load");
            return;
        }
        Map<String, PartConfig> byId = new LinkedHashMap<String, PartConfig>();

        for (String name : DEFAULT_PART_RESOURCES) {
            String path = ROCKET_PARTS_RESOURCE_PATH + name;
            InputStream stream = RocketPartLoader.class.getClassLoader().getResourceAsStream(path);
            if (stream == null) {
                LOG.warn("Default rocket part resource not found: " + path);
                continue;
            }
            try {
                Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
                PartConfig config = GSON.fromJson(reader, PartConfig.class);
                reader.close();
                if (config != null && config.id != null && !config.id.isEmpty()) {
                    byId.put(config.id, config);
                    LOG.info("Loaded default rocket part from resources: " + config.id);
                }
            } catch (JsonSyntaxException e) {
                LOG.warn("Invalid JSON in resource " + path + ": " + e.getMessage());
            } catch (IOException e) {
                LOG.warn("Could not read resource " + path + ": " + e.getMessage());
            }
        }

        File configDir = Loader.instance().getConfigDir();
        File partsDir = new File(new File(configDir, "orbitalindustries"), "rocket_parts");
        if (partsDir.exists()) {
            File[] files = partsDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (!file.getName().toLowerCase().endsWith(".json"))
                        continue;
                    try {
                        PartConfig config = GSON.fromJson(new FileReader(file), PartConfig.class);
                        if (config != null && config.id != null && !config.id.isEmpty()) {
                            byId.put(config.id, config);
                            LOG.info("Loaded rocket part from config: " + file.getName() + " (id=" + config.id + ")");
                        }
                    } catch (JsonSyntaxException e) {
                        LOG.warn("Invalid JSON in " + file.getName() + ": " + e.getMessage());
                    } catch (IOException e) {
                        LOG.warn("Could not read " + file.getName() + ": " + e.getMessage());
                    }
                }
            }
        }

        for (PartConfig config : byId.values()) {
            try {
                RocketPartType type = parseType(config.type);
                double thrust = config.thrust != null ? config.thrust.doubleValue() : 0.0;
                double fuelCapacity = config.fuelCapacity != null ? config.fuelCapacity.doubleValue() : 0.0;
                double mass = config.mass != null ? config.mass.doubleValue() : 0.0;
                int navigationTier = config.navigationTier != null ? config.navigationTier.intValue() : 0;
                double maxRange = config.maxRange != null ? config.maxRange.doubleValue() : 0.0;
                DataDrivenRocketPart part = new DataDrivenRocketPart(
                        config.id, type, thrust, fuelCapacity, mass, navigationTier, maxRange);
                registry.register(part, config.id);
            } catch (Exception e) {
                LOG.warn("Failed to register rocket part " + config.id + ": " + e.getMessage());
            }
        }
    }

    private static RocketPartType parseType(String s) {
        if (s == null || s.isEmpty())
            return RocketPartType.HULL;
        try {
            return RocketPartType.valueOf(s.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return RocketPartType.HULL;
        }
    }

    @SuppressWarnings("unused")
    private static class PartConfig {
        String id;
        String type;
        Double thrust;
        Double fuelCapacity;
        Double mass;
        Integer navigationTier;
        Double maxRange;
    }
}
