package com.erickdsnk.orbitalindustries.rocket;

/**
 * Categories of rocket parts. Each part type contributes to {@link RocketStats}
 * when assembled into a {@link RocketBlueprint}.
 */
public enum RocketPartType {
    ENGINE,
    FUEL_TANK,
    GUIDANCE,
    HULL,
    PAYLOAD
}
