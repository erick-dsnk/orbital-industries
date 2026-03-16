package com.erickdsnk.orbitalindustries.rocket.part;

import com.erickdsnk.orbitalindustries.rocket.RocketPart;
import com.erickdsnk.orbitalindustries.rocket.RocketPartType;
import com.erickdsnk.orbitalindustries.rocket.RocketStats;

/**
 * Simple guidance part that adds navigation tier and mass. Used for blueprint
 * aggregation tests.
 */
public final class BasicGuidancePart implements RocketPart {

    private final int navigationTier;
    private final double mass;

    public BasicGuidancePart(int navigationTier, double mass) {
        this.navigationTier = navigationTier;
        this.mass = mass;
    }

    @Override
    public RocketPartType getType() {
        return RocketPartType.GUIDANCE;
    }

    @Override
    public void modifyStats(RocketStats stats) {
        stats.addNavigationTier(navigationTier);
        stats.addMass(mass);
    }
}
