package com.erickdsnk.orbitalindustries.world.gen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

import com.erickdsnk.orbitalindustries.planet.gen.MoonTerrainGenerator;

/**
 * Chunk provider for the Moon dimension. Generates full terrain from y=0 up to
 * a noise-derived surface, fills with stone and regolith, carves craters, and
 * builds chunks from block arrays. This fully replaces Minecraft's default
 * overworld generator for the Moon so the mod has complete control over
 * terrain.
 * <p>
 * Chunk coordinate math: chunk (chunkX, chunkZ) covers world X in
 * [chunkX*16, chunkX*16+15] and Z in [chunkZ*16, chunkZ*16+15]. Block index in
 * the flat array: index = x + z*16 + y*256 for local x,z in [0,15], y in
 * [0,255]; array length 16*16*256 = 65536.
 * <p>
 * Future compatibility: the same pattern (block array → terrain → carve →
 * Chunk) can be used for Mars, asteroid fields, gas giant moons, and
 * procedural planets by swapping constants and noise.
 */
public class MoonChunkProvider implements IChunkProvider {

    // --- Terrain constants (match MoonTerrainGenerator for consistent look) ---
    private static final int BASE_SURFACE_Y = 64;
    private static final double TERRAIN_NOISE_AMPLITUDE = 32.0;
    private static final int MIN_REGOLITH_LAYERS = 1;
    private static final int MAX_REGOLITH_LAYERS = 3;

    // --- Crater constants ---
    private static final int CRATER_CHANCE_PER_CHUNK = 3;
    private static final int MIN_CRATER_RADIUS = 2;
    private static final int MAX_CRATER_RADIUS = 6;
    private static final int MIN_CRATER_DEPTH = 2;
    private static final int MAX_CRATER_DEPTH = 5;
    private static final int CRATER_RIM_HEIGHT = 1;

    private static final int CHUNK_SIZE_XZ = 16;
    private static final int CHUNK_SIZE_Y = 256;
    private static final int CHUNK_VOLUME = CHUNK_SIZE_XZ * CHUNK_SIZE_XZ * CHUNK_SIZE_Y;

    private final World world;
    private final long worldSeed;

    private static final Block STONE = Blocks.stone;
    private static final Block REGOLITH = Blocks.end_stone;

    public MoonChunkProvider(World world, long worldSeed) {
        this.world = world;
        this.worldSeed = worldSeed;
    }

    @Override
    public Chunk provideChunk(int chunkX, int chunkZ) {
        // Allocate chunk arrays. Index = x + z*16 + y*256 (local x,z 0..15, y 0..255).
        Block[] blocks = new Block[CHUNK_VOLUME];
        byte[] metadata = new byte[CHUNK_VOLUME];
        for (int i = 0; i < CHUNK_VOLUME; i++) {
            blocks[i] = Blocks.air;
        }

        int baseWorldX = chunkX * CHUNK_SIZE_XZ;
        int baseWorldZ = chunkZ * CHUNK_SIZE_XZ;

        // topSurfaceY[localX][localZ] = highest block Y in that column (for crater
        // carving).
        int[][] topSurfaceY = new int[CHUNK_SIZE_XZ][CHUNK_SIZE_XZ];

        // 1) Base terrain + regolith: fill columns from y=0 to surface; air above.
        for (int localX = 0; localX < CHUNK_SIZE_XZ; localX++) {
            for (int localZ = 0; localZ < CHUNK_SIZE_XZ; localZ++) {
                int wx = baseWorldX + localX;
                int wz = baseWorldZ + localZ;
                double noise = MoonTerrainGenerator.terrainNoise(worldSeed, wx, wz);
                int surfaceHeight = BASE_SURFACE_Y
                        + (int) Math.round(noise * TERRAIN_NOISE_AMPLITUDE);
                int regolithLayers = MoonTerrainGenerator.regolithLayersAt(worldSeed, wx, wz);
                if (regolithLayers < MIN_REGOLITH_LAYERS)
                    regolithLayers = MIN_REGOLITH_LAYERS;
                if (regolithLayers > MAX_REGOLITH_LAYERS)
                    regolithLayers = MAX_REGOLITH_LAYERS;

                surfaceHeight = Math.max(regolithLayers, Math.min(255, surfaceHeight));
                int stoneTop = surfaceHeight - regolithLayers;
                if (stoneTop < 0)
                    stoneTop = 0;

                for (int y = 0; y < stoneTop; y++) {
                    setBlock(blocks, metadata, localX, y, localZ, STONE, (byte) 0);
                }
                for (int y = stoneTop; y <= surfaceHeight; y++) {
                    setBlock(blocks, metadata, localX, y, localZ, REGOLITH, (byte) 0);
                }
                topSurfaceY[localX][localZ] = surfaceHeight;
            }
        }

        // 2) Crater carving: 3x3 chunk neighborhood, deterministic from world seed.
        List<CraterInfo> craters = collectCraters(chunkX, chunkZ);
        applyCraters(blocks, metadata, baseWorldX, baseWorldZ, craters, topSurfaceY);

        Chunk chunk = new Chunk(world, blocks, metadata, chunkX, chunkZ);
        chunk.generateSkylightMap();
        return chunk;
    }

