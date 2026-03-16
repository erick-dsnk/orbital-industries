package com.erickdsnk.orbitalindustries.rocket;

/**
 * Interface for a single rocket part. Parts modify aggregated
 * {@link RocketStats}
 * when assembled into a {@link RocketBlueprint}. Implementations may be item-
 * or
 * block-based in the future.
 */
public interface RocketPart {

    /** Part category (engine, fuel tank, guidance, hull, payload). */
    RocketPartType getType();

    /** Apply this part's contribution to the combined rocket stats. */
    void modifyStats(RocketStats stats);
}
