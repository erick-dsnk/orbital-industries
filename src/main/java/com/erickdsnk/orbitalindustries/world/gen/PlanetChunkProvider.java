package com.erickdsnk.orbitalindustries.world.gen;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

import com.erickdsnk.orbitalindustries.OrbitalIndustriesAPI;
import com.erickdsnk.orbitalindustries.planet.Planet;
import com.erickdsnk.orbitalindustries.planet.structure.StructureEntry;

/**
 * Generic chunk provider for all data-driven planet dimensions. Delegates
 * terrain generation to the planet's
 * {@link com.erickdsnk.orbitalindustries.planet.PlanetTerrainGenerator}
 * and runs structure placement from the planet's structure list during
 * populate.
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
        Chunk chunk = new Chunk(world, chunkX, chunkZ);
        if (planet.getTerrainGenerator() != null) {
            planet.getTerrainGenerator().generateTerrain(world, chunk, chunkX, chunkZ);
        }
        chunk.generateSkylightMap();
        return chunk;
    }

    @Override
    public void populate(IChunkProvider chunkProvider, int chunkX, int chunkZ) {
        if (planet.getTerrainGenerator() != null) {
            planet.getTerrainGenerator().populate(this, world, chunkX, chunkZ);
        }
        List<StructureEntry> structures = planet.getStructures();
        if (structures != null && OrbitalIndustriesAPI.structureRegistry != null) {
            Random rng = new Random(world.getSeed());
            long seed = rng.nextLong() * (long) chunkX + rng.nextLong() * (long) chunkZ ^ world.getSeed();
            rng.setSeed(seed);
            for (StructureEntry entry : structures) {
                if (rng.nextDouble() < entry.getChancePerChunk()) {
                    OrbitalIndustriesAPI.structureRegistry.generate(entry.getType(), world, chunkX, chunkZ, rng, entry);
                }
            }
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
        return "PlanetChunkProvider[" + (planet != null ? planet.getId() : "null") + "]";
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
    }
}
