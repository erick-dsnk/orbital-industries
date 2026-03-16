package com.erickdsnk.orbitalindustries.planet.gen.feature;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.chunk.Chunk;

import com.erickdsnk.orbitalindustries.core.BlockResolver;
import com.erickdsnk.orbitalindustries.planet.biome.PlanetBiome;
import com.erickdsnk.orbitalindustries.planet.biome.PlanetBiomeProvider;

/**
 * Terrain feature that carves bowl-shaped craters with an ejecta rim.
 * Optionally
 * fills the crater floor with a configurable block (e.g. impact/burnt rock) via
 * generator option {@code craterFloorBlock} (block name like
 * "OrbitalIndustries:moon_basalt").
 * Options: craterChancePerChunk, minRadius, maxRadius, minDepth, maxDepth,
 * rimHeight,
 * craterFloorBlock (optional), craterFloorLayers (default 2; only used when
 * craterFloorBlock is set).
 * Uses biome's craterProbabilityModifier at each neighbor chunk center.
 */
public final class CraterFeature implements TerrainFeature {

    private static final int DEFAULT_CRATER_CHANCE_PER_CHUNK = 3;
    private static final int DEFAULT_CRATER_FLOOR_LAYERS = 2;
    private static final int MIN_CRATER_RADIUS = 2;
    private static final int MAX_CRATER_RADIUS = 6;
    private static final int MIN_CRATER_DEPTH = 2;
    private static final int MAX_CRATER_DEPTH = 5;
    private static final int CRATER_RIM_HEIGHT = 1;

    @Override
    public void apply(Chunk chunk, int chunkX, int chunkZ, int baseWorldX, int baseWorldZ,
            long seed, int[][] topSurfaceY, TerrainFeatureContext context) {
        int craterChance = context.getIntOption("craterChancePerChunk", DEFAULT_CRATER_CHANCE_PER_CHUNK);
        int minRadius = context.getIntOption("craterMinRadius", MIN_CRATER_RADIUS);
        int maxRadius = context.getIntOption("craterMaxRadius", MAX_CRATER_RADIUS);
        int minDepth = context.getIntOption("craterMinDepth", MIN_CRATER_DEPTH);
        int maxDepth = context.getIntOption("craterMaxDepth", MAX_CRATER_DEPTH);
        int rimHeight = context.getIntOption("craterRimHeight", CRATER_RIM_HEIGHT);

        int floorLayers = context.getIntOption("craterFloorLayers", DEFAULT_CRATER_FLOOR_LAYERS);
        String floorBlockName = context.getStringOption("craterFloorBlock");
        Block craterFloorBlock = null;
        if (floorBlockName != null && !floorBlockName.isEmpty()) {
            craterFloorBlock = BlockResolver.getBlockByName(floorBlockName, context.getDefaultStoneBlock());
        }
        PlanetBiomeProvider provider = context.getBiomeProvider();
        List<Crater> craters = collectCratersInNeighborhood(seed, chunkX, chunkZ, provider,
                craterChance, minRadius, maxRadius, minDepth, maxDepth);
        applyCratersToChunk(chunk, context, baseWorldX, baseWorldZ, craters, topSurfaceY, rimHeight, floorLayers,
                craterFloorBlock);
    }

    private List<Crater> collectCratersInNeighborhood(long worldSeed, int chunkX, int chunkZ,
            PlanetBiomeProvider provider, int craterChancePerChunk, int minRadius, int maxRadius,
            int minDepth, int maxDepth) {
        List<Crater> list = new ArrayList<Crater>();
        for (int dcx = -1; dcx <= 1; dcx++) {
            for (int dcz = -1; dcz <= 1; dcz++) {
                int nc = chunkX + dcx;
                int nz = chunkZ + dcz;
                long seed = worldSeed + (long) nc * 341873128712L + (long) nz * 132897987541L;
                Random rng = new Random(seed);
                int centerX = nc * 16 + 8;
                int centerZ = nz * 16 + 8;
                double modifier = 1.0;
                if (provider != null) {
                    PlanetBiome centerBiome = provider.getBiomeAt(centerX, centerZ);
                    if (centerBiome != null) {
                        modifier = centerBiome.getCraterProbabilityModifier();
                    }
                }
                int maxCraters = Math.max(0, (int) Math.ceil((craterChancePerChunk + 1) * modifier));
                int numCraters = maxCraters > 0 ? rng.nextInt(maxCraters) : 0;
                int baseNx = nc * 16;
                int baseNz = nz * 16;
                int radiusRange = Math.max(1, maxRadius - minRadius + 1);
                int depthRange = Math.max(1, maxDepth - minDepth + 1);
                for (int i = 0; i < numCraters; i++) {
                    int cwx = baseNx + rng.nextInt(16);
                    int cwz = baseNz + rng.nextInt(16);
                    int radius = minRadius + rng.nextInt(radiusRange);
                    int maxDepthVal = minDepth + rng.nextInt(depthRange);
                    list.add(new Crater(cwx, cwz, radius, maxDepthVal));
                }
            }
        }
        return list;
    }

    private void applyCratersToChunk(Chunk chunk, TerrainFeatureContext context, int baseWorldX, int baseWorldZ,
            List<Crater> craters, int[][] topSurfaceY, int rimHeight, int floorLayers, Block craterFloorBlock) {
        for (int localX = 0; localX < 16; localX++) {
            for (int localZ = 0; localZ < 16; localZ++) {
                int wx = baseWorldX + localX;
                int wz = baseWorldZ + localZ;
                int surfaceY = topSurfaceY[localX][localZ];
                PlanetBiome biome = context.getBiomeAt(wx, wz);
                Block surfaceBlock = biome != null ? biome.getSurfaceBlock() : context.getDefaultSurfaceBlock();
                if (surfaceBlock == null) {
                    surfaceBlock = Blocks.end_stone;
                }

                int maxDepth = 0;
                boolean inRim = false;
                for (Crater c : craters) {
                    double dx = wx - c.worldX;
                    double dz = wz - c.worldZ;
                    double distSq = dx * dx + dz * dz;
                    double radiusSq = (double) c.radius * c.radius;
                    if (distSq <= radiusSq) {
                        double dist = Math.sqrt(distSq);
                        double t = dist / Math.max(1, c.radius);
                        int depth = (int) (c.maxDepth * (1.0 - t * t) + 0.5);
                        if (depth > maxDepth) {
                            maxDepth = depth;
                        }
                    } else if (rimHeight > 0) {
                        double rimRadiusSq = (double) (c.radius + 1) * (c.radius + 1);
                        if (distSq <= rimRadiusSq) {
                            inRim = true;
                        }
                    }
                }
                maxDepth = Math.min(maxDepth, surfaceY + 1);
                for (int d = 0; d < maxDepth; d++) {
                    int y = surfaceY - d;
                    if (y >= 0) {
                        chunk.func_150807_a(localX, y, localZ, Blocks.air, 0);
                    }
                }
                // Fill crater floor with configured block (e.g. impact/burnt rock) when
                // craterFloorBlock is set
                if (maxDepth > 0 && floorLayers > 0 && craterFloorBlock != null) {
                    int floorY = surfaceY - maxDepth;
                    for (int layer = 0; layer < floorLayers && floorY - layer >= 0; layer++) {
                        chunk.func_150807_a(localX, floorY - layer, localZ, craterFloorBlock, 0);
                    }
                }
                if (inRim && maxDepth == 0) {
                    int rimY = surfaceY + rimHeight;
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
}