    /**
     * Block index for chunk arrays: x + z*16 + y*256 (local coordinates).
     */
    private static int index(int x, int y, int z) {
        return x + (z << 4) + (y << 8);
    }

    private static void setBlock(Block[] blocks, byte[] metadata, int x, int y, int z, Block block, byte meta) {
        int i = index(x, y, z);
        blocks[i] = block;
        metadata[i] = meta;
    }

    /**
     * Collect craters from the 3x3 chunk neighborhood so craters centered in
     * adjacent chunks are carved into this chunk where they overlap.
     */
    private List<CraterInfo> collectCraters(int chunkX, int chunkZ) {
        List<CraterInfo> list = new ArrayList<CraterInfo>();
        for (int dcx = -1; dcx <= 1; dcx++) {
            for (int dcz = -1; dcz <= 1; dcz++) {
                int nc = chunkX + dcx;
                int nz = chunkZ + dcz;
                long seed = worldSeed + (long) nc * 341873128712L + (long) nz * 132897987541L;
                Random rng = new Random(seed);
                int baseNx = nc * CHUNK_SIZE_XZ;
                int baseNz = nz * CHUNK_SIZE_XZ;
                int numCraters = rng.nextInt(CRATER_CHANCE_PER_CHUNK + 1);
                for (int i = 0; i < numCraters; i++) {
                    int cwx = baseNx + rng.nextInt(CHUNK_SIZE_XZ);
                    int cwz = baseNz + rng.nextInt(CHUNK_SIZE_XZ);
                    int radius = MIN_CRATER_RADIUS + rng.nextInt(MAX_CRATER_RADIUS - MIN_CRATER_RADIUS + 1);
                    int maxDepth = MIN_CRATER_DEPTH + rng.nextInt(MAX_CRATER_DEPTH - MIN_CRATER_DEPTH + 1);
                    list.add(new CraterInfo(cwx, cwz, radius, maxDepth));
                }
            }
        }
        return list;
    }

    /**
     * Carve bowl-shaped craters into the block array and add a 1-block rim.
     * Uses distSq for inside/rim check; sqrt only when computing bowl depth.
     */
    private void applyCraters(Block[] blocks, byte[] metadata, int baseWorldX, int baseWorldZ,
            List<CraterInfo> craters, int[][] topSurfaceY) {
        for (int localX = 0; localX < CHUNK_SIZE_XZ; localX++) {
            for (int localZ = 0; localZ < CHUNK_SIZE_XZ; localZ++) {
                int wx = baseWorldX + localX;
                int wz = baseWorldZ + localZ;
                int surfaceY = topSurfaceY[localX][localZ];

                int maxDepth = 0;
                boolean inRim = false;
                for (CraterInfo c : craters) {
                    double dx = wx - c.worldX;
                    double dz = wz - c.worldZ;
                    double distSq = dx * dx + dz * dz;
                    double radiusSq = (double) c.radius * c.radius;
                    if (distSq <= radiusSq) {
                        double dist = Math.sqrt(distSq);
                        double t = dist / Math.max(1, c.radius);
                        int depth = (int) (c.maxDepth * (1.0 - t * t) + 0.5);
                        if (depth > maxDepth)
                            maxDepth = depth;
                    } else if (CRATER_RIM_HEIGHT > 0) {
                        double rimRadiusSq = (double) (c.radius + 1) * (c.radius + 1);
                        if (distSq <= rimRadiusSq)
                            inRim = true;
                    }
                }
                maxDepth = Math.min(maxDepth, surfaceY + 1);
                for (int d = 0; d < maxDepth; d++) {
                    int y = surfaceY - d;
                    if (y >= 0) {
                        int i = index(localX, y, localZ);
                        blocks[i] = Blocks.air;
                        metadata[i] = 0;
                    }
                }
                if (inRim && maxDepth == 0) {
                    int rimY = surfaceY + CRATER_RIM_HEIGHT;
                    if (rimY < CHUNK_SIZE_Y) {
                        setBlock(blocks, metadata, localX, rimY, localZ, REGOLITH, (byte) 0);
                    }
                }
            }
        }
    }

    private static final class CraterInfo {
        final int worldX, worldZ, radius, maxDepth;

        CraterInfo(int worldX, int worldZ, int radius, int maxDepth) {
            this.worldX = worldX;
            this.worldZ = worldZ;
            this.radius = radius;
            this.maxDepth = maxDepth;
        }
    }

    @Override
    public void populate(IChunkProvider chunkProvider, int chunkX, int chunkZ) {
        // Empty for now. Later: ore deposits, abandoned structures, crashed probes,
        // asteroid fragments.
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
        return "MoonChunkProvider";
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
