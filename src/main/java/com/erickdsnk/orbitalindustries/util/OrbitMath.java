package com.erickdsnk.orbitalindustries.util;

/**
 * Static math utilities for gravity, orbits, and space travel.
 * Pure functions; no game state.
 *
 * TODO: Used by GravityManager for per-dimension gravity multipliers.
 * TODO: Used by transport/travel systems for orbital period and escape velocity calculations.
 */
public final class OrbitMath {

    private OrbitMath() {}

    /** Gravitational constant (m^3 kg^-1 s^-2). Used for gravity/orbit calculations. */
    public static final double G = 6.674e-11;

    /**
     * Gravity acceleration at surface given mass and radius (SI-like units).
     * g = G * M / R^2
     *
     * TODO: Scale to game units and use with GravityManager for fall damage and movement.
     */
    public static double surfaceGravity(double massKg, double radiusM) {
        if (radiusM <= 0) return 0;
        return G * massKg / (radiusM * radiusM);
    }

    /**
     * Gravity multiplier relative to Earth (1.0 = Earth surface).
     * Result is dimensionless; multiply by 9.81 for approximate m/s^2.
     *
     * TODO: GravityManager will use this or a similar ratio for dimension gravity.
     */
    public static double gravityMultiplierRelativeToEarth(double massKg, double radiusM) {
        // Earth reference: ~5.97e24 kg, ~6.37e6 m
        double earthG = surfaceGravity(5.97e24, 6.37e6);
        double g = surfaceGravity(massKg, radiusM);
        return earthG > 0 ? g / earthG : 0;
    }

    /**
     * Orbital period in seconds for a circular orbit: T = 2*pi*sqrt(r^3 / (G*M)).
     *
     * TODO: Used for orbital mechanics and travel time estimates.
     */
    public static double orbitalPeriodSeconds(double orbitRadiusM, double centralMassKg) {
        if (centralMassKg <= 0 || orbitRadiusM <= 0) return 0;
        return 2 * Math.PI * Math.sqrt(Math.pow(orbitRadiusM, 3) / (G * centralMassKg));
    }

    /**
     * Escape velocity (m/s) from surface: v = sqrt(2*G*M/R).
     *
     * TODO: Used for launch/transport cost or validation.
     */
    public static double escapeVelocity(double massKg, double radiusM) {
        if (radiusM <= 0 || massKg <= 0) return 0;
        return Math.sqrt(2 * G * massKg / radiusM);
    }
}
