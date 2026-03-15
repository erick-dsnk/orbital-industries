package com.erickdsnk.orbitalindustries.planet.structure;

import java.util.Collections;
import java.util.Map;

/**
 * Immutable config for one structure type in a dimension's structure list.
 * Loaded from JSON (type, chancePerChunk, and optional extra params).
 */
public final class StructureEntry {

    private final String type;
    private final double chancePerChunk;
    private final Map<String, Object> params;

    public StructureEntry(String type, double chancePerChunk) {
        this(type, chancePerChunk, null);
    }

    public StructureEntry(String type, double chancePerChunk, Map<String, Object> params) {
        this.type = type == null ? "" : type;
        this.chancePerChunk = chancePerChunk;
        this.params = params == null || params.isEmpty()
                ? Collections.<String, Object>emptyMap()
                : Collections.unmodifiableMap(params);
    }

    public String getType() {
        return type;
    }

    public double getChancePerChunk() {
        return chancePerChunk;
    }

    public Map<String, Object> getParams() {
        return params;
    }
}
