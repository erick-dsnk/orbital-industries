package com.erickdsnk.orbitalindustries.planet.gen.feature;

import net.minecraft.world.chunk.Chunk;

/**
 * A pluggable terrain feature that modifies a chunk after base terrain is
 * filled (e.g. craters, caves). Registered by id in TerrainFeatureRegistry and
 * applied in order by ModularTerrainGenerator.
 */
public interface TerrainFeature {

    /**
     * Apply this feature to the chunk. The chunk is already filled with base
     * terrain; topSurfaceY[localX][localZ] gives the surface height for each
     * column.
     *
     * @param chunk       the chunk to modify (local coords 0–15 x/z)
     * @param chunkX      chunk X coordinate
     * @param chunkZ      chunk Z coordinate
     * @param baseWorldX  world X of chunk origin (chunkX * 16)
     * @param baseWorldZ  world Z of chunk origin (chunkZ * 16)
     * @param seed        world seed
     * @param topSurfaceY surface height per column [localX][localZ]
     * @param context     biome provider, options, defaults
     */
    void apply(Chunk chunk, int chunkX, int chunkZ, int baseWorldX, int baseWorldZ,
            long seed, int[][] topSurfaceY, TerrainFeatureContext context);
}
