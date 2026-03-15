package com.erickdsnk.orbitalindustries;

import com.erickdsnk.orbitalindustries.dimension.DimensionRegistry;
import com.erickdsnk.orbitalindustries.environment.EnvironmentManager;
import com.erickdsnk.orbitalindustries.environment.OxygenSystem;
import com.erickdsnk.orbitalindustries.environment.VacuumDamageHandler;
import com.erickdsnk.orbitalindustries.planet.PlanetManager;
import com.erickdsnk.orbitalindustries.planet.PlanetRegistry;
import com.erickdsnk.orbitalindustries.planet.biome.PlanetBiomeRegistry;
import com.erickdsnk.orbitalindustries.planet.structure.PlanetStructureRegistry;
import com.erickdsnk.orbitalindustries.space.AtmosphereManager;
import com.erickdsnk.orbitalindustries.space.GravityManager;
import com.erickdsnk.orbitalindustries.space.OrbitalEnvironmentManager;
import com.erickdsnk.orbitalindustries.transport.LaunchManager;
import com.erickdsnk.orbitalindustries.transport.TeleportManager;

/**
 * Holder for architecture singletons. Initialized during proxy preInit.
 */
public final class OrbitalIndustriesAPI {

    private OrbitalIndustriesAPI() {
    }

    public static DimensionRegistry dimensionRegistry;
    public static PlanetRegistry planetRegistry;
    public static PlanetManager planetManager;
    public static EnvironmentManager environmentManager;
    public static OrbitalEnvironmentManager orbitalEnvironmentManager;
    public static GravityManager gravityManager;
    public static AtmosphereManager atmosphereManager;
    public static LaunchManager launchManager;
    public static TeleportManager teleportManager;
    public static VacuumDamageHandler vacuumDamageHandler;
    public static OxygenSystem oxygenSystem;
    public static PlanetStructureRegistry structureRegistry;
    public static PlanetBiomeRegistry biomeRegistry;
}
