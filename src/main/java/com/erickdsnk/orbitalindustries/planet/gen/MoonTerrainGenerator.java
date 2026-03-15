package com.erickdsnk.orbitalindustries.planet.gen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

import com.erickdsnk.orbitalindustries.core.OIModLogger;
import com.erickdsnk.orbitalindustries.planet.PlanetTerrainGenerator;
import com.erickdsnk.orbitalindustries.planet.biome.PlanetBiome;
import com.erickdsnk.orbitalindustries.planet.biome.PlanetBiomeProvider;

/**
 * Terrain generator for the Moon: rolling highlands and lowlands (multi-octave
 * noise), stone base, endstone/regolith surface, and crater generation. Surface
 * height varies so the landscape is not flat apart from craters. Uses
 * {@link PlanetBiomeProvider} for planet-specific biomes (e.g.
 * cratered_highlands,
 * smooth_plains) with different height and crater density.
 * <p>
 * This system will later support Mars canyon systems (biome-driven height and
 * structure hooks), asteroid field variation (biome as asteroid type), ice
 * worlds
 * (ice/snow/rock biomes), and gas giant moons (each dimension with its own
 * planet and biome list).
 */
public class MoonTerrainGenerator implements PlanetTerrainGenerator {

    private static final OIModLogger LOG = new OIModLogger("MoonTerrainGenerator");

    private static final int DEFAULT_BASE_SURFACE_Y = 64;
    private static final double TERRAIN_NOISE_AMPLITUDE = 8.0;
    private static final int MIN_REGOLITH_LAYERS = 1;
    private static final int MAX_REGOLITH_LAYERS = 3;
    private static final int DEFAULT_CRATER_CHANCE_PER_CHUNK = 3;
    private static final int MAX_CRATER_RADIUS = 6;
    private static final int MIN_CRATER_RADIUS = 2;
    /** Maximum depth (blocks) at crater center; bowl shape so rim is shallow. */
    private static final int MAX_CRATER_DEPTH = 5;
    private static final int MIN_CRATER_DEPTH = 2;
    /** Rim height in blocks; real craters have a raised ejecta rim. */
    private static final int CRATER_RIM_HEIGHT = 1;

    private final List<PlanetBiome> biomes;
    private final int baseSurfaceY;
    private final int craterChancePerChunk;

    /**
     * Constructor with biome list and options from JSON. Use default single moon
     * biome if biomes is null or size &lt; 2.
     *
     * @param options may contain "baseSurfaceY" (int), "craterChancePerChunk" (int)
     */
    public MoonTerrainGenerator(List<PlanetBiome> biomes, Map<String, Object> options) {
        if (biomes == null || biomes.size() < 2) {
            this.biomes = Collections.singletonList(
                    new PlanetBiome("moon_default", "Lunar Surface", Blocks.end_stone, Blocks.stone, 0.0, 1.0));
        } else {
            this.biomes = Collections.unmodifiableList(new ArrayList<PlanetBiome>(biomes));
        }
        this.baseSurfaceY = getIntOption(options, "baseSurfaceY", DEFAULT_BASE_SURFACE_Y);
        this.craterChancePerChunk = getIntOption(options, "craterChancePerChunk", DEFAULT_CRATER_CHANCE_PER_CHUNK);
    }

    /**
     * Constructor with biome list only; uses default options.
     */
    public MoonTerrainGenerator(List<PlanetBiome> biomes) {
        this(biomes, null);
    }

    /**
     * No-arg constructor for backward compatibility; uses single default moon
     * biome and default options.
     */
    public MoonTerrainGenerator() {
        this(null, null);
    }

    private static int getIntOption(Map<String, Object> options, String key, int defaultValue) {
        if (options == null || !options.containsKey(key)) {
            return defaultValue;
        }
        Object v = options.get(key);
        if (v instanceof Number) {
            return ((Number) v).intValue();
        }
        return defaultValue;
    }

