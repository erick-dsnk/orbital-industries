package com.erickdsnk.orbitalindustries.registry;

import com.erickdsnk.orbitalindustries.core.BlockLoader;

/**
 * Central registration for mod blocks. Delegates to {@link BlockLoader} for
 * data-driven registration from {@code orbitalindustries/blocks.json}. New
 * blocks are added by editing the JSON; no code changes required.
 */
public final class BlockRegistry {

    public static void registerBlocks() {
        BlockLoader.loadBlocks();
    }
}
