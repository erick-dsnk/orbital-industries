package com.erickdsnk.orbitalindustries.registry;

import com.erickdsnk.orbitalindustries.item.rocket.ItemRocket;
import com.erickdsnk.orbitalindustries.item.rocket.ItemRocketPart;

import cpw.mods.fml.common.registry.GameRegistry;

/**
 * Central registration for mod items. Register with GameRegistry during
 * preInit.
 */
public final class ItemRegistry {

    public static ItemRocketPart itemRocketPart;
    public static ItemRocket itemRocket;

    public static void registerItems() {
        itemRocketPart = new ItemRocketPart();
        GameRegistry.registerItem(itemRocketPart, "rocket_part");
        itemRocket = new ItemRocket();
        GameRegistry.registerItem(itemRocket, "rocket");
    }
}
