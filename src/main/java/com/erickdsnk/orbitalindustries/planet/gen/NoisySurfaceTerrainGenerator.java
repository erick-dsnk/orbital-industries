package com.erickdsnk.orbitalindustries.planet.gen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

import com.erickdsnk.orbitalindustries.planet.PlanetTerrainGenerator;
import com.erickdsnk.orbitalindustries.planet.biome.PlanetBiome;
import com.erickdsnk.orbitalindustries.planet.biome.PlanetBiomeProvider;

/**
 * Base terrain generator: noisy surface height from multi-octave 2D noise plus
 * biome height modifier, stone + surface (regolith) layers from biomes, and
 * biome array. No craters or caves. Used as the base by
 * ModularTerrainGenerator;
 * exposes {@link #getLastTopSurfaceY()} after {@link #generateTerrain} so
 * features can carve the chunk.
 */
public class NoisySurfaceTerrainGenerator implements PlanetTerrainGenerator {

    private static final int DEFAULT_BASE_SURFACE_Y = 64;
    private static final double DEFAULT_TERRAIN_NOISE_AMPLITUDE = 8.0;
    private static final double TERRAIN_NOISE_FREQ = 0.028;
    private static final int MIN_REGOLITH_LAYERS = 1;
    private static final int MAX_REGOLITH_LAYERS = 3;
    private static final double REGOLITH_NOISE_FREQ = 0.04;
    private static final long REGOLITH_NOISE_SEED_OFFSET = 0x9E3779B97F4A7C15L;

    private final List<PlanetBiome> biomes;
    private final int baseSurfaceY;
    private final double terrainNoiseAmplitude;
    private final int minRegolithLayers;
    private final int maxRegolithLayers;

    /** Set during generateTerrain for use by ModularTerrainGenerator. */
    private int[][] lastTopSurfaceY;

    public NoisySurfaceTerrainGenerator(List<PlanetBiome> biomes, Map<String, Object> options) {
        this.biomes = biomes == null || biomes.isEmpty()
                ? Collections.<PlanetBiome>emptyList()
                : Collections.unmodifiableList(new ArrayList<PlanetBiome>(biomes));
        this.baseSurfaceY = getIntOption(options, "baseSurfaceY", DEFAULT_BASE_SURFACE_Y);
        this.terrainNoiseAmplitude = getDoubleOption(options, "terrainNoiseAmplitude", DEFAULT_TERRAIN_NOISE_AMPLITUDE);
        this.minRegolithLayers = getIntOption(options, "minRegolithLayers", MIN_REGOLITH_LAYERS);
        this.maxRegolithLayers = getIntOption(options, "maxRegolithLayers", MAX_REGOLITH_LAYERS);
    }

    private static int getIntOption(Map<String, Object> options, String key, int defaultValue) {
        if (options == null || !options.containsKey(key)) {
            return defaultValue;
        }
        Object v = options.get(key);
        if (v instanceof Number) {
            return ((Number) v).intValue();
        }
        return defaultValue;
    }

    private static double getDoubleOption(Map<String, Object> options, String key, double defaultValue) {
        if (options == null || !options.containsKey(key)) {
            return defaultValue;
        }
        Object v = options.get(key);
        if (v instanceof Number) {
            return ((Number) v).doubleValue();
        }
        return defaultValue;
    }

    /** Terrain height noise in [-1, 1] (multi-octave value noise). */
    private static double terrainNoise(long seed, double wx, double wz) {
        double wide = (NoiseUtils.valueNoise2D(seed, wx * TERRAIN_NOISE_FREQ * 0.35, wz * TERRAIN_NOISE_FREQ * 0.35)
                * 2.0 - 1.0) * 0.55;
        double main = (NoiseUtils.valueNoise2D(seed + 1, wx * TERRAIN_NOISE_FREQ, wz * TERRAIN_NOISE_FREQ) * 2.0 - 1.0)
                * 0.30;
        double detail = (NoiseUtils.valueNoise2D(seed + 2, wx * TERRAIN_NOISE_FREQ * 2.2, wz * TERRAIN_NOISE_FREQ * 2.2)
                * 2.0 - 1.0) * 0.15;
        double n = wide + main + detail;
        return Math.max(-1.0, Math.min(1.0, n));
    }

