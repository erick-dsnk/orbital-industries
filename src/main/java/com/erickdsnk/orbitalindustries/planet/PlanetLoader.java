package com.erickdsnk.orbitalindustries.planet;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import com.erickdsnk.orbitalindustries.OrbitalIndustriesAPI;
import com.erickdsnk.orbitalindustries.core.BlockResolver;
import com.erickdsnk.orbitalindustries.core.OIModLogger;
import com.erickdsnk.orbitalindustries.planet.biome.PlanetBiome;
import com.erickdsnk.orbitalindustries.planet.gen.PlanetTerrainRegistry;
import com.erickdsnk.orbitalindustries.planet.structure.StructureEntry;

import cpw.mods.fml.common.Loader;

/**
 * Loads planet definitions from JSON files in
 * config/orbitalindustries/planets/.
 * Each JSON file defines one planet: terrain generator (by id), biomes,
 * structures, and generator options. Terrain generator is created per planet
 * via {@link PlanetTerrainRegistry#createGenerator}.
 */
public final class PlanetLoader {

    private static final OIModLogger LOG = new OIModLogger("PlanetLoader");
    private static final double DEFAULT_GRAVITY = 0.16;
    private static final double DEFAULT_CHANCE_PER_CHUNK = 0.01;
    private static final Gson GSON = new Gson();

    /**
     * Scan config/orbitalindustries/planets/, parse each JSON file, resolve
     * biomes (block names), create terrain generator via factory, and register
     * planets with {@link PlanetRegistry}.
     */
    public static void loadPlanets() {
        if (OrbitalIndustriesAPI.planetRegistry == null) {
            LOG.warn("PlanetRegistry not initialized; skipping planet load");
            return;
        }
        File configDir = Loader.instance().getConfigDir();
        File planetsDir = new File(new File(configDir, "orbitalindustries"), "planets");
        if (!planetsDir.exists()) {
            if (!planetsDir.mkdirs()) {
                LOG.warn("Could not create planets config dir: " + planetsDir.getAbsolutePath());
                return;
            }
        }
        writeDefaultMoonJsonIfMissing(planetsDir);
        File[] files = planetsDir.listFiles();
        if (files == null) {
            return;
        }
        Set<String> seenIds = new HashSet<String>();
        Set<Integer> seenDimensions = new HashSet<Integer>();
        for (File file : files) {
            if (!file.getName().toLowerCase().endsWith(".json")) {
                continue;
            }
            try {
                PlanetConfig config = GSON.fromJson(new FileReader(file), PlanetConfig.class);
                if (config == null || config.id == null || config.id.isEmpty()) {
                    LOG.warn("Skipping " + file.getName() + ": missing or empty id");
                    continue;
                }
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
                if (seenIds.contains(config.id)) {
                    LOG.warn("Skipping " + file.getName() + ": duplicate planet id " + config.id);
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
                seenIds.add(config.id);
                seenDimensions.add(config.dimensionId);
                LOG.info("Loaded planet from " + file.getName() + ": id=" + config.id + ", dimensionId="
                        + config.dimensionId);
            } catch (JsonSyntaxException e) {
                LOG.warn("Invalid JSON in " + file.getName() + ": " + e.getMessage());
            } catch (IOException e) {
                LOG.warn("Could not read " + file.getName() + ": " + e.getMessage());
            }
        }
    }

    private static List<PlanetBiome> resolveBiomes(List<BiomeConfig> biomeConfigs) {
        if (biomeConfigs == null || biomeConfigs.isEmpty()) {
            return Collections.emptyList();
        }
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
            int mcBiomeId = b.minecraftBiomeId != null ? b.minecraftBiomeId.intValue() : -1;
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

    private static void writeDefaultMoonJsonIfMissing(File planetsDir) {
        File moonJson = new File(planetsDir, "moon.json");
        if (moonJson.exists()) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"id\": \"moon\",\n");
        sb.append("  \"name\": \"Moon\",\n");
        sb.append("  \"dimensionId\": -40,\n");
        sb.append("  \"terrainGenerator\": \"moon\",\n");
        sb.append("  \"gravity\": 0.16,\n");
        sb.append("  \"atmosphere\": \"NONE\",\n");
        sb.append("  \"generatorOptions\": {\n");
        sb.append("    \"baseSurfaceY\": 64,\n");
        sb.append("    \"craterChancePerChunk\": 3\n");
        sb.append("  },\n");
        sb.append("  \"biomes\": [\n");
        sb.append("    {\n");
        sb.append("      \"id\": \"moon_default\",\n");
        sb.append("      \"displayName\": \"Lunar Surface\",\n");
        sb.append("      \"surfaceBlock\": \"minecraft:end_stone\",\n");
        sb.append("      \"stoneBlock\": \"minecraft:stone\",\n");
        sb.append("      \"terrainHeightModifier\": 0,\n");
        sb.append("      \"craterProbabilityModifier\": 1.0\n");
        sb.append("    }\n");
        sb.append("  ],\n");
        sb.append("  \"structures\": []\n");
        sb.append("}\n");
        try {
            FileWriter w = new FileWriter(moonJson);
            w.write(sb.toString());
            w.close();
            LOG.info("Created default config/orbitalindustries/planets/moon.json");
        } catch (IOException e) {
            LOG.warn("Could not write default moon.json: " + e.getMessage());
        }
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
