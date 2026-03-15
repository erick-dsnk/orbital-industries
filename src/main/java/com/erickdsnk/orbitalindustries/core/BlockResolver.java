package com.erickdsnk.orbitalindustries.core;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

/**
 * Resolves block names from JSON config (e.g. "minecraft:stone",
 * "minecraft:end_stone") to Block instances. Used when loading biomes and
 * other data-driven config.
 */
public final class BlockResolver {

    private static final OIModLogger LOG = new OIModLogger("BlockResolver");

    private BlockResolver() {
    }

    /**
     * Resolve a block by name. Accepts "modid:blockname" or "blockname".
     * Returns null if the name is null or empty.
     *
     * @param name block name (e.g. "minecraft:stone")
     * @return the Block, or null if not found
     */
    public static Block getBlockByName(String name) {
        return getBlockByName(name, null);
    }

    /**
     * Resolve a block by name, with fallback if not found.
     *
     * @param name     block name (e.g. "minecraft:stone")
     * @param fallback block to return when name is null, empty, or not found
     * @return the Block, or fallback if not found
     */
    public static Block getBlockByName(String name, Block fallback) {
        if (name == null || name.trim().isEmpty()) {
            return fallback;
        }
        String key = name.trim();
        Block block = Block.getBlockFromName(key);
        if (block == null && key.indexOf(':') > 0) {
            block = Block.getBlockFromName(key.substring(key.indexOf(':') + 1));
        }
        if (block == null) {
            LOG.warn("Unknown block name: '" + key + "', using fallback");
            return fallback;
        }
        return block;
    }
}
