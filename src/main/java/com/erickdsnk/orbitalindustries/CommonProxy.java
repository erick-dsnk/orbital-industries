package com.erickdsnk.orbitalindustries;

import com.erickdsnk.orbitalindustries.core.ConfigManager;
import com.erickdsnk.orbitalindustries.core.OIModLogger;
import com.erickdsnk.orbitalindustries.dimension.DimensionRegistry;
import com.erickdsnk.orbitalindustries.environment.EnvironmentManager;
import com.erickdsnk.orbitalindustries.environment.VacuumDamageHandler;
import com.erickdsnk.orbitalindustries.environment.impl.OxygenSystemImpl;
import com.erickdsnk.orbitalindustries.network.PacketHandler;
import com.erickdsnk.orbitalindustries.planet.PlanetManager;
import com.erickdsnk.orbitalindustries.planet.PlanetRegistry;
import com.erickdsnk.orbitalindustries.registry.BlockRegistry;
import com.erickdsnk.orbitalindustries.registry.EntityRegistry;
import com.erickdsnk.orbitalindustries.registry.ItemRegistry;
import com.erickdsnk.orbitalindustries.space.impl.AtmosphereManagerImpl;
import com.erickdsnk.orbitalindustries.space.impl.GravityManagerImpl;
import com.erickdsnk.orbitalindustries.space.impl.OrbitalEnvironmentManagerImpl;
import com.erickdsnk.orbitalindustries.transport.CommandMoon;
import com.erickdsnk.orbitalindustries.transport.CommandOrbit;
import com.erickdsnk.orbitalindustries.transport.LaunchManager;
import com.erickdsnk.orbitalindustries.transport.TeleportManager;
import com.erickdsnk.orbitalindustries.dimension.MoonWorldProvider;
import com.erickdsnk.orbitalindustries.dimension.OrbitWorldProvider;
import com.erickdsnk.orbitalindustries.planet.gen.MoonTerrainGenerator;
import com.erickdsnk.orbitalindustries.planet.AtmosphereType;
import com.erickdsnk.orbitalindustries.planet.Planet;
import com.erickdsnk.orbitalindustries.planet.biome.PlanetBiome;

