package com.erickdsnk.orbitalindustries.core;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.erickdsnk.orbitalindustries.block.BlockDefinition;
import com.erickdsnk.orbitalindustries.block.BlockFromDefinition;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;

/**
 * Data-driven block registration. Loads block definitions from
 * {@code orbitalindustries/blocks.json} in resources (defaults) and
 * {@code config/orbitalindustries/blocks.json} (overrides/adds). Each
 * definition
 * creates one block instance registered with GameRegistry. New blocks can be
 * added by editing the JSON only.
 */
public final class BlockLoader {

    private static final OIModLogger LOG = new OIModLogger("BlockLoader");
    private static final String BLOCKS_RESOURCE_PATH = "orbitalindustries/blocks.json";
    private static final String BLOCKS_CONFIG_NAME = "blocks.json";
    private static final String TEXTURE_DOMAIN = "orbitalindustries";

    private static final com.google.gson.Gson GSON = new com.google.gson.Gson();

    private static Map<String, Block> blocksById = Collections.emptyMap();

    private BlockLoader() {
    }

    /**
     * Load block definitions from JSON, create and register blocks. Call during
     * preInit after nothing else depends on block registry. Ids are
     * case-sensitive.
     */
    public static void loadBlocks() {
        Map<String, BlockDefinition> byId = new LinkedHashMap<String, BlockDefinition>();

        // 1. Load defaults from mod resources
        InputStream stream = BlockLoader.class.getClassLoader().getResourceAsStream(BLOCKS_RESOURCE_PATH);
        if (stream != null) {
            try {
                Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
                BlocksFile file = GSON.fromJson(reader, BlocksFile.class);
                reader.close();
                if (file != null && file.blocks != null) {
                    for (BlockDefinition def : file.blocks) {
                        if (def != null && def.id != null && !def.id.trim().isEmpty()) {
                            byId.put(def.id.trim(), def);
                        }
                    }
                    LOG.info("Loaded " + file.blocks.size() + " block definition(s) from resources");
                }
            } catch (com.google.gson.JsonSyntaxException e) {
                LOG.warn("Invalid JSON in " + BLOCKS_RESOURCE_PATH + ": " + e.getMessage());
            } catch (IOException e) {
                LOG.warn("Could not read " + BLOCKS_RESOURCE_PATH + ": " + e.getMessage());
            }
        } else {
            LOG.warn("Default blocks resource not found: " + BLOCKS_RESOURCE_PATH);
        }

        // 2. Load from config (overrides or adds)
        File configDir = Loader.instance().getConfigDir();
        File blocksFile = new File(new File(configDir, "orbitalindustries"), BLOCKS_CONFIG_NAME);
        if (blocksFile.exists()) {
            try {
                BlocksFile file = GSON.fromJson(new FileReader(blocksFile), BlocksFile.class);
                if (file != null && file.blocks != null) {
                    for (BlockDefinition def : file.blocks) {
                        if (def != null && def.id != null && !def.id.trim().isEmpty()) {
                            byId.put(def.id.trim(), def);
                        }
                    }
                    LOG.info("Loaded block definitions from config: " + blocksFile.getAbsolutePath());
                }
            } catch (com.google.gson.JsonSyntaxException e) {
                LOG.warn("Invalid JSON in " + blocksFile.getName() + ": " + e.getMessage());
            } catch (IOException e) {
                LOG.warn("Could not read " + blocksFile.getName() + ": " + e.getMessage());
            }
        }

        // 3. Register each block
        Map<String, Block> registered = new LinkedHashMap<String, Block>();
        for (Map.Entry<String, BlockDefinition> e : byId.entrySet()) {
            String id = e.getKey();
            BlockDefinition def = e.getValue();
            def.id = id;
            if (def.texture == null || def.texture.isEmpty()) {
                def.texture = id;
            }
            Block block = new BlockFromDefinition(def);
            block.setBlockName("orbitalindustries." + id);
            block.setBlockTextureName(TEXTURE_DOMAIN + ":" + def.texture);
            GameRegistry.registerBlock(block, id);
            registered.put(id, block);
        }
        blocksById = Collections.unmodifiableMap(registered);
        LOG.info("Registered " + blocksById.size() + " block(s) from definitions");
    }

    /**
     * Returns the block registered for the given id, or null if none. Id
     * should be the definition id (e.g. "moon_rock"), not the full
     * "OrbitalIndustries:moon_rock" form.
     */
    public static Block getBlock(String id) {
        return id != null ? blocksById.get(id) : null;
    }

    /**
     * Returns an unmodifiable map of all blocks registered from definitions, by id.
     */
    public static Map<String, Block> getRegisteredBlocks() {
        return blocksById;
    }

    @SuppressWarnings("unused")
    private static class BlocksFile {
        List<BlockDefinition> blocks;
    }
}
