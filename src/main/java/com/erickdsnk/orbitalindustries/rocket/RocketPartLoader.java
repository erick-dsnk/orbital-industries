package com.erickdsnk.orbitalindustries.rocket;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import com.erickdsnk.orbitalindustries.core.OIModLogger;

import cpw.mods.fml.common.Loader;

/**
 * Loads {@link RocketPartDefinition}s from JSON. Defaults from
 * {@code assets/orbitalindustries/rocket_parts/*.json}; overrides/adds from
 * {@code config/orbitalindustries/rocket_parts/*.json}.
 */
public final class RocketPartLoader {

    private static final OIModLogger LOG = new OIModLogger("RocketPartLoader");
    /** Classpath directory (no leading slash) listing part JSON files. */
    static final String ROCKET_PARTS_RESOURCE_DIR = "assets/orbitalindustries/rocket_parts";
    private static final Gson GSON = new Gson();

    /**
     * Merge defaults + config into one map, then register all with the registry.
     * Does not clear the registry; {@link RocketPartRegistry#loadAll()} does that
     * first.
     */
    public static void loadInto(RocketPartRegistry registry) {
        if (registry == null) {
            LOG.warn("RocketPartRegistry is null; skipping rocket part load");
            return;
        }
        Map<String, RocketPartDefinition> merged = new LinkedHashMap<String, RocketPartDefinition>();

        for (String resourcePath : listDefaultJsonResourcePaths()) {
            RocketPartDefinition def = readDefinitionFromClasspath(resourcePath);
            if (def != null && def.getId() != null && !def.getId().isEmpty()) {
                merged.put(def.getId(), def);
                LOG.info("Loaded default rocket part: " + def.getId());
            }
        }

        loadFromConfigDir(merged);

        for (RocketPartDefinition def : merged.values()) {
            try {
                normalizeType(def);
                registry.register(def);
            } catch (Exception e) {
                LOG.warn("Failed to register rocket part " + def.getId() + ": " + e.getMessage());
            }
        }
    }

    private static void normalizeType(RocketPartDefinition def) {
        if (def.getType() == null) {
            def.setType(RocketPartType.HULL);
        }
    }

    private static List<String> listDefaultJsonResourcePaths() {
        List<String> out = new ArrayList<String>();
        ClassLoader cl = RocketPartLoader.class.getClassLoader();
        URL url = cl.getResource(ROCKET_PARTS_RESOURCE_DIR);
        if (url == null) {
            LOG.warn("Rocket parts resource directory not found: " + ROCKET_PARTS_RESOURCE_DIR);
            return out;
        }
        try {
            if ("file".equals(url.getProtocol())) {
                File folder = new File(url.toURI());
                File[] files = folder.listFiles();
                if (files != null) {
                    for (File f : files) {
                        if (f.isFile() && f.getName().endsWith(".json")) {
                            out.add(ROCKET_PARTS_RESOURCE_DIR + "/" + f.getName());
                        }
                    }
                }
            } else if ("jar".equals(url.getProtocol())) {
                JarURLConnection conn = (JarURLConnection) url.openConnection();
                JarFile jar = conn.getJarFile();
                String prefix = ROCKET_PARTS_RESOURCE_DIR + "/";
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry e = entries.nextElement();
                    String name = e.getName();
                    if (name.startsWith(prefix) && name.endsWith(".json") && !e.isDirectory()) {
                        if (name.indexOf('/', prefix.length()) < 0) {
                            out.add(name);
                        }
                    }
                }
            } else {
                LOG.warn("Unsupported rocket parts URL protocol: " + url.getProtocol());
            }
        } catch (Exception e) {
            LOG.warn("Could not list rocket parts in " + ROCKET_PARTS_RESOURCE_DIR + ": "
                    + e.getMessage());
        }
        Collections.sort(out);
        return out;
    }

    private static RocketPartDefinition readDefinitionFromClasspath(String resourcePath) {
        InputStream stream = RocketPartLoader.class.getClassLoader().getResourceAsStream(resourcePath);
        if (stream == null) {
            LOG.warn("Rocket part resource not found: " + resourcePath);
            return null;
        }
        try {
            Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
            try {
                RocketPartDefinition def = GSON.fromJson(reader, RocketPartDefinition.class);
                return def;
            } finally {
                reader.close();
            }
        } catch (JsonSyntaxException e) {
            LOG.warn("Invalid JSON in resource " + resourcePath + ": " + e.getMessage());
            return null;
        } catch (IOException e) {
            LOG.warn("Could not read resource " + resourcePath + ": " + e.getMessage());
            return null;
        }
    }

    private static void loadFromConfigDir(Map<String, RocketPartDefinition> merged) {
        File configDir = Loader.instance().getConfigDir();
        File partsDir = new File(new File(configDir, "orbitalindustries"), "rocket_parts");
        if (!partsDir.exists()) {
            return;
        }
        File[] files = partsDir.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (!file.getName().toLowerCase().endsWith(".json")) {
                continue;
            }
            try {
                Reader reader = new FileReader(file);
                try {
                    RocketPartDefinition def = GSON.fromJson(reader, RocketPartDefinition.class);
                    if (def != null && def.getId() != null && !def.getId().isEmpty()) {
                        merged.put(def.getId(), def);
                        LOG.info("Loaded rocket part from config: " + file.getName() + " (id="
                                + def.getId() + ")");
                    }
                } finally {
                    reader.close();
                }
            } catch (JsonSyntaxException e) {
                LOG.warn("Invalid JSON in " + file.getName() + ": " + e.getMessage());
            } catch (IOException e) {
                LOG.warn("Could not read " + file.getName() + ": " + e.getMessage());
            }
        }
    }
}
