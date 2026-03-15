package com.erickdsnk.orbitalindustries.planet.structure;

import java.util.Random;

import net.minecraft.world.World;

/**
 * No-op structure generator. Use for structure types that are configured in
 * JSON but not yet implemented (e.g. "abandoned_shelter").
 */
public final class NoOpStructureGenerator implements PlanetStructureGenerator {

    @Override
    public boolean generate(World world, int chunkX, int chunkZ, Random random, StructureEntry config) {
        return false;
    }
}