    @Override
    public void generateTerrain(World world, Chunk chunk, int chunkX, int chunkZ) {
        long seed = world.getSeed();
        int baseWorldX = chunkX * 16;
        int baseWorldZ = chunkZ * 16;
        PlanetBiomeProvider provider = new PlanetBiomeProvider(seed, biomes);

        int[][] topSurfaceY = new int[16][16];
        Set<String> biomeIdsInChunk = new HashSet<String>();
        for (int localX = 0; localX < 16; localX++) {
            for (int localZ = 0; localZ < 16; localZ++) {
                int wx = baseWorldX + localX;
                int wz = baseWorldZ + localZ;
                PlanetBiome biome = provider.getBiomeAt(wx, wz);
                if (biome == null) {
                    biome = biomes.isEmpty() ? null : biomes.get(0);
                }
                Block stone = biome != null ? biome.getStoneBlock() : getStoneBlock();
                Block surface = biome != null ? biome.getSurfaceBlock() : getSurfaceBlock();
                double heightMod = getSmoothedHeightModifier(provider, wx, wz);
                if (biome != null) {
                    biomeIdsInChunk.add(biome.getId());
                }
                int baseSurfaceY = this.baseSurfaceY
                        + (int) Math.round(terrainNoise(seed, wx, wz) * TERRAIN_NOISE_AMPLITUDE)
                        + (int) Math.round(heightMod);
                int regolithLayers = regolithLayersAt(seed, wx, wz);
                int topY = baseSurfaceY + regolithLayers - 1;
                topSurfaceY[localX][localZ] = topY;

                int stoneTop = baseSurfaceY - regolithLayers;
                for (int y = 0; y < stoneTop; y++) {
                    chunk.func_150807_a(localX, y, localZ, stone, 0);
                }
                for (int y = stoneTop; y <= topY; y++) {
                    chunk.func_150807_a(localX, y, localZ, surface, 0);
                }
            }
        }

        List<Crater> craters = collectCratersInNeighborhood(seed, chunkX, chunkZ, provider);
        applyCratersToChunk(chunk, provider, baseWorldX, baseWorldZ, craters, topSurfaceY);

        byte[] biomeArray = new byte[256];
        for (int localZ = 0; localZ < 16; localZ++) {
            for (int localX = 0; localX < 16; localX++) {
                PlanetBiome biome = provider.getBiomeAt(baseWorldX + localX, baseWorldZ + localZ);
                int id = (biome != null && biome.getMinecraftBiomeId() >= 0)
                        ? biome.getMinecraftBiomeId()
                        : 0;
                biomeArray[localZ * 16 + localX] = (byte) (id & 0xFF);
            }
        }
        chunk.setBiomeArray(biomeArray);

        LOG.debug("Chunk " + chunkX + "," + chunkZ + " biomes: " + biomeIdsInChunk);
    }

    /**
     * Average terrain height modifier over a 5x5 neighborhood so biome boundaries
     * blend smoothly instead of creating a sudden step in elevation.
     */
    private double getSmoothedHeightModifier(PlanetBiomeProvider provider, int wx, int wz) {
        final int radius = 2;
        double sum = 0.0;
        int count = 0;
        for (int dz = -radius; dz <= radius; dz++) {
            for (int dx = -radius; dx <= radius; dx++) {
                PlanetBiome b = provider.getBiomeAt(wx + dx, wz + dz);
                sum += b != null ? b.getTerrainHeightModifier() : 0.0;
                count++;
            }
        }
        return count > 0 ? sum / count : 0.0;
    }

    // --- Procedural noise (interpolated value noise for smooth terrain) ---

    /**
     * Terrain noise frequency: low value gives wide, gentle hills (lower =
     * wider and smoother, higher = more detail and steeper).
     */
    private static final double TERRAIN_NOISE_FREQ = 0.028;
    /** Regolith noise frequency (independent pattern). */
    private static final double REGOLITH_NOISE_FREQ = 0.04;
    /** Seed offset for regolith channel so it differs from terrain. */
    private static final long REGOLITH_NOISE_SEED_OFFSET = 0x9E3779B97F4A7C15L;

    /**
     * Smooth 2D value noise for terrain height. Uses lattice hash + bilinear
     * interpolation with smoothstep so height varies continuously (no grid steps).
     * Returns value in [0, 1]; use (noise * 2 - 1) for [-1, 1] symmetric variation.
     */
    private static double valueNoise2D(long seed, double x, double z) {
        int ix = (int) Math.floor(x);
        int iz = (int) Math.floor(z);
        double fx = x - ix;
        double fz = z - iz;
        double sx = smoothstep(fx);
        double sz = smoothstep(fz);

        double v00 = latticeHash(seed, ix, iz);
        double v10 = latticeHash(seed, ix + 1, iz);
        double v01 = latticeHash(seed, ix, iz + 1);
        double v11 = latticeHash(seed, ix + 1, iz + 1);

        double a = v00 + sx * (v10 - v00);
        double b = v01 + sx * (v11 - v01);
        return a + sz * (b - a);
    }

