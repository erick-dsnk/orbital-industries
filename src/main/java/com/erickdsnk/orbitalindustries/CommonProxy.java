package com.erickdsnk.orbitalindustries;

import com.erickdsnk.orbitalindustries.core.ConfigManager;
import com.erickdsnk.orbitalindustries.core.OIModLogger;
import com.erickdsnk.orbitalindustries.dimension.DimensionRegistry;
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
import com.erickdsnk.orbitalindustries.transport.LaunchManager;
import com.erickdsnk.orbitalindustries.transport.TeleportManager;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class CommonProxy {

    private static final OIModLogger LOG = new OIModLogger("CommonProxy");

    public void preInit(FMLPreInitializationEvent event) {
        ConfigManager.load(event.getSuggestedConfigurationFile());
        LOG.info("ConfigManager loaded");
        OrbitalIndustries.LOG.info(ConfigManager.getGreeting());
        OrbitalIndustries.LOG.info("I am OrbitalIndustries at version " + Tags.VERSION);

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
        OrbitalIndustriesAPI.vacuumDamageHandler = new VacuumDamageHandler();
        LOG.info("VacuumDamageHandler initialized");
        OrbitalIndustriesAPI.oxygenSystem = new OxygenSystemImpl(OrbitalIndustriesAPI.atmosphereManager);
        LOG.info("OxygenSystem initialized");
    }

    public void init(FMLInitializationEvent event) {
        // TODO: Register concrete dimensions via DimensionRegistry when planets/dimensions are defined
        LOG.info("DimensionRegistry ready");
        PacketHandler.registerPackets();
        LOG.info("PacketHandler ready");
    }

    public void postInit(FMLPostInitializationEvent event) {}

    public void serverStarting(FMLServerStartingEvent event) {}
}
