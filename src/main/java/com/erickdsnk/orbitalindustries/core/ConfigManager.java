package com.erickdsnk.orbitalindustries.core;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

import com.erickdsnk.orbitalindustries.OrbitalIndustries;

/**
 * Central configuration for the mod. Load/save Forge Configuration and expose typed getters.
 *
 * TODO: Add dimension ID getters when dimensions are configurable.
 * TODO: Add feature flags (e.g. enable vacuum damage, oxygen system) for tuning.
 */
public final class ConfigManager {

    private static Configuration configuration;
    private static String greeting = "Hello World";

    public static void load(File configFile) {
        configuration = new Configuration(configFile);
        greeting = configuration.getString(
                "greeting",
                Configuration.CATEGORY_GENERAL,
                greeting,
                "How shall I greet?");
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
     * TODO: Return dimension IDs from config (e.g. space dimension, moon dimension).
     */
    public static int getSpaceDimensionId() {
        return 2;
    }
}
