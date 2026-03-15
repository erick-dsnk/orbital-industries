package com.erickdsnk.orbitalindustries.planet.biome;

import java.util.LinkedHashMap;
import java.util.Map;

import com.erickdsnk.orbitalindustries.core.OIModLogger;

import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

/**
 * Registers planet biomes with Minecraft so the F3 debug screen and other
 * systems show the correct biome name instead of "Ocean". Assigns unique
 * biome IDs and creates {@link PlanetBiomeGenBase} instances.
 */
public final class PlanetBiomeRegistry {

    private static final OIModLogger LOG = new OIModLogger("PlanetBiomeRegistry");

    /** Start after vanilla biomes (0–39) to avoid conflicts. */
    private static final int FIRST_BIOME_ID = 40;

    private final Map<String, Integer> idByKey = new LinkedHashMap<String, Integer>();
    private int nextId = FIRST_BIOME_ID;

    /**
     * Get or register a Minecraft biome for the given planet biome. Returns the
     * biome ID to use in the chunk biome array so F3 shows the display name.
     * Same key returns the same ID on subsequent calls.
     *
     * @param biomeKey    unique key (e.g. planet id + ":" + biome id from JSON)
     * @param displayName name shown in F3 and other UIs
     * @return the Minecraft biome ID (always >= 0)
     */
    public int getOrRegister(String biomeKey, String displayName) {
        if (biomeKey == null || biomeKey.isEmpty()) {
            biomeKey = "unknown";
        }
        if (displayName == null || displayName.isEmpty()) {
            displayName = biomeKey;
        }
        Integer existing = idByKey.get(biomeKey);
        if (existing != null) {
            return existing.intValue();
        }
        if (nextId > 255) {
            LOG.warn("Ran out of planet biome IDs; reusing 40");
            nextId = FIRST_BIOME_ID;
        }
        int id = nextId++;
        PlanetBiomeGenBase biome = new PlanetBiomeGenBase(id, displayName);
        try {
            BiomeDictionary.registerBiomeType(biome, Type.MOUNTAIN, Type.WASTELAND);
        } catch (Throwable t) {
            // BiomeDictionary may not exist or may behave differently in some environments
            LOG.debug("Could not register biome type for " + displayName + ": " + t.getMessage());
        }
        idByKey.put(biomeKey, Integer.valueOf(id));
        LOG.info("Registered planet biome: id=" + id + ", key=" + biomeKey + ", name=" + displayName);
        return id;
    }

    public int getRegisteredCount() {
        return idByKey.size();
    }
}
