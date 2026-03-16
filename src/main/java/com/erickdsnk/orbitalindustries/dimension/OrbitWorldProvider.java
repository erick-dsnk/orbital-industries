package com.erickdsnk.orbitalindustries.dimension;

import net.minecraft.world.chunk.IChunkProvider;

import com.erickdsnk.orbitalindustries.world.gen.VoidChunkProvider;

/**
 * World provider for the orbit dimension. Void/sky only; no terrain.
 */
public class OrbitWorldProvider extends SpaceWorldProvider {

    @Override
    public String getDimensionName() {
        return "Orbit";
    }

    @Override
    public IChunkProvider createChunkGenerator() {
        return new VoidChunkProvider(worldObj);
    }
}
