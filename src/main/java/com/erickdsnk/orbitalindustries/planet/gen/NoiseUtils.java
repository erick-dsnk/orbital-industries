package com.erickdsnk.orbitalindustries.planet.gen;

/**
 * Shared deterministic noise utilities for terrain and biome generation. Used
 * by
 * NoisySurfaceTerrainGenerator, terrain features (caves), and
 * PlanetBiomeProvider
 * so noise is defined once and remains consistent across the codebase.
 */
public final class NoiseUtils {

    private NoiseUtils() {
    }

    /** Smoothstep for interpolation (avoids linear banding). */
    public static double smoothstep(double t) {
        t = Math.max(0, Math.min(1, t));
        return t * t * (3.0 - 2.0 * t);
    }

    /** Deterministic hash at lattice point; returns [0, 1). */
    public static double latticeHash(long seed, int ix, int iz) {
        long h = seed + (long) ix * 374761393L + (long) iz * 668265263L;
        h = (h ^ (h >>> 33)) * 0xff51afd7ed558ccdL;
        h = (h ^ (h >>> 33)) * 0xc4ceb9fe1a85ec53L;
        return ((h ^ (h >>> 33)) & 0x7FFF_FFFFL) / (double) (0x7FFF_FFFFL + 1L);
    }

    /** Deterministic 3D lattice hash; returns [0, 1). */
    public static double latticeHash3D(long seed, int ix, int iy, int iz) {
        long h = seed + (long) ix * 374761393L + (long) iy * 668265263L + (long) iz * 1103515245L;
        h = (h ^ (h >>> 33)) * 0xff51afd7ed558ccdL;
        h = (h ^ (h >>> 33)) * 0xc4ceb9fe1a85ec53L;
        return ((h ^ (h >>> 33)) & 0x7FFF_FFFFL) / (double) (0x7FFF_FFFFL + 1L);
    }

    /**
     * Smooth 2D value noise. Uses lattice hash + bilinear interpolation with
     * smoothstep. Returns value in [0, 1]; use (noise * 2 - 1) for [-1, 1].
     */
    public static double valueNoise2D(long seed, double x, double z) {
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

    /**
     * Smooth 3D value noise. Uses trilinear interpolation with smoothstep.
     * Returns value in [0, 1].
     */
    public static double valueNoise3D(long seed, double x, double y, double z) {
        int ix = (int) Math.floor(x);
        int iy = (int) Math.floor(y);
        int iz = (int) Math.floor(z);
        double fx = x - ix;
        double fy = y - iy;
        double fz = z - iz;
        double sx = smoothstep(fx);
        double sy = smoothstep(fy);
        double sz = smoothstep(fz);

        double v000 = latticeHash3D(seed, ix, iy, iz);
        double v100 = latticeHash3D(seed, ix + 1, iy, iz);
        double v010 = latticeHash3D(seed, ix, iy + 1, iz);
        double v110 = latticeHash3D(seed, ix + 1, iy + 1, iz);
        double v001 = latticeHash3D(seed, ix, iy, iz + 1);
        double v101 = latticeHash3D(seed, ix + 1, iy, iz + 1);
        double v011 = latticeHash3D(seed, ix, iy + 1, iz + 1);
        double v111 = latticeHash3D(seed, ix + 1, iy + 1, iz + 1);

        double a00 = v000 + sx * (v100 - v000);
        double a10 = v010 + sx * (v110 - v010);
        double a01 = v001 + sx * (v101 - v001);
        double a11 = v011 + sx * (v111 - v011);
        double b0 = a00 + sy * (a10 - a00);
        double b1 = a01 + sy * (a11 - a01);
        return b0 + sz * (b1 - b0);
    }
}
