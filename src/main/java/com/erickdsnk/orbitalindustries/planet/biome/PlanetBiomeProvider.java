package com.erickdsnk.orbitalindustries.planet.biome;

import java.util.Collections;
import java.util.List;

import com.erickdsnk.orbitalindustries.planet.gen.NoiseUtils;

/**
 * Selects a {@link PlanetBiome} at world (x, z) using a simple noise-based
 * scheme. Uses deterministic 2D value noise so biome boundaries are stable for
 * a given world seed. Independent seed offset keeps biome layout separate from
 * terrain height noise.
 * <p>
 * This design will later support Mars canyon systems (biome-driven height and
 * structure hooks), asteroid field variation (biome as asteroid type with
 * different blocks/resources), ice worlds (ice/snow/rock biomes), and gas giant
 * moons (each dimension with its own planet and biome list using the same
 * provider pattern).
 */
public final class PlanetBiomeProvider {

    /** Seed offset so biome noise is independent from terrain/crater noise. */
    private static final long BIOME_NOISE_SEED_OFFSET = 0x5A7E3C1D9B2F4E6AL;

    private static final double BIOME_NOISE_FREQ = 0.012;

    private final long worldSeed;
    private final List<PlanetBiome> biomes;
    private final PlanetBiome singleBiome;

    /**
     * @param worldSeed world seed for deterministic biome placement
     * @param biomes    list of biomes; null or size &lt; 2 is treated as
     *                  single-biome (first or default)
     */
    public PlanetBiomeProvider(long worldSeed, List<PlanetBiome> biomes) {
        this.worldSeed = worldSeed;
        if (biomes == null || biomes.isEmpty()) {
            this.biomes = Collections.<PlanetBiome>emptyList();
            this.singleBiome = null;
        } else if (biomes.size() == 1) {
            this.biomes = Collections.unmodifiableList(biomes);
            this.singleBiome = biomes.get(0);
        } else {
            this.biomes = Collections.unmodifiableList(biomes);
            this.singleBiome = null;
        }
    }

    /**
     * Returns the biome for the given world block column.
     */
    public PlanetBiome getBiomeAt(int x, int z) {
        if (singleBiome != null) {
            return singleBiome;
        }
        if (biomes.isEmpty()) {
            return null;
        }
        double n = NoiseUtils.valueNoise2D(worldSeed + BIOME_NOISE_SEED_OFFSET, x * BIOME_NOISE_FREQ,
                z * BIOME_NOISE_FREQ);
        int index = (int) (n * biomes.size()) % biomes.size();
        if (index < 0) {
            index += biomes.size();
        }
        return biomes.get(index);
    }

    /**
     * Returns a 16×16 grid of biomes for the chunk (indices 0–15 for local x/z).
     * Chunk coordinates are in chunk space (chunkX, chunkZ).
     */
    public PlanetBiome[][] getBiomesForChunk(int chunkX, int chunkZ) {
        PlanetBiome[][] grid = new PlanetBiome[16][16];
        int baseX = chunkX * 16;
        int baseZ = chunkZ * 16;
        for (int lx = 0; lx < 16; lx++) {
            for (int lz = 0; lz < 16; lz++) {
                grid[lx][lz] = getBiomeAt(baseX + lx, baseZ + lz);
            }
        }
        return grid;
    }

}
