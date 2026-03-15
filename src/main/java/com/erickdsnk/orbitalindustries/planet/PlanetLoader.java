package com.erickdsnk.orbitalindustries.planet;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import com.erickdsnk.orbitalindustries.OrbitalIndustriesAPI;
import com.erickdsnk.orbitalindustries.core.BlockResolver;
import com.erickdsnk.orbitalindustries.planet.biome.PlanetBiomeRegistry;
import com.erickdsnk.orbitalindustries.core.OIModLogger;
import com.erickdsnk.orbitalindustries.planet.biome.PlanetBiome;
import com.erickdsnk.orbitalindustries.planet.gen.PlanetTerrainRegistry;
import com.erickdsnk.orbitalindustries.planet.structure.StructureEntry;

import cpw.mods.fml.common.Loader;

/**
 * Loads planet definitions from JSON. Default dimension configs are read from
 * the mod resources ({@code orbitalindustries/planets/*.json}); users can
 * override or add planets by placing JSON files in
 * {@code config/orbitalindustries/planets/} (config dir takes precedence per
 * id).
 * Each JSON defines one planet: terrain generator (by id), biomes, structures,
 * and generator options. Terrain generator is created per planet via
 * {@link PlanetTerrainRegistry#createGenerator}.
 */
public final class PlanetLoader {

    private static final OIModLogger LOG = new OIModLogger("PlanetLoader");
    private static final String PLANETS_RESOURCE_PATH = "orbitalindustries/planets/";
    /** Default dimension JSONs shipped in the mod JAR (under resources). */
    private static final String[] DEFAULT_PLANET_RESOURCES = { "moon.json" };
    private static final double DEFAULT_GRAVITY = 0.16;
    private static final double DEFAULT_CHANCE_PER_CHUNK = 0.01;
    private static final Gson GSON = new Gson();

