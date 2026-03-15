package com.erickdsnk.orbitalindustries.core;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

/**
 * Central configuration for the mod. Load/save Forge Configuration and expose
 * typed getters.
 *
 * TODO: Add dimension ID getters when dimensions are configurable.
 * TODO: Add feature flags (e.g. enable vacuum damage, oxygen system) for
 * tuning.
 */
public final class ConfigManager {

    private static Configuration configuration;
    private static String greeting = "Hello World";
    private static int orbitDimensionId = 2;
    private static int moonDimensionId = 3;

    public static void load(File configFile) {
        configuration = new Configuration(configFile);
        greeting = configuration.getString("greeting", Configuration.CATEGORY_GENERAL, greeting, "How shall I greet?");
        // Orbit dimension ID; later will support multiple space dimensions (planets,
        // moons, orbital stations).
        orbitDimensionId = configuration.getInt("orbitDimensionId", Configuration.CATEGORY_GENERAL, 2, -256, 256,
                "Dimension ID for the orbit (space) dimension.");
        moonDimensionId = configuration.getInt("moonDimensionId", Configuration.CATEGORY_GENERAL, 3, -256, 256,
                "Dimension ID for the Moon dimension.");
        if (configuration.hasChanged()) {
            configuration.save();
        }
    }

    public static void save() {
        if (configuration != null && configuration.hasChanged()) {
            configuration.save();
        }
    }

    public static String getGreeting() {
        return greeting;
    }

    /**
     * Return dimension IDs from config (e.g. space dimension, moon dimension).
     */
    public static int getSpaceDimensionId() {
        return 2;
    }

    /**
     * Dimension ID for the orbit (test space) dimension. Used by DimensionRegistry
     * and /orbit command.
     */
    public static int getOrbitDimensionId() {
        return orbitDimensionId;
    }

    /**
     * Dimension ID for the Moon. Used by DimensionRegistry and /moon command.
     */
    public static int getMoonDimensionId() {
        return moonDimensionId;
    }
}
