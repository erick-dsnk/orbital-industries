package com.erickdsnk.orbitalindustries.planet.gen.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.chunk.Chunk;

import com.erickdsnk.orbitalindustries.planet.gen.NoiseUtils;

/**
 * Terrain feature that carves caves using 3D value noise. Only replaces stone
 * and surface blocks so other features (e.g. craters) are preserved. Options:
 * caveEnabled (1/0), caveMinY, caveCarveThreshold, caveNoiseFreq.
 */
public final class CaveFeature implements TerrainFeature {

    private static final long CAVE_NOISE_SEED_OFFSET = 0x6C0789657F4A7C15L;
    private static final int DEFAULT_CAVE_MIN_Y = 5;
    private static final double DEFAULT_CAVE_NOISE_FREQ = 0.14;
    private static final double DEFAULT_CAVE_CARVE_THRESHOLD = 0.38;

    @Override
    public void apply(Chunk chunk, int chunkX, int chunkZ, int baseWorldX, int baseWorldZ,
            long seed, int[][] topSurfaceY, TerrainFeatureContext context) {
        if (context.getIntOption("caveEnabled", 1) == 0) {
            return;
        }
        int caveMinY = context.getIntOption("caveMinY", DEFAULT_CAVE_MIN_Y);
        double caveFreq = context.getDoubleOption("caveNoiseFreq", DEFAULT_CAVE_NOISE_FREQ);
        double carveThreshold = context.getDoubleOption("caveCarveThreshold", DEFAULT_CAVE_CARVE_THRESHOLD);
        Block stone = context.getDefaultStoneBlock();
        Block surface = context.getDefaultSurfaceBlock();
        if (stone == null) {
            stone = Blocks.stone;
        }
        if (surface == null) {
            surface = Blocks.end_stone;
        }

        for (int localX = 0; localX < 16; localX++) {
            for (int localZ = 0; localZ < 16; localZ++) {
                int wx = baseWorldX + localX;
                int wz = baseWorldZ + localZ;
                int surfaceY = topSurfaceY[localX][localZ];
                for (int y = caveMinY; y <= surfaceY + 2; y++) {
                    Block block = chunk.getBlock(localX, y, localZ);
                    if (block != stone && block != surface) {
                        continue;
                    }
                    double n = caveNoise(seed, caveFreq, wx, y, wz);
                    if (n < carveThreshold) {
                        chunk.func_150807_a(localX, y, localZ, Blocks.air, 0);
                    }
                }
            }
        }
    }

    /**
     * Cave density noise: multi-octave 3D value noise in [0, 1]. Carve when below
     * threshold.
     */
    private static double caveNoise(long seed, double scale, double wx, double wy, double wz) {
        double large = NoiseUtils.valueNoise3D(seed + CAVE_NOISE_SEED_OFFSET, wx * scale * 0.5, wy * scale * 0.5,
                wz * scale * 0.5);
        double main = NoiseUtils.valueNoise3D(seed + CAVE_NOISE_SEED_OFFSET + 1, wx * scale, wy * scale, wz * scale);
        double detail = NoiseUtils.valueNoise3D(seed + CAVE_NOISE_SEED_OFFSET + 2, wx * scale * 2.0, wy * scale * 2.0,
                wz * scale * 2.0);
        return large * 0.5 + main * 0.35 + detail * 0.15;
    }
}
