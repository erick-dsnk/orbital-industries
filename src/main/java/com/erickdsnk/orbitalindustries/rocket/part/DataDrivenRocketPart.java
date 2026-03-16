package com.erickdsnk.orbitalindustries.rocket.part;

import com.erickdsnk.orbitalindustries.rocket.RocketPart;
import com.erickdsnk.orbitalindustries.rocket.RocketPartType;
import com.erickdsnk.orbitalindustries.rocket.RocketStats;

/**
 * {@link RocketPart} implementation loaded from JSON. Applies whichever stat
 * fields are defined (thrust, fuelCapacity, mass, navigationTier, maxRange).
 */
public final class DataDrivenRocketPart implements RocketPart {

    private final String id;
    private final RocketPartType type;
    private final double thrust;
    private final double fuelCapacity;
    private final double mass;
    private final int navigationTier;
    private final double maxRange;

    public DataDrivenRocketPart(String id, RocketPartType type,
            double thrust, double fuelCapacity, double mass, int navigationTier, double maxRange) {
        this.id = id;
        this.type = type != null ? type : RocketPartType.HULL;
        this.thrust = thrust;
        this.fuelCapacity = fuelCapacity;
        this.mass = mass;
        this.navigationTier = navigationTier;
        this.maxRange = maxRange;
    }

    public String getId() {
        return id;
    }

    @Override
    public RocketPartType getType() {
        return type;
    }

    @Override
    public void modifyStats(RocketStats stats) {
        if (thrust != 0.0)
            stats.addThrust(thrust);
        if (fuelCapacity != 0.0)
            stats.addFuelCapacity(fuelCapacity);
        if (mass != 0.0)
            stats.addMass(mass);
        if (navigationTier != 0)
            stats.addNavigationTier(navigationTier);
        if (maxRange != 0.0)
            stats.addMaxRange(maxRange);
    }
}
