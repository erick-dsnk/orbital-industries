package com.erickdsnk.orbitalindustries.planet.structure;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.world.World;

/**
 * Registry of structure generators by type id. Used during chunk populate to
 * place structures listed in a dimension's JSON config.
 */
public final class PlanetStructureRegistry {

    private final Map<String, PlanetStructureGenerator> byType = new LinkedHashMap<String, PlanetStructureGenerator>();

    public void register(String typeId, PlanetStructureGenerator generator) {
        if (typeId != null && generator != null) {
            byType.put(typeId, generator);
        }
    }

    public PlanetStructureGenerator get(String typeId) {
        return typeId == null ? null : byType.get(typeId);
    }

    /**
     * Generate a structure of the given type. No-op if type is not registered.
     *
     * @return true if a generator was found and ran (even if it placed nothing)
     */
    public boolean generate(String typeId, World world, int chunkX, int chunkZ, Random random, StructureEntry config) {
        PlanetStructureGenerator gen = get(typeId);
        if (gen == null) {
            return false;
        }
        return gen.generate(world, chunkX, chunkZ, random, config);
    }

    public Map<String, PlanetStructureGenerator> getAll() {
        return Collections.unmodifiableMap(new LinkedHashMap<String, PlanetStructureGenerator>(byType));
    }
}