    /** Smoothstep for interpolation (avoids linear banding). */
    private static double smoothstep(double t) {
        t = Math.max(0, Math.min(1, t));
        return t * t * (3.0 - 2.0 * t);
    }

    /** Deterministic hash at lattice point; returns [0, 1). */
    private static double latticeHash(long seed, int ix, int iz) {
        long h = seed + (long) ix * 374761393L + (long) iz * 668265263L;
        h = (h ^ (h >>> 33)) * 0xff51afd7ed558ccdL;
        h = (h ^ (h >>> 33)) * 0xc4ceb9fe1a85ec53L;
        // Use long literal so 0x7FFF_FFFFL + 1 does not overflow (int would become
        // Integer.MIN_VALUE).
        return ((h ^ (h >>> 33)) & 0x7FFF_FFFFL) / (double) (0x7FFF_FFFFL + 1L);
    }

    /**
     * Terrain height noise: three octaves for wide highlands/lowlands plus
     * rolling detail so the surface is clearly uneven, not a flat plane.
     * Returns value in [-1, 1].
     * Public for use by MoonChunkProvider (full chunk generation).
     */
    public static double terrainNoise(long seed, double wx, double wz) {
        double wide = (valueNoise2D(seed, wx * TERRAIN_NOISE_FREQ * 0.35, wz * TERRAIN_NOISE_FREQ * 0.35) * 2.0 - 1.0)
                * 0.55;
        double main = (valueNoise2D(seed + 1, wx * TERRAIN_NOISE_FREQ, wz * TERRAIN_NOISE_FREQ) * 2.0 - 1.0) * 0.30;
        double detail = (valueNoise2D(seed + 2, wx * TERRAIN_NOISE_FREQ * 2.2, wz * TERRAIN_NOISE_FREQ * 2.2) * 2.0
                - 1.0) * 0.15;
        double n = wide + main + detail;
        return Math.max(-1.0, Math.min(1.0, n));
    }

    /**
     * Regolith layer count (1–3) from smooth noise so variation follows contours
     * instead of a blocky grid. Public for use by MoonChunkProvider.
     */
    public static int regolithLayersAt(long seed, double wx, double wz) {
        double n = valueNoise2D(seed + REGOLITH_NOISE_SEED_OFFSET,
                wx * REGOLITH_NOISE_FREQ, wz * REGOLITH_NOISE_FREQ);
        int layers = MIN_REGOLITH_LAYERS + (int) (n * (MAX_REGOLITH_LAYERS - MIN_REGOLITH_LAYERS + 1));
        return Math.max(MIN_REGOLITH_LAYERS, Math.min(MAX_REGOLITH_LAYERS, layers));
    }

    /**
     * Collect all craters whose circles can intersect this chunk. We consider the
     * 3x3 chunk neighborhood so craters centered in a neighbor chunk still get
     * carved into this chunk where they overlap. Craters are placed in world
     * coordinates so the same crater is generated consistently from any chunk.
     * Uses the biome at each neighbor chunk center to scale crater count via
     * craterProbabilityModifier.
     */
    private List<Crater> collectCratersInNeighborhood(long worldSeed, int chunkX, int chunkZ,
            PlanetBiomeProvider provider) {
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
     * (bowl shape), then carve. Rim is applied where we're just outside a crater;
     * surface block comes from the biome at that column.
     */
    private void applyCratersToChunk(Chunk chunk, PlanetBiomeProvider provider, int baseWorldX, int baseWorldZ,
            List<Crater> craters, int[][] topSurfaceY) {
        for (int localX = 0; localX < 16; localX++) {
            for (int localZ = 0; localZ < 16; localZ++) {
                int wx = baseWorldX + localX;
                int wz = baseWorldZ + localZ;
                int surfaceY = topSurfaceY[localX][localZ];
                PlanetBiome biome = provider != null ? provider.getBiomeAt(wx, wz) : null;
                Block surfaceBlock = biome != null ? biome.getSurfaceBlock() : getSurfaceBlock();

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
                    } else if (CRATER_RIM_HEIGHT > 0) {
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
