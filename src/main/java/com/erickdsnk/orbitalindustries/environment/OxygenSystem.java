package com.erickdsnk.orbitalindustries.environment;

import net.minecraft.world.World;

/**
 * Interface for oxygen/sealed environment. Concepts: oxygen level, is sealed, consume oxygen.
 *
 * TODO: Blocks that provide oxygen (sealed rooms, oxygen distributors).
 * TODO: Integration with VacuumDamageHandler: damage when not sealed and no atmosphere.
 */
public interface OxygenSystem {

    /**
     * Whether the given position is in a sealed, oxygenated environment.
     */
    boolean hasOxygen(World world, int x, int y, int z);

    /**
     * TODO: Oxygen level 0.0–1.0 for partial pressure or sealed-area logic.
     */
    float getOxygenLevel(World world, int x, int y, int z);
}
