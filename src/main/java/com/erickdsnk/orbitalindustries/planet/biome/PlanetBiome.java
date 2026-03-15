package com.erickdsnk.orbitalindustries.planet.biome;

import net.minecraft.block.Block;

/**
 * Immutable data object representing a biome on a planet. Defines surface and
 * subsurface blocks plus terrain and feature modifiers. Used by
 * {@link PlanetBiomeProvider} to select biomes at world coordinates.
 * <p>
 * This type will later drive vegetation, structures, and resources. For
 * example:
 * Mars canyon systems (canyon_floor, mesa biomes with height modifiers and
 * structure hooks), asteroid field variation (metallic/silicate/ice "biomes"
 * with different blocks and resource tables), ice worlds (ice/snow/rock biomes
 * with temperature and water-ice blocks), and gas giant moons (each moon
 * dimension with its own planet and biome list). For now the implementation
 * is minimal to establish the biome architecture.
 */
public final class PlanetBiome {

    private final String id;
    private final String displayName;
    private final Block surfaceBlock;
    private final Block stoneBlock;
    /**
     * Additive height modifier in blocks applied to base terrain height (e.g. +2
     * for highlands, -1 for plains).
     */
    private final double terrainHeightModifier;
    /**
     * Multiplier for crater probability (1.0 = default, 0.5 = half craters,
     * 2.0 = double).
     */
    private final double craterProbabilityModifier;

    public PlanetBiome(String id, String displayName, Block surfaceBlock, Block stoneBlock,
            double terrainHeightModifier, double craterProbabilityModifier) {
        this.id = id;
        this.displayName = displayName;
        this.surfaceBlock = surfaceBlock;
        this.stoneBlock = stoneBlock;
        this.terrainHeightModifier = terrainHeightModifier;
        this.craterProbabilityModifier = craterProbabilityModifier;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Block getSurfaceBlock() {
        return surfaceBlock;
    }

    public Block getStoneBlock() {
        return stoneBlock;
    }

    public double getTerrainHeightModifier() {
        return terrainHeightModifier;
    }

    public double getCraterProbabilityModifier() {
        return craterProbabilityModifier;
    }
}
