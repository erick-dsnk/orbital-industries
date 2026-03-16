package com.erickdsnk.orbitalindustries.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

/**
 * Generic block whose properties are defined by a {@link BlockDefinition}. Used
 * for data-driven block registration from JSON; one instance per definition.
 */
public class BlockFromDefinition extends Block {

    public BlockFromDefinition(BlockDefinition def) {
        super(parseMaterial(def.material));
        setStepSound(parseSoundType(def.soundType));
        setHardness(def.hardness != null ? def.hardness.floatValue() : 1.5f);
        setResistance(def.resistance != null ? def.resistance.floatValue() : 10.0f);
        if (def.harvestTool != null && !def.harvestTool.isEmpty()) {
            int level = def.harvestLevel != null ? def.harvestLevel.intValue() : 0;
            setHarvestLevel(def.harvestTool, level);
        }
    }

    private static Material parseMaterial(String s) {
        if (s == null || s.isEmpty())
            return Material.rock;
        String m = s.trim().toLowerCase();
        if ("sand".equals(m))
            return Material.sand;
        if ("ground".equals(m))
            return Material.ground;
        if ("wood".equals(m))
            return Material.wood;
        if ("iron".equals(m))
            return Material.iron;
        if ("clay".equals(m))
            return Material.clay;
        if ("grass".equals(m))
            return Material.grass;
        if ("glass".equals(m))
            return Material.glass;
        if ("ice".equals(m))
            return Material.ice;
        if ("rock".equals(m) || "stone".equals(m))
            return Material.rock;
        return Material.rock;
    }

    private static Block.SoundType parseSoundType(String s) {
        if (s == null || s.isEmpty())
            return Block.soundTypeStone;
        String t = s.trim().toLowerCase();
        if ("gravel".equals(t))
            return Block.soundTypeGravel;
        if ("wood".equals(t))
            return Block.soundTypeWood;
        if ("grass".equals(t))
            return Block.soundTypeGrass;
        if ("sand".equals(t))
            return Block.soundTypeSand;
        if ("cloth".equals(t))
            return Block.soundTypeCloth;
        if ("glass".equals(t))
            return Block.soundTypeGlass;
        if ("metal".equals(t))
            return Block.soundTypeMetal;
        if ("piston".equals(t))
            return Block.soundTypePiston;
        return Block.soundTypeStone;
    }
}
