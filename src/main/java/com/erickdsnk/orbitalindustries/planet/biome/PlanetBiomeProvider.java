package com.erickdsnk.orbitalindustries.planet.biome;

import java.util.Collections;
import java.util.List;

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
        double n = valueNoise2D(worldSeed + BIOME_NOISE_SEED_OFFSET, x * BIOME_NOISE_FREQ, z * BIOME_NOISE_FREQ);
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

    /**
     * Deterministic 2D value noise in [0, 1). Lattice hash with bilinear
     * interpolation for smooth biome boundaries.
     */
    private static double valueNoise2D(long seed, double x, double z) {
        int ix = (int) Math.floor(x);
        int iz = (int) Math.floor(z);
        double fx = x - ix;
        double fz = z - iz;
        double sx = smoothstep(fx);
        double sz = smoothstep(fz);

        double v00 = latticeHash(seed, ix, iz);
        double v10 = latticeHash(seed, ix + 1, iz);
        double v01 = latticeHash(seed, ix, iz + 1);
        double v11 = latticeHash(seed, ix + 1, iz + 1);

        double a = v00 + sx * (v10 - v00);
        double b = v01 + sx * (v11 - v01);
        return a + sz * (b - a);
    }

    private static double smoothstep(double t) {
        t = Math.max(0, Math.min(1, t));
        return t * t * (3.0 - 2.0 * t);
    }

    private static double latticeHash(long seed, int ix, int iz) {
        long h = seed + (long) ix * 374761393L + (long) iz * 668265263L;
        h = (h ^ (h >>> 33)) * 0xff51afd7ed558ccdL;
        h = (h ^ (h >>> 33)) * 0xc4ceb9fe1a85ec53L;
        return ((h ^ (h >>> 33)) & 0x7FFF_FFFFL) / (double) (0x7FFF_FFFFL + 1L);
    }
}
