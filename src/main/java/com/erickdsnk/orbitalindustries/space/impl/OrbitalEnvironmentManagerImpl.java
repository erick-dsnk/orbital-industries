package com.erickdsnk.orbitalindustries.space.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.world.World;

import com.erickdsnk.orbitalindustries.core.ConfigManager;
import com.erickdsnk.orbitalindustries.space.OrbitalEnvironmentManager;

/**
 * Default OrbitalEnvironmentManager: space dimensions are configured (e.g. from config).
 * No hardcoded dimension IDs; use ConfigManager or a dedicated space-dimension set.
 *
 * TODO: Populate space dimension IDs from config or PlanetRegistry (e.g. "orbit" dimensions).
 * TODO: Radiation/temperature by zone.
 */
public final class OrbitalEnvironmentManagerImpl implements OrbitalEnvironmentManager {

    private final Set<Integer> spaceDimensionIds = new HashSet<Integer>();

    public OrbitalEnvironmentManagerImpl() {
        // TODO: Load from config or register when dimensions are created.
        spaceDimensionIds.add(ConfigManager.getSpaceDimensionId());
    }

    public void registerSpaceDimension(int dimensionId) {
        spaceDimensionIds.add(dimensionId);
    }

    @Override
    public boolean isSpaceDimension(int dimensionId) {
        return spaceDimensionIds.contains(dimensionId);
    }

    @Override
    public boolean isInSpace(World world) {
        return world != null && isSpaceDimension(world.provider.dimensionId);
    }
}
