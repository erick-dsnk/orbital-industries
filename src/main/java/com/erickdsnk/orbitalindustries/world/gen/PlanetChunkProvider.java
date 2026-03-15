package com.erickdsnk.orbitalindustries.world.gen;

import java.util.Collections;
import java.util.List;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

import com.erickdsnk.orbitalindustries.planet.Planet;
import com.erickdsnk.orbitalindustries.planet.PlanetTerrainGenerator;

/**
 * Chunk provider for planet dimensions. Holds a reference to the planet and
 * delegates terrain generation to {@link Planet#getTerrainGenerator()}. Used by
 * {@link com.erickdsnk.orbitalindustries.world.dimension.PlanetDimensionProvider}
 * via planet.createChunkProvider(world).
 */
public class PlanetChunkProvider implements IChunkProvider {

    private final World world;
    private final Planet planet;

    public PlanetChunkProvider(World world, Planet planet) {
        this.world = world;
        this.planet = planet;
    }

    @Override
    public Chunk provideChunk(int chunkX, int chunkZ) {
        PlanetTerrainGenerator generator = planet != null ? planet.getTerrainGenerator() : null;
        if (generator == null) {
            return createEmptyChunk(chunkX, chunkZ);
        }
        Chunk chunk = new Chunk(world, chunkX, chunkZ);
        generator.generateTerrain(world, chunk, chunkX, chunkZ);
        chunk.generateSkylightMap();
        return chunk;
    }

    private Chunk createEmptyChunk(int chunkX, int chunkZ) {
        Chunk chunk = new Chunk(world, chunkX, chunkZ);
        chunk.generateSkylightMap();
        return chunk;
    }

    @Override
    public void populate(IChunkProvider chunkProvider, int chunkX, int chunkZ) {
        PlanetTerrainGenerator generator = planet != null ? planet.getTerrainGenerator() : null;
        if (generator != null) {
            generator.populate(chunkProvider, world, chunkX, chunkZ);
        }
    }

    @Override
    public boolean chunkExists(int chunkX, int chunkZ) {
        return false;
    }

    @Override
    public Chunk loadChunk(int chunkX, int chunkZ) {
        return provideChunk(chunkX, chunkZ);
    }

    @Override
    public void recreateStructures(int chunkX, int chunkZ) {
        // No structures
    }

    @Override
    public boolean saveChunks(boolean skipExisting, IProgressUpdate progress) {
        return true;
    }

    @Override
    public boolean unloadQueuedChunks() {
        return false;
    }

    @Override
    public boolean canSave() {
        return true;
    }

    @Override
    public String makeString() {
        return "PlanetChunkProvider";
    }

    @Override
    public List<BiomeGenBase.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, int x, int y, int z) {
        return Collections.emptyList();
    }

    @Override
    public ChunkPosition func_147416_a(World world, String structureName, int x, int y, int z) {
        return null;
    }

    @Override
    public int getLoadedChunkCount() {
        return 0;
    }

    @Override
    public void saveExtraData() {
        // No extra data
    }
}
