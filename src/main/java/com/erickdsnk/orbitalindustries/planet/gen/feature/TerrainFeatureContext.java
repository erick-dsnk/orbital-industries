package com.erickdsnk.orbitalindustries.planet.gen.feature;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import com.erickdsnk.orbitalindustries.planet.biome.PlanetBiome;
import com.erickdsnk.orbitalindustries.planet.biome.PlanetBiomeProvider;

/**
 * Context passed to terrain features: world, biome provider, dimension-level
 * options, and default blocks. Features use this to read options and to get the
 * biome at (x, z) for per-biome modifiers (e.g. crater probability).
 */
public final class TerrainFeatureContext {

    private final World world;
    private final PlanetBiomeProvider biomeProvider;
    private final List<PlanetBiome> biomes;
    private final Map<String, Object> options;
    private final Block defaultSurfaceBlock;
    private final Block defaultStoneBlock;

    public TerrainFeatureContext(World world, PlanetBiomeProvider biomeProvider,
            List<PlanetBiome> biomes, Map<String, Object> options,
            Block defaultSurfaceBlock, Block defaultStoneBlock) {
        this.world = world;
        this.biomeProvider = biomeProvider;
        this.biomes = biomes == null ? Collections.<PlanetBiome>emptyList() : biomes;
        this.options = options == null ? Collections.<String, Object>emptyMap() : options;
        this.defaultSurfaceBlock = defaultSurfaceBlock;
        this.defaultStoneBlock = defaultStoneBlock;
    }

    public World getWorld() {
        return world;
    }

    public PlanetBiomeProvider getBiomeProvider() {
        return biomeProvider;
    }

    public List<PlanetBiome> getBiomes() {
        return biomes;
    }

    public Map<String, Object> getOptions() {
        return options;
    }

    public Block getDefaultSurfaceBlock() {
        return defaultSurfaceBlock;
    }

    public Block getDefaultStoneBlock() {
        return defaultStoneBlock;
    }

    /** Get biome at world (x, z); returns first biome or null if none. */
    public PlanetBiome getBiomeAt(int worldX, int worldZ) {
        if (biomeProvider != null) {
            PlanetBiome b = biomeProvider.getBiomeAt(worldX, worldZ);
            if (b != null) {
                return b;
            }
        }
        return biomes.isEmpty() ? null : biomes.get(0);
    }

    public int getIntOption(String key, int defaultValue) {
        if (options == null || !options.containsKey(key)) {
            return defaultValue;
        }
        Object v = options.get(key);
        if (v instanceof Number) {
            return ((Number) v).intValue();
        }
        return defaultValue;
    }

    public double getDoubleOption(String key, double defaultValue) {
        if (options == null || !options.containsKey(key)) {
            return defaultValue;
        }
        Object v = options.get(key);
        if (v instanceof Number) {
            return ((Number) v).doubleValue();
        }
        return defaultValue;
    }

    /**
     * Get string option (e.g. block names for features). Returns null if missing or
     * not a string.
     */
    public String getStringOption(String key) {
        if (options == null || !options.containsKey(key)) {
            return null;
        }
        Object v = options.get(key);
        return v != null ? v.toString().trim() : null;
    }
}
