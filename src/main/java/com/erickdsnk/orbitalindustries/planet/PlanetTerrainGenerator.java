package com.erickdsnk.orbitalindustries.planet;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

/**
 * Interface for planet-specific terrain generation. Each celestial body can
 * define its own world generation logic so new planets and moons can be added
 * easily without changing core dimension code.
 * <p>
 * Implementations fill chunks with terrain in {@link #generateTerrain} and
 * optionally add ores/structures in {@link #populate}. Block types for
 * surface and subsurface are exposed via {@link #getSurfaceBlock()} and
 * {@link #getStoneBlock()}.
 * <p>
 * This design will later support Mars, asteroid belts, gas giants, procedural
 * planets, and different biome systems. Java 8 only; no external libraries.
 */
public interface PlanetTerrainGenerator {

    /**
     * Generate terrain for the given chunk. The implementation should fill the
     * chunk using the chunk's block-setting API (e.g. setBlock with local
     * coordinates 0–15 for x/z).
     *
     * @param world  the world
     * @param chunk  the chunk to fill (local coords: x,z 0–15; y 0–255)
     * @param chunkX chunk X coordinate
     * @param chunkZ chunk Z coordinate
     */
    void generateTerrain(World world, Chunk chunk, int chunkX, int chunkZ);

    /**
     * Populate the chunk with ores, structures, or other features. Called after
     * terrain generation. May be a no-op (e.g. for barren moons).
     *
     * @param chunkProvider the chunk provider (for loading adjacent chunks if
     *                      needed)
     * @param world         the world
     * @param chunkX        chunk X coordinate
     * @param chunkZ        chunk Z coordinate
     */
    void populate(IChunkProvider chunkProvider, World world, int chunkX, int chunkZ);

    /**
     * Block used for the surface layer (e.g. regolith, grass, sand).
     */
    Block getSurfaceBlock();

    /**
     * Block used for the stone/subsurface layer below the surface.
     */
    Block getStoneBlock();
}
