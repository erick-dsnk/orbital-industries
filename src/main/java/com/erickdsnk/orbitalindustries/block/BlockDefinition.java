package com.erickdsnk.orbitalindustries.block;

/**
 * Data transfer object for block definitions loaded from JSON. All properties
 * are optional; defaults are applied in {@link BlockFromDefinition}.
 */
public final class BlockDefinition {

    /**
     * Unique block id (e.g. "moon_rock"). Used for registration and texture if
     * texture omitted.
     */
    public String id;
    /**
     * Texture name under assets/orbitalindustries/textures/blocks/. Defaults to id.
     */
    public String texture;
    /** Material: rock, sand, ground, wood, iron, clay, grass, etc. Default rock. */
    public String material;
    /** Block hardness. Default 1.5. */
    public Float hardness;
    /** Block resistance. Default 10.0. */
    public Float resistance;
    /**
     * Step sound: stone, gravel, wood, sand, grass, metal, glass, cloth, piston.
     * Default stone.
     */
    public String soundType;
    /** Harvest tool: pickaxe, axe, shovel, etc. Optional. */
    public String harvestTool;
    /** Harvest level (0 = wood, 1 = stone, 2 = iron, 3 = diamond). Default 0. */
    public Integer harvestLevel;
}
