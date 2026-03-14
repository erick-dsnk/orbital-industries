package com.erickdsnk.orbitalindustries.dimension;

import net.minecraft.world.WorldProvider;

/**
 * Base WorldProvider for space dimensions. Uses dimension ID from registration;
 * no hardcoded dimension IDs. Subclasses or a single implementation can be
 * registered per dimension via DimensionRegistry.
 *
 * TODO: Sky renderer registration for space sky.
 * TODO: Spawn point and world gen; delegate to SpaceDimensionProvider or config.
 */
public class SpaceWorldProvider extends WorldProvider {

    @Override
    public String getDimensionName() {
        return "Space";
    }
}
