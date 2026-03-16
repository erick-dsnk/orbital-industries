package com.erickdsnk.orbitalindustries.registry;

import com.erickdsnk.orbitalindustries.block.rocket.RocketAssemblerBlock;
import com.erickdsnk.orbitalindustries.core.BlockLoader;
import com.erickdsnk.orbitalindustries.tile.rocket.RocketAssemblerTileEntity;

import cpw.mods.fml.common.registry.GameRegistry;

/**
 * Central registration for mod blocks. Delegates to {@link BlockLoader} for
 * data-driven registration from {@code orbitalindustries/blocks.json}; also
 * registers code-only blocks (e.g. Rocket Assembler).
 */
public final class BlockRegistry {

    public static RocketAssemblerBlock rocketAssemblerBlock;

    public static void registerBlocks() {
        BlockLoader.loadBlocks();
        rocketAssemblerBlock = new RocketAssemblerBlock();
        GameRegistry.registerBlock(rocketAssemblerBlock, "rocket_assembler");
        GameRegistry.registerTileEntity(RocketAssemblerTileEntity.class, "OrbitalIndustries:RocketAssemblerTileEntity");
    }
}