    /** Surface layer count from smooth noise. */
    private int regolithLayersAt(long seed, double wx, double wz) {
        double n = NoiseUtils.valueNoise2D(seed + REGOLITH_NOISE_SEED_OFFSET, wx * REGOLITH_NOISE_FREQ,
                wz * REGOLITH_NOISE_FREQ);
        int layers = minRegolithLayers + (int) (n * (maxRegolithLayers - minRegolithLayers + 1));
        return Math.max(minRegolithLayers, Math.min(maxRegolithLayers, layers));
    }

    private double getSmoothedHeightModifier(PlanetBiomeProvider provider, int wx, int wz) {
        final int radius = 2;
        double sum = 0.0;
        int count = 0;
        for (int dz = -radius; dz <= radius; dz++) {
            for (int dx = -radius; dx <= radius; dx++) {
                PlanetBiome b = provider.getBiomeAt(wx + dx, wz + dz);
                sum += b != null ? b.getTerrainHeightModifier() : 0.0;
                count++;
            }
        }
        return count > 0 ? sum / count : 0.0;
    }

    @Override
    public void generateTerrain(World world, Chunk chunk, int chunkX, int chunkZ) {
        long seed = world.getSeed();
        int baseWorldX = chunkX * 16;
        int baseWorldZ = chunkZ * 16;
        PlanetBiomeProvider provider = new PlanetBiomeProvider(seed, biomes);

        int[][] topSurfaceY = new int[16][16];
        for (int localX = 0; localX < 16; localX++) {
            for (int localZ = 0; localZ < 16; localZ++) {
                int wx = baseWorldX + localX;
                int wz = baseWorldZ + localZ;
                PlanetBiome biome = provider.getBiomeAt(wx, wz);
                if (biome == null && !biomes.isEmpty()) {
                    biome = biomes.get(0);
                }
                Block stone = biome != null ? biome.getStoneBlock() : getStoneBlock();
                Block surface = biome != null ? biome.getSurfaceBlock() : getSurfaceBlock();
                double heightMod = getSmoothedHeightModifier(provider, wx, wz);
                int columnBaseY = baseSurfaceY
                        + (int) Math.round(terrainNoise(seed, wx, wz) * terrainNoiseAmplitude)
                        + (int) Math.round(heightMod);
                int regolithLayers = regolithLayersAt(seed, wx, wz);
                int topY = columnBaseY + regolithLayers - 1;
                topSurfaceY[localX][localZ] = topY;

                int stoneTop = columnBaseY - regolithLayers;
                for (int y = 0; y < stoneTop; y++) {
                    chunk.func_150807_a(localX, y, localZ, stone, 0);
                }
                for (int y = stoneTop; y <= topY; y++) {
                    chunk.func_150807_a(localX, y, localZ, surface, 0);
                }
            }
        }

        lastTopSurfaceY = topSurfaceY;

        byte[] biomeArray = new byte[256];
        for (int localZ = 0; localZ < 16; localZ++) {
            for (int localX = 0; localX < 16; localX++) {
                PlanetBiome biome = provider.getBiomeAt(baseWorldX + localX, baseWorldZ + localZ);
                int id = (biome != null && biome.getMinecraftBiomeId() >= 0)
                        ? biome.getMinecraftBiomeId()
                        : 0;
                biomeArray[localZ * 16 + localX] = (byte) (id & 0xFF);
            }
        }
        chunk.setBiomeArray(biomeArray);
    }

    /**
     * Returns the top surface Y grid from the last generateTerrain call. Used
     * by ModularTerrainGenerator to pass to features. May be null if never called.
     */
    public int[][] getLastTopSurfaceY() {
        return lastTopSurfaceY;
    }

    @Override
    public void populate(IChunkProvider chunkProvider, World world, int chunkX, int chunkZ) {
        // No-op; structures are handled by PlanetChunkProvider.
    }

    @Override
    public Block getSurfaceBlock() {
        return biomes.isEmpty() ? net.minecraft.init.Blocks.end_stone : biomes.get(0).getSurfaceBlock();
    }

    @Override
    public Block getStoneBlock() {
        return biomes.isEmpty() ? Blocks.stone : biomes.get(0).getStoneBlock();
    }
}
