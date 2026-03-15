package com.erickdsnk.orbitalindustries.planet.gen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

import com.erickdsnk.orbitalindustries.planet.PlanetTerrainGenerator;

/**
 * Simple terrain generator for the Moon: mostly flat terrain with a stone base
 * layer, endstone surface, and basic crater generation using
 * random circles. Minimal implementation to validate the planet terrain
 * architecture.
 * <p>
 * This system will later support Mars, asteroid belts, gas giants, procedural
 * planets, and different biome systems; this class is kept simple for now.
 */
public class MoonTerrainGenerator implements PlanetTerrainGenerator {

    private static final int BASE_SURFACE_Y = 64;
    private static final int STONE_HEIGHT = 62;
    private static final int REGOLITH_LAYERS = 2;
    private static final int CRATER_CHANCE_PER_CHUNK = 3;
    private static final int MAX_CRATER_RADIUS = 6;
    private static final int MIN_CRATER_RADIUS = 2;
    /** Maximum depth (blocks) at crater center; bowl shape so rim is shallow. */
    private static final int MAX_CRATER_DEPTH = 5;
    private static final int MIN_CRATER_DEPTH = 2;
    /** Rim height in blocks; real craters have a raised ejecta rim. */
    private static final int CRATER_RIM_HEIGHT = 1;

    @Override
    public void generateTerrain(World world, Chunk chunk, int chunkX, int chunkZ) {
        Block stone = getStoneBlock();
        Block surface = getSurfaceBlock();

        for (int localX = 0; localX < 16; localX++) {
            for (int localZ = 0; localZ < 16; localZ++) {
                for (int y = 0; y < STONE_HEIGHT; y++) {
                    chunk.func_150807_a(localX, y, localZ, stone, 0);
                }
                for (int y = STONE_HEIGHT; y < BASE_SURFACE_Y + REGOLITH_LAYERS; y++) {
                    chunk.func_150807_a(localX, y, localZ, surface, 0);
                }
            }
        }

        int baseWorldX = chunkX * 16;
        int baseWorldZ = chunkZ * 16;
        List<Crater> craters = collectCratersInNeighborhood(world.getSeed(), chunkX, chunkZ);
        applyCratersToChunk(chunk, surface, baseWorldX, baseWorldZ, craters);
    }

    /**
     * Collect all craters whose circles can intersect this chunk. We consider the
     * 3x3 chunk neighborhood so craters centered in a neighbor chunk still get
     * carved into this chunk where they overlap. Craters are placed in world
     * coordinates so the same crater is generated consistently from any chunk.
     */
    private List<Crater> collectCratersInNeighborhood(long worldSeed, int chunkX, int chunkZ) {
        List<Crater> list = new ArrayList<Crater>();
        for (int dcx = -1; dcx <= 1; dcx++) {
            for (int dcz = -1; dcz <= 1; dcz++) {
                int nc = chunkX + dcx;
                int nz = chunkZ + dcz;
                long seed = worldSeed + (long) nc * 341873128712L + (long) nz * 132897987541L;
                Random rng = new Random(seed);
                int baseNx = nc * 16;
                int baseNz = nz * 16;
                int numCraters = rng.nextInt(CRATER_CHANCE_PER_CHUNK + 1);
                for (int i = 0; i < numCraters; i++) {
                    int cwx = baseNx + rng.nextInt(16);
                    int cwz = baseNz + rng.nextInt(16);
                    int radius = MIN_CRATER_RADIUS + rng.nextInt(MAX_CRATER_RADIUS - MIN_CRATER_RADIUS + 1);
                    int maxDepth = MIN_CRATER_DEPTH + rng.nextInt(MAX_CRATER_DEPTH - MIN_CRATER_DEPTH + 1);
                    list.add(new Crater(cwx, cwz, radius, maxDepth));
                }
            }
        }
        return list;
    }

    /**
     * Apply all craters to the current chunk using world coordinates. For each
     * block in the chunk we compute the max depth from any overlapping crater
     * (bowl shape), then carve. Rim is applied where we're just outside a crater.
     */
    private void applyCratersToChunk(Chunk chunk, Block surfaceBlock, int baseWorldX, int baseWorldZ,
            List<Crater> craters) {
        int surfaceY = BASE_SURFACE_Y + REGOLITH_LAYERS - 1;

        for (int localX = 0; localX < 16; localX++) {
            for (int localZ = 0; localZ < 16; localZ++) {
                int wx = baseWorldX + localX;
                int wz = baseWorldZ + localZ;

                int maxDepth = 0;
                boolean inRim = false;
                for (Crater c : craters) {
                    double dx = wx - c.worldX;
                    double dz = wz - c.worldZ;
                    double dist = Math.sqrt(dx * dx + dz * dz);
                    if (dist <= c.radius) {
                        double t = dist / Math.max(1, c.radius);
                        int depth = (int) (c.maxDepth * (1.0 - t * t) + 0.5);
                        if (depth > maxDepth) {
                            maxDepth = depth;
                        }
                    } else if (dist <= c.radius + 1 && CRATER_RIM_HEIGHT > 0) {
                        inRim = true;
                    }
                }
                maxDepth = Math.min(maxDepth, surfaceY + 1);
                for (int d = 0; d < maxDepth; d++) {
                    int y = surfaceY - d;
                    if (y >= 0) {
                        chunk.func_150807_a(localX, y, localZ, Blocks.air, 0);
                    }
                }
                if (inRim && maxDepth == 0) {
                    int rimY = surfaceY + CRATER_RIM_HEIGHT;
                    if (rimY < 256) {
                        chunk.func_150807_a(localX, rimY, localZ, surfaceBlock, 0);
                    }
                }
            }
        }
    }

    private static final class Crater {
        final int worldX;
        final int worldZ;
        final int radius;
        final int maxDepth;

        Crater(int worldX, int worldZ, int radius, int maxDepth) {
            this.worldX = worldX;
            this.worldZ = worldZ;
            this.radius = radius;
            this.maxDepth = maxDepth;
        }
    }

    @Override
    public void populate(IChunkProvider chunkProvider, World world, int chunkX, int chunkZ) {
        // No ores or structures on the moon for now.
    }

    @Override
    public Block getSurfaceBlock() {
        return Blocks.end_stone;
    }

    @Override
    public Block getStoneBlock() {
        return Blocks.stone;
    }
}
