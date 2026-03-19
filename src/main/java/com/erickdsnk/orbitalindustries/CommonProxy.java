package com.erickdsnk.orbitalindustries;

import com.erickdsnk.orbitalindustries.core.ConfigManager;
import com.erickdsnk.orbitalindustries.core.OIModLogger;
import com.erickdsnk.orbitalindustries.dimension.DimensionRegistry;
import com.erickdsnk.orbitalindustries.dimension.OrbitWorldProvider;
import com.erickdsnk.orbitalindustries.environment.EnvironmentManager;
import com.erickdsnk.orbitalindustries.environment.VacuumDamageHandler;
import com.erickdsnk.orbitalindustries.environment.impl.OxygenSystemImpl;
import com.erickdsnk.orbitalindustries.gui.GuiHandler;
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
import com.erickdsnk.orbitalindustries.transport.CommandOverworld;
import com.erickdsnk.orbitalindustries.transport.LaunchManager;
import com.erickdsnk.orbitalindustries.transport.SpaceNavigationSystem;
import com.erickdsnk.orbitalindustries.transport.TeleportManager;
import com.erickdsnk.orbitalindustries.planet.gen.ModularTerrainGenerator;
import com.erickdsnk.orbitalindustries.planet.gen.NoisySurfaceTerrainGenerator;
import com.erickdsnk.orbitalindustries.planet.gen.PlanetTerrainGeneratorFactory;
import com.erickdsnk.orbitalindustries.planet.gen.PlanetTerrainRegistry;
import com.erickdsnk.orbitalindustries.planet.gen.feature.CaveFeature;
import com.erickdsnk.orbitalindustries.planet.gen.feature.CraterFeature;
import com.erickdsnk.orbitalindustries.planet.gen.feature.TerrainFeatureRegistry;
import com.erickdsnk.orbitalindustries.planet.PlanetLoader;
import com.erickdsnk.orbitalindustries.planet.PlanetTerrainGenerator;
import com.erickdsnk.orbitalindustries.planet.biome.PlanetBiome;
import com.erickdsnk.orbitalindustries.planet.biome.PlanetBiomeRegistry;
import com.erickdsnk.orbitalindustries.planet.structure.NoOpStructureGenerator;
import com.erickdsnk.orbitalindustries.planet.structure.PlanetStructureRegistry;
import com.erickdsnk.orbitalindustries.rocket.RocketPartRegistry;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import com.erickdsnk.orbitalindustries.planet.AtmosphereType;
import com.erickdsnk.orbitalindustries.planet.Planet;
import com.erickdsnk.orbitalindustries.world.dimension.PlanetDimensionProvider;

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
        OrbitalIndustriesAPI.structureRegistry = new PlanetStructureRegistry();
        LOG.info("StructureRegistry initialized");
        OrbitalIndustriesAPI.biomeRegistry = new PlanetBiomeRegistry();
        LOG.info("BiomeRegistry initialized");
        OrbitalIndustriesAPI.rocketPartRegistry = new RocketPartRegistry();
        LOG.info("RocketPartRegistry initialized");
    }

    public void init(FMLInitializationEvent event) {
        // 1. Register terrain features (before terrain generator factories)
        TerrainFeatureRegistry.register("craters", new CraterFeature());
        TerrainFeatureRegistry.register("caves", new CaveFeature());
        LOG.info("Terrain features registered");

        // 2. Register terrain generator factories (before loading planet JSON).
        // Planet JSONs supply terrainGenerator id and generatorOptions (including
        // "features"); createGenerator receives those options when planets are loaded.
        PlanetTerrainRegistry.registerFactory("noisy_surface", new PlanetTerrainGeneratorFactory() {
            @Override
            public PlanetTerrainGenerator create(List<PlanetBiome> biomes, Map<String, Object> options) {
                NoisySurfaceTerrainGenerator base = new NoisySurfaceTerrainGenerator(biomes, options);
                List<String> featureIds = ModularTerrainGenerator.parseFeatureIds(options);
                if (featureIds.isEmpty()) {
                    return base;
                }
                return new ModularTerrainGenerator(base, featureIds, biomes, options);
            }
        });
        // Backward compat: "moon" = noisy_surface with default features when not in
        // options
        PlanetTerrainRegistry.registerFactory("moon", new PlanetTerrainGeneratorFactory() {
            @Override
            public PlanetTerrainGenerator create(List<PlanetBiome> biomes, Map<String, Object> options) {
                NoisySurfaceTerrainGenerator base = new NoisySurfaceTerrainGenerator(biomes, options);
                List<String> featureIds = ModularTerrainGenerator.parseFeatureIds(options);
                if (featureIds.isEmpty()) {
                    featureIds = Arrays.asList("craters", "caves");
                }
                return new ModularTerrainGenerator(base, featureIds, biomes, options);
            }
        });
        LOG.info("Terrain generator factories registered");

        // 3. Register structure types (placeholders; implement real generators as
        // needed)
        OrbitalIndustriesAPI.structureRegistry.register("abandoned_shelter", new NoOpStructureGenerator());
        LOG.info("Structure types registered");

        OrbitalIndustriesAPI.rocketPartRegistry.loadAll();
        LOG.info("Rocket parts loaded");

        // 3. Load planet definitions from config/orbitalindustries/planets/
        PlanetLoader.loadPlanets();
        LOG.info("Planet configs loaded");

        // 5. Register Earth in registry (not from JSON)
        OrbitalIndustriesAPI.planetRegistry.register(new Planet("earth", "Earth", 0, null, null, 1.0,
                AtmosphereType.BREATHABLE, 1.0, true));

        OrbitalIndustriesAPI.spaceNavigationSystem = new SpaceNavigationSystem(OrbitalIndustriesAPI.planetRegistry);
        LOG.info("SpaceNavigationSystem initialized");

        // 6. Register dimensions for all planets that have a terrain generator
        // (data-driven)
        for (Planet planet : OrbitalIndustriesAPI.planetRegistry.getPlanets()) {
            if (planet.getDimensionId() != 0 && planet.getTerrainGenerator() != null) {
                OrbitalIndustriesAPI.dimensionRegistry.registerDimension(planet.getDimensionId(),
                        PlanetDimensionProvider.class);
                LOG.info("Registered planet dimension: " + planet.getId() + " (dim " + planet.getDimensionId() + ")");
            }
        }

        // 7. Register orbit dimension (void space for rocket transfers)
        int orbitDimId = ConfigManager.getOrbitDimensionId();
        OrbitalIndustriesAPI.dimensionRegistry.registerDimension(orbitDimId, OrbitWorldProvider.class);
        LOG.info("Registered orbit dimension: " + orbitDimId);

        GravityTickHandler gravityHandler = new GravityTickHandler();
        FMLCommonHandler.instance().bus().register(gravityHandler);
        MinecraftForge.EVENT_BUS.register(gravityHandler);
        LOG.info("Gravity tick handler registered");

        FMLCommonHandler.instance().bus().register(OrbitalIndustriesAPI.vacuumDamageHandler);
        MinecraftForge.EVENT_BUS.register(OrbitalIndustriesAPI.vacuumDamageHandler);
        LOG.info("Vacuum damage handler registered");

        PacketHandler.registerPackets();
        LOG.info("PacketHandler ready");
        cpw.mods.fml.common.network.NetworkRegistry.INSTANCE.registerGuiHandler(OrbitalIndustries.instance,
                new GuiHandler());
        LOG.info("GuiHandler registered");
    }

    public void postInit(FMLPostInitializationEvent event) {

    }

    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandMoon());
        event.registerServerCommand(new CommandOverworld());
    }
}
