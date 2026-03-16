package com.erickdsnk.orbitalindustries.registry;

import com.erickdsnk.orbitalindustries.core.BlockLoader;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Creative inventory tab for Orbital Industries blocks and items. Uses the
 * first
 * registered block (moon_rock if present) as the tab icon.
 */
public final class CreativeTabOI extends CreativeTabs {

    /** Tab index: after vanilla tabs (0–11). */
    private static final int TAB_INDEX = 12;

    public static final CreativeTabs TAB = new CreativeTabOI();

    private CreativeTabOI() {
        super(TAB_INDEX, "orbitalindustries");
    }

    /** Icon for the creative tab (vanilla 1.7.10 expects Item). */
    @Override
    public Item getTabIconItem() {
        if (BlockLoader.getRegisteredBlocks() != null && !BlockLoader.getRegisteredBlocks().isEmpty()) {
            Block first = BlockLoader.getBlock("moon_rock");
            if (first == null) {
                first = BlockLoader.getRegisteredBlocks().values().iterator().next();
            }
            if (first != null) {
                return Item.getItemFromBlock(first);
            }
        }
        return null;
    }

    /** Icon as ItemStack (Forge may use this for display). */
    @Override
    public ItemStack getIconItemStack() {
        if (BlockLoader.getRegisteredBlocks() != null && !BlockLoader.getRegisteredBlocks().isEmpty()) {
            Block first = BlockLoader.getBlock("moon_rock");
            if (first == null) {
                first = BlockLoader.getRegisteredBlocks().values().iterator().next();
            }
            if (first != null) {
                return new ItemStack(first, 1, 0);
            }
        }
        return null;
    }
}
