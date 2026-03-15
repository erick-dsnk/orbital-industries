package com.erickdsnk.orbitalindustries.planet;

/**
 * Type of atmosphere for a celestial body. Used by Planet data and
 * atmosphere/oxygen systems.
 * These values will later control oxygen mechanics (e.g. breathable vs vacuum)
 * and environmental
 * damage (e.g. TOXIC causing damage over time, NONE requiring life support).
 */
public enum AtmosphereType {

    /**
     * No atmosphere (vacuum). Will require life support / oxygen systems in space
     * travel.
     */
    NONE,

    /**
     * Thin atmosphere; may later affect oxygen consumption or partial
     * breathability.
     */
    THIN,

    /** Breathable; standard overworld-like conditions. */
    BREATHABLE,

    /** Toxic; will later apply environmental damage over time unless protected. */
    TOXIC
}
