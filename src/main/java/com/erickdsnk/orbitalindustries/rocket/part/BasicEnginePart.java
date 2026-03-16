package com.erickdsnk.orbitalindustries.rocket.part;

import com.erickdsnk.orbitalindustries.rocket.RocketPart;
import com.erickdsnk.orbitalindustries.rocket.RocketPartType;
import com.erickdsnk.orbitalindustries.rocket.RocketStats;

/**
 * Simple engine part that adds thrust and mass. Used for blueprint aggregation
 * tests.
 */
public final class BasicEnginePart implements RocketPart {

    private final double thrust;
    private final double mass;

    public BasicEnginePart(double thrust, double mass) {
        this.thrust = thrust;
        this.mass = mass;
    }

    @Override
    public RocketPartType getType() {
        return RocketPartType.ENGINE;
    }

    @Override
    public void modifyStats(RocketStats stats) {
        stats.addThrust(thrust);
        stats.addMass(mass);
    }
}
