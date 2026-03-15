package com.erickdsnk.orbitalindustries.dimension;

import java.util.Collections;
import java.util.List;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

import com.erickdsnk.orbitalindustries.core.OIModLogger;
import com.erickdsnk.orbitalindustries.planet.Planet;
import com.erickdsnk.orbitalindustries.planet.PlanetManager;
import com.erickdsnk.orbitalindustries.planet.PlanetTerrainGenerator;

/**
 * Chunk provider that delegates terrain generation to the current world's
 * planet
 * via {@link PlanetTerrainGenerator}. Uses
 * {@link PlanetManager#getCurrentPlanet(World)}
 * to resolve the planet; if the planet has no generator, returns empty (air)
 * chunks.
 */
public class PlanetChunkProvider implements IChunkProvider {

    private static final OIModLogger LOG = new OIModLogger("PlanetChunkProvider");

    private final World world;
    private final PlanetManager planetManager;

    public PlanetChunkProvider(World world, PlanetManager planetManager) {
        this.world = world;
        this.planetManager = planetManager;
    }

    @Override
    public Chunk provideChunk(int chunkX, int chunkZ) {
        Planet planet = planetManager.getCurrentPlanet(world);
        PlanetTerrainGenerator generator = planet != null ? planet.getTerrainGenerator() : null;

        if (planet == null || generator == null) {
            return createEmptyChunk(chunkX, chunkZ);
        }

        if (!planet.getBiomes().isEmpty()) {
            LOG.debug("Chunk " + chunkX + "," + chunkZ + " generated (planet " + planet.getId() + ", "
                    + planet.getBiomes().size() + " biomes)");
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
        Planet planet = planetManager.getCurrentPlanet(world);
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
        // No structures to recreate
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
        // No extra data to save
    }
}