import java.util.Arrays;
import java.util.List;
import com.erickdsnk.orbitalindustries.space.GravityTickHandler;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class CommonProxy {

    private static final OIModLogger LOG = new OIModLogger("CommonProxy");

    public void preInit(FMLPreInitializationEvent event) {
        ConfigManager.load(event.getSuggestedConfigurationFile());
        LOG.info("ConfigManager loaded");
        LOG.info(ConfigManager.getGreeting());
        LOG.info("I am OrbitalIndustries at version " + Tags.VERSION);

        BlockRegistry.registerBlocks();
        LOG.info("BlockRegistry initialized");
        ItemRegistry.registerItems();
        LOG.info("ItemRegistry initialized");
        EntityRegistry.registerEntities();
        LOG.info("EntityRegistry initialized");

        OrbitalIndustriesAPI.dimensionRegistry = new DimensionRegistry();
        LOG.info("DimensionRegistry initialized");
        OrbitalIndustriesAPI.planetRegistry = new PlanetRegistry();
        LOG.info("PlanetRegistry initialized");
        OrbitalIndustriesAPI.planetManager = new PlanetManager(OrbitalIndustriesAPI.planetRegistry);
        LOG.info("PlanetManager initialized");
        OrbitalIndustriesAPI.environmentManager = new EnvironmentManager(OrbitalIndustriesAPI.planetManager);
        LOG.info("EnvironmentManager initialized");
        OrbitalIndustriesAPI.atmosphereManager = new AtmosphereManagerImpl(OrbitalIndustriesAPI.planetManager);
        LOG.info("AtmosphereManager initialized");
        OrbitalIndustriesAPI.gravityManager = new GravityManagerImpl(OrbitalIndustriesAPI.planetManager);
        LOG.info("GravityManager initialized");
        OrbitalIndustriesAPI.orbitalEnvironmentManager = new OrbitalEnvironmentManagerImpl();
        LOG.info("OrbitalEnvironmentManager initialized");
        OrbitalIndustriesAPI.launchManager = new LaunchManager();
        LOG.info("LaunchManager initialized");
        OrbitalIndustriesAPI.teleportManager = new TeleportManager();
        LOG.info("TeleportManager initialized");
        OrbitalIndustriesAPI.vacuumDamageHandler = new VacuumDamageHandler(OrbitalIndustriesAPI.environmentManager);
        LOG.info("VacuumDamageHandler initialized");
        OrbitalIndustriesAPI.oxygenSystem = new OxygenSystemImpl(OrbitalIndustriesAPI.atmosphereManager);
        LOG.info("OxygenSystem initialized");
    }

    public void init(FMLInitializationEvent event) {
        // Init: register overworld (Earth) and orbit dimension + planet; gravity tick
        // handler.
        // Init order ensures dimensions and gravity are ready for travel and
        // environment
        // systems (planets, moons, orbital stations).
        OrbitalIndustriesAPI.planetRegistry.register(
                new Planet("earth", "Earth", 0, 1.0, AtmosphereType.BREATHABLE, 1.0, true, null, null));

        int orbitId = ConfigManager.getOrbitDimensionId();
        OrbitalIndustriesAPI.dimensionRegistry.registerDimension(orbitId, OrbitWorldProvider.class);
        LOG.info("Registered dimension orbit with ID " + orbitId);

        OrbitalIndustriesAPI.planetRegistry.register(
                new Planet("orbit", "Orbit", orbitId, 0.1, AtmosphereType.NONE, 0.0, false, null, null));
        if (OrbitalIndustriesAPI.orbitalEnvironmentManager instanceof OrbitalEnvironmentManagerImpl) {
            ((OrbitalEnvironmentManagerImpl) OrbitalIndustriesAPI.orbitalEnvironmentManager)
                    .registerSpaceDimension(orbitId);
        }

        Planet earth = OrbitalIndustriesAPI.planetRegistry.getById("earth");
        int moonId = ConfigManager.getMoonDimensionId();
        OrbitalIndustriesAPI.dimensionRegistry.registerDimension(moonId, MoonWorldProvider.class);
        LOG.info("Moon dimension registered with ID " + moonId);
        List<PlanetBiome> moonBiomes = Arrays.<PlanetBiome>asList(
                MoonTerrainGenerator.CRATERED_HIGHLANDS,
                MoonTerrainGenerator.SMOOTH_PLAINS);
        OrbitalIndustriesAPI.planetRegistry.register(
                new Planet("moon", "Moon", moonId, 0.16, AtmosphereType.NONE, 0.25, true, earth,
                        new MoonTerrainGenerator(moonBiomes), moonBiomes));
        LOG.info("Moon planet registered with dimension id " + moonId);

        GravityTickHandler gravityHandler = new GravityTickHandler();
        FMLCommonHandler.instance().bus().register(gravityHandler);
        MinecraftForge.EVENT_BUS.register(gravityHandler);
        LOG.info("Gravity tick handler registered");

        FMLCommonHandler.instance().bus().register(OrbitalIndustriesAPI.vacuumDamageHandler);
        MinecraftForge.EVENT_BUS.register(OrbitalIndustriesAPI.vacuumDamageHandler);
        LOG.info("Vacuum damage handler registered");

        PacketHandler.registerPackets();
        LOG.info("PacketHandler ready");
    }

    public void postInit(FMLPostInitializationEvent event) {
        int orbitId = ConfigManager.getOrbitDimensionId();
        boolean dimOk = net.minecraftforge.common.DimensionManager.isDimensionRegistered(orbitId);
        double gravity = OrbitalIndustriesAPI.gravityManager.getGravityMultiplier(orbitId);
        if (dimOk && Math.abs(gravity - 0.1) < 0.001) {
            LOG.info("Space dimension framework validated: orbit dim " + orbitId + ", gravity " + gravity);
        }
    }

    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandOrbit());
        event.registerServerCommand(new CommandMoon());
    }
}
