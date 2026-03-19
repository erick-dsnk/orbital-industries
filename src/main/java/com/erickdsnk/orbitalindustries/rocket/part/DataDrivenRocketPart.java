package com.erickdsnk.orbitalindustries.rocket.part;

import com.erickdsnk.orbitalindustries.rocket.RocketPart;
import com.erickdsnk.orbitalindustries.rocket.RocketPartDefinition;
import com.erickdsnk.orbitalindustries.rocket.RocketPartType;
import com.erickdsnk.orbitalindustries.rocket.RocketStats;

/**
 * {@link RocketPart} implementation loaded from JSON via
 * {@link RocketPartDefinition}.
 */
public final class DataDrivenRocketPart implements RocketPart {

    private final RocketPartDefinition def;

    public DataDrivenRocketPart(RocketPartDefinition def) {
        if (def == null) {
            throw new IllegalArgumentException("def");
        }
        this.def = def;
    }

    public RocketPartDefinition getDefinition() {
        return def;
    }

    public String getId() {
        return def.getId();
    }

    @Override
    public RocketPartType getType() {
        RocketPartType t = def.getType();
        return t != null ? t : RocketPartType.HULL;
    }

    @Override
    public void modifyStats(RocketStats stats) {
        if (def.getThrust() != 0.0) {
            stats.addThrust(def.getThrust());
        }
        if (def.getFuelCapacity() != 0.0) {
            stats.addFuelCapacity(def.getFuelCapacity());
        }
        if (def.getMass() != 0.0) {
            stats.addMass(def.getMass());
        }
        if (def.getNavigationTier() != 0) {
            stats.addNavigationTier(def.getNavigationTier());
        }
        if (def.getMaxRange() != 0.0) {
            stats.addMaxRange(def.getMaxRange());
        }
    }
}
