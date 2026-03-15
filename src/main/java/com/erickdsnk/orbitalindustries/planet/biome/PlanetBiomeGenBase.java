package com.erickdsnk.orbitalindustries.planet.biome;

import net.minecraft.world.biome.BiomeGenBase;

/**
 * Minimal BiomeGenBase for planet biomes so Minecraft's F3 debug screen and
 * other systems show the correct biome name. Used for the Moon dimension's
 * Cratered Highlands and Smooth Plains; register with unique IDs (e.g. 40, 41).
 */
public class PlanetBiomeGenBase extends BiomeGenBase {

    public PlanetBiomeGenBase(int id, String name) {
        super(id);
        setBiomeName(name);
        setDisableRain();
        enableSnow = false;
    }
}
