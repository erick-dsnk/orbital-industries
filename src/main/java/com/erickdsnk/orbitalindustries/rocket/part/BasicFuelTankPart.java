package com.erickdsnk.orbitalindustries.rocket.part;

import com.erickdsnk.orbitalindustries.rocket.RocketPart;
import com.erickdsnk.orbitalindustries.rocket.RocketPartType;
import com.erickdsnk.orbitalindustries.rocket.RocketStats;

/**
 * Simple fuel tank part that adds fuel capacity and mass. Used for blueprint
 * aggregation tests.
 */
public final class BasicFuelTankPart implements RocketPart {

    private final double fuelCapacity;
    private final double mass;

    public BasicFuelTankPart(double fuelCapacity, double mass) {
        this.fuelCapacity = fuelCapacity;
        this.mass = mass;
    }

    @Override
    public RocketPartType getType() {
        return RocketPartType.FUEL_TANK;
    }

    @Override
    public void modifyStats(RocketStats stats) {
        stats.addFuelCapacity(fuelCapacity);
        stats.addMass(mass);
    }
}