    /**
     * Load planet configs from resources first (defaults), then from config
     * directory (overrides/adds). Register each with {@link PlanetRegistry}.
     */
    public static void loadPlanets() {
        if (OrbitalIndustriesAPI.planetRegistry == null) {
            LOG.warn("PlanetRegistry not initialized; skipping planet load");
            return;
        }
        Map<String, PlanetConfig> byId = new LinkedHashMap<String, PlanetConfig>();

        // 1. Load defaults from mod resources
        for (String name : DEFAULT_PLANET_RESOURCES) {
            String path = PLANETS_RESOURCE_PATH + name;
            InputStream stream = PlanetLoader.class.getClassLoader().getResourceAsStream(path);
            if (stream == null) {
                LOG.warn("Default planet resource not found: " + path);
                continue;
            }
            try {
                Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
                PlanetConfig config = GSON.fromJson(reader, PlanetConfig.class);
                reader.close();
                if (config != null && config.id != null && !config.id.isEmpty()) {
                    byId.put(config.id, config);
                    LOG.info("Loaded default planet from resources: " + config.id);
                }
            } catch (JsonSyntaxException e) {
                LOG.warn("Invalid JSON in resource " + path + ": " + e.getMessage());
            } catch (IOException e) {
                LOG.warn("Could not read resource " + path + ": " + e.getMessage());
            }
        }

        // 2. Load from config directory (overrides or adds)
        File configDir = Loader.instance().getConfigDir();
        File planetsDir = new File(new File(configDir, "orbitalindustries"), "planets");
        if (planetsDir.exists()) {
            File[] files = planetsDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (!file.getName().toLowerCase().endsWith(".json")) {
                        continue;
                    }
                    try {
                        PlanetConfig config = GSON.fromJson(new FileReader(file), PlanetConfig.class);
                        if (config != null && config.id != null && !config.id.isEmpty()) {
                            byId.put(config.id, config);
                            LOG.info("Loaded planet from config: " + file.getName() + " (id=" + config.id + ")");
                        }
                    } catch (JsonSyntaxException e) {
                        LOG.warn("Invalid JSON in " + file.getName() + ": " + e.getMessage());
                    } catch (IOException e) {
                        LOG.warn("Could not read " + file.getName() + ": " + e.getMessage());
                    }
                }
            }
        }

        // 3. Register each planet
        Set<Integer> seenDimensions = new HashSet<Integer>();
        for (PlanetConfig config : byId.values()) {
            try {
                if (config.name == null || config.name.isEmpty()) {
                    config.name = config.id;
                }
                if (config.terrainGenerator == null || config.terrainGenerator.isEmpty()) {
                    LOG.warn("Skipping " + config.id + ": missing terrainGenerator");
                    continue;
                }
                List<PlanetBiome> biomes = resolveBiomes(config.biomes);
                Map<String, Object> options = config.generatorOptions == null
                        ? Collections.<String, Object>emptyMap()
                        : config.generatorOptions;
                PlanetTerrainGenerator generator = PlanetTerrainRegistry.createGenerator(
                        config.terrainGenerator, biomes, options);
                if (generator == null) {
                    LOG.warn("Skipping " + config.id + ": unknown terrainGenerator '" + config.terrainGenerator + "'");
                    continue;
                }
                if (seenDimensions.contains(config.dimensionId)) {
                    LOG.warn("Skipping " + config.id + ": duplicate dimensionId " + config.dimensionId);
                    continue;
                }
                double gravity = config.gravity != null ? config.gravity.doubleValue() : DEFAULT_GRAVITY;
                AtmosphereType atmosphere = parseAtmosphere(config.atmosphere);
                List<StructureEntry> structures = resolveStructures(config.structures);
                Planet planet = new Planet(config.id, config.name, config.dimensionId, config.terrainGenerator,
                        generator, gravity, atmosphere, 0.0, generator != null, biomes, structures);
                OrbitalIndustriesAPI.planetRegistry.registerPlanet(planet);
                seenDimensions.add(config.dimensionId);
                LOG.info("Registered planet: id=" + config.id + ", dimensionId=" + config.dimensionId);
            } catch (Exception e) {
                LOG.warn("Failed to register planet " + config.id + ": " + e.getMessage());
            }
        }
    }

    private static List<PlanetBiome> resolveBiomes(List<BiomeConfig> biomeConfigs) {
        if (biomeConfigs == null || biomeConfigs.isEmpty()) {
            return Collections.emptyList();
        }
        PlanetBiomeRegistry biomeRegistry = OrbitalIndustriesAPI.biomeRegistry;
        List<PlanetBiome> list = new ArrayList<PlanetBiome>();
        for (BiomeConfig b : biomeConfigs) {
            if (b.id == null || b.id.isEmpty()) {
                continue;
            }
            String displayName = b.displayName != null ? b.displayName : b.id;
            Block surface = BlockResolver.getBlockByName(b.surfaceBlock, Blocks.end_stone);
            Block stone = BlockResolver.getBlockByName(b.stoneBlock, Blocks.stone);
            double heightMod = b.terrainHeightModifier != null ? b.terrainHeightModifier.doubleValue() : 0.0;
            double craterMod = b.craterProbabilityModifier != null ? b.craterProbabilityModifier.doubleValue() : 1.0;
            int mcBiomeId = -1;
            if (biomeRegistry != null) {
                mcBiomeId = biomeRegistry.getOrRegister(b.id, displayName);
            } else if (b.minecraftBiomeId != null) {
                mcBiomeId = b.minecraftBiomeId.intValue();
            }
            list.add(new PlanetBiome(b.id, displayName, surface, stone, heightMod, craterMod, mcBiomeId));
        }
        return list.isEmpty() ? Collections.<PlanetBiome>emptyList() : Collections.unmodifiableList(list);
    }

    private static List<StructureEntry> resolveStructures(List<StructureConfig> structureConfigs) {
        if (structureConfigs == null || structureConfigs.isEmpty()) {
            return null;
        }
        List<StructureEntry> list = new ArrayList<StructureEntry>();
        for (StructureConfig s : structureConfigs) {
            if (s.type == null || s.type.isEmpty()) {
                continue;
            }
            double chance = s.chancePerChunk != null ? s.chancePerChunk.doubleValue() : DEFAULT_CHANCE_PER_CHUNK;
            Map<String, Object> params = s.params != null ? s.params : Collections.<String, Object>emptyMap();
            list.add(new StructureEntry(s.type, chance, params));
        }
        return list.isEmpty() ? null : Collections.unmodifiableList(list);
    }

    private static AtmosphereType parseAtmosphere(String s) {
        if (s == null || s.isEmpty()) {
            return AtmosphereType.NONE;
        }
        try {
            return AtmosphereType.valueOf(s.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return AtmosphereType.NONE;
        }
    }

    /** DTO for JSON parsing. */
    @SuppressWarnings("unused")
    private static class PlanetConfig {
        String id;
        String name;
        int dimensionId;
        String terrainGenerator;
        Double gravity;
        String atmosphere;
        Map<String, Object> generatorOptions;
        List<BiomeConfig> biomes;
        List<StructureConfig> structures;
    }

    @SuppressWarnings("unused")
    private static class BiomeConfig {
        String id;
        String displayName;
        String surfaceBlock;
        String stoneBlock;
        Double terrainHeightModifier;
        Double craterProbabilityModifier;
        Integer minecraftBiomeId;
    }

    @SuppressWarnings("unused")
    private static class StructureConfig {
        String type;
        Double chancePerChunk;
        Map<String, Object> params;
    }
}
