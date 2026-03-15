package com.erickdsnk.orbitalindustries.planet.structure;

import java.util.Random;

import net.minecraft.world.World;

/**
 * Interface for structure generation during chunk populate. Implementations
 * place structures (e.g. abandoned shelters, crash sites) in the world.
 * Registered by type id in {@link PlanetStructureRegistry}.
 */
public interface PlanetStructureGenerator {

    /**
     * Attempt to generate this structure in the given chunk. Called when the
     * dimension's structure list includes this type and the chance roll passes.
     *
     * @param world  the world
     * @param chunkX chunk X coordinate
     * @param chunkZ chunk Z coordinate
     * @param random seeded random for this chunk
     * @param config entry from JSON (chancePerChunk, params)
     * @return true if a structure was placed (e.g. for logging)
     */
    boolean generate(World world, int chunkX, int chunkZ, Random random, StructureEntry config);
}
