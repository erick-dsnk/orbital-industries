package com.erickdsnk.orbitalindustries.environment.impl;

import net.minecraft.world.World;

import com.erickdsnk.orbitalindustries.environment.OxygenSystem;
import com.erickdsnk.orbitalindustries.space.AtmosphereManager;

/**
 * Default OxygenSystem: delegates to AtmosphereManager for natural atmosphere;
 * sealed structures will override in future (e.g. check for oxygen blocks).
 *
 * TODO: Scan for sealed structures and oxygen-providing blocks.
 */
public final class OxygenSystemImpl implements OxygenSystem {

    private final AtmosphereManager atmosphereManager;

    public OxygenSystemImpl(AtmosphereManager atmosphereManager) {
        this.atmosphereManager = atmosphereManager;
    }

    @Override
    public boolean hasOxygen(World world, int x, int y, int z) {
        if (world == null) return false;
        return atmosphereManager.hasAtmosphere(world.provider.dimensionId);
    }

    @Override
    public float getOxygenLevel(World world, int x, int y, int z) {
        return hasOxygen(world, x, y, z) ? 1.0f : 0.0f;
    }
}
