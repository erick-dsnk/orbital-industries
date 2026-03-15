package com.erickdsnk.orbitalindustries.planet.gen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

import com.erickdsnk.orbitalindustries.planet.PlanetTerrainGenerator;

/**
 * Simple terrain generator for the Moon: mostly flat terrain with a stone base
 * layer, regolith-like surface (gravel), and basic crater generation using
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
    private static final int MAX_CRATER_RADIUS = 4;
    private static final int MIN_CRATER_RADIUS = 1;

    @Override
    public void generateTerrain(World world, Chunk chunk, int chunkX, int chunkZ) {
        Block stone = getStoneBlock();
        Block surface = getSurfaceBlock();
        Random rng = new Random(world.getSeed() + (long) chunkX * 341873128712L + (long) chunkZ * 132897987541L);

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

        int numCraters = rng.nextInt(CRATER_CHANCE_PER_CHUNK + 1);
        for (int i = 0; i < numCraters; i++) {
            int cx = rng.nextInt(16);
            int cz = rng.nextInt(16);
            int radius = MIN_CRATER_RADIUS + rng.nextInt(MAX_CRATER_RADIUS - MIN_CRATER_RADIUS + 1);
            int cy = BASE_SURFACE_Y + rng.nextInt(REGOLITH_LAYERS + 1);
            carveCrater(chunk, cx, cy, cz, radius);
        }
    }

    private void carveCrater(Chunk chunk, int centerX, int centerY, int centerZ, int radius) {
        int radiusSq = radius * radius;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (dx * dx + dz * dz <= radiusSq) {
                    int x = centerX + dx;
                    int z = centerZ + dz;
                    if (x >= 0 && x < 16 && z >= 0 && z < 16) {
                        for (int y = centerY; y >= 0; y--) {
                            chunk.func_150807_a(x, y, z, Blocks.air, 0);
                        }
                    }
                }
            }
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
