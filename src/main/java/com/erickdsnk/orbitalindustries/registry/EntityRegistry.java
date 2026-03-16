package com.erickdsnk.orbitalindustries.registry;

import com.erickdsnk.orbitalindustries.OrbitalIndustries;
import com.erickdsnk.orbitalindustries.rocket.EntityRocket;

/**
 * Central registration for mod entities. Register with FML EntityRegistry
 * during preInit.
 */
public final class EntityRegistry {

    private static final int ROCKET_ENTITY_ID = 0;
    private static final int TRACKING_RANGE = 64;
    private static final int UPDATE_FREQUENCY = 2;

    public static void registerEntities() {
        if (OrbitalIndustries.instance != null) {
            cpw.mods.fml.common.registry.EntityRegistry.registerModEntity(EntityRocket.class, "Rocket",
                    ROCKET_ENTITY_ID,
                    OrbitalIndustries.instance, TRACKING_RANGE, UPDATE_FREQUENCY, true);
        }
    }
}
