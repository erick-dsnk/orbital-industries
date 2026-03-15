package com.erickdsnk.orbitalindustries.planet.gen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

import com.erickdsnk.orbitalindustries.planet.PlanetTerrainGenerator;
import com.erickdsnk.orbitalindustries.planet.biome.PlanetBiome;
import com.erickdsnk.orbitalindustries.planet.biome.PlanetBiomeProvider;
import com.erickdsnk.orbitalindustries.planet.gen.feature.TerrainFeature;
import com.erickdsnk.orbitalindustries.planet.gen.feature.TerrainFeatureContext;
import com.erickdsnk.orbitalindustries.planet.gen.feature.TerrainFeatureRegistry;

/**
 * Composes a base terrain generator (e.g. NoisySurfaceTerrainGenerator) with
 * an ordered list of terrain features (craters, caves). Runs base generation
 * first, then applies each feature. Used for dimensions that specify
 * "noisy_surface" or "moon" with a feature list in generator options.
 */
public final class ModularTerrainGenerator implements PlanetTerrainGenerator {

    private final NoisySurfaceTerrainGenerator base;
    private final List<String> featureIds;
    private final List<PlanetBiome> biomes;
    private final Map<String, Object> options;

    public ModularTerrainGenerator(NoisySurfaceTerrainGenerator base, List<String> featureIds,
            List<PlanetBiome> biomes, Map<String, Object> options) {
        this.base = base;
        this.featureIds = featureIds == null || featureIds.isEmpty()
                ? Collections.<String>emptyList()
                : featureIds;
        this.biomes = biomes == null ? Collections.<PlanetBiome>emptyList() : biomes;
        this.options = options == null ? Collections.<String, Object>emptyMap() : options;
    }

    @Override
    public void generateTerrain(World world, Chunk chunk, int chunkX, int chunkZ) {
        base.generateTerrain(world, chunk, chunkX, chunkZ);
        int[][] topSurfaceY = base.getLastTopSurfaceY();
        if (topSurfaceY == null || featureIds.isEmpty()) {
            return;
        }
        long seed = world.getSeed();
        int baseWorldX = chunkX * 16;
        int baseWorldZ = chunkZ * 16;
        PlanetBiomeProvider provider = new PlanetBiomeProvider(seed, biomes);
        TerrainFeatureContext context = new TerrainFeatureContext(world, provider, biomes, options,
                base.getSurfaceBlock(), base.getStoneBlock());
        for (String featureId : featureIds) {
            TerrainFeature feature = TerrainFeatureRegistry.get(featureId);
            if (feature != null) {
                feature.apply(chunk, chunkX, chunkZ, baseWorldX, baseWorldZ, seed, topSurfaceY, context);
            }
        }
    }

    @Override
    public void populate(IChunkProvider chunkProvider, World world, int chunkX, int chunkZ) {
        base.populate(chunkProvider, world, chunkX, chunkZ);
    }

    @Override
    public Block getSurfaceBlock() {
        return base.getSurfaceBlock();
    }

    @Override
    public Block getStoneBlock() {
        return base.getStoneBlock();
    }

    /**
     * Parse a "features" option from generator options (JSON array of strings).
     * Returns an empty list if missing or invalid.
     */
    public static List<String> parseFeatureIds(Map<String, Object> options) {
        if (options == null || !options.containsKey("features")) {
            return Collections.emptyList();
        }
        Object raw = options.get("features");
        if (!(raw instanceof List)) {
            return Collections.emptyList();
        }
        List<?> list = (List<?>) raw;
        List<String> out = new ArrayList<String>(list.size());
        for (Object item : list) {
            if (item != null && item instanceof String) {
                String id = (String) item;
                if (!id.isEmpty()) {
                    out.add(id);
                }
            }
        }
        return out;
    }
}
