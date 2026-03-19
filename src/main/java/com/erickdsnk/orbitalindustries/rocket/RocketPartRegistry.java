package com.erickdsnk.orbitalindustries.rocket;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;

import com.erickdsnk.orbitalindustries.item.rocket.ItemRocketPart;
import com.erickdsnk.orbitalindustries.rocket.part.DataDrivenRocketPart;

/**
 * Registry of rocket parts by id. Populated from JSON via
 * {@link RocketPartLoader};
 * modpack creators can add or override parts via config.
 */
public final class RocketPartRegistry {

    private final Map<String, RocketPartDefinition> definitions = new LinkedHashMap<String, RocketPartDefinition>();
    private final Map<String, RocketPart> parts = new LinkedHashMap<String, RocketPart>();

    /**
     * Load default assets and config overrides into this registry.
     */
    public void loadAll() {
        definitions.clear();
        parts.clear();
        RocketPartLoader.loadInto(this);
    }

    /**
     * Register a part from its definition. Replaces any existing entry with the
     * same
     * id.
     */
    public void register(RocketPartDefinition def) {
        if (def == null || def.getId() == null || def.getId().isEmpty()) {
            return;
        }
        if (def.getType() == null) {
            def.setType(RocketPartType.HULL);
        }
        definitions.put(def.getId(), def);
        parts.put(def.getId(), new DataDrivenRocketPart(def));
    }

    /** Definition by id, or null if unknown. */
    public RocketPartDefinition get(String id) {
        return id != null ? definitions.get(id) : null;
    }

    /** Resolved {@link RocketPart} instance for assembly / blueprints. */
    public RocketPart getPart(String id) {
        return id != null ? parts.get(id) : null;
    }

    /**
     * Resolve part from an {@link ItemRocketPart} stack using NBT {@link
     * ItemRocketPart#NBT_PART_ID}.
     */
    public RocketPart getPartFromItem(ItemStack stack) {
        if (stack == null || !(stack.getItem() instanceof ItemRocketPart)) {
            return null;
        }
        String partId = ItemRocketPart.getPartId(stack);
        if (partId == null || partId.isEmpty()) {
            return null;
        }
        return getPart(partId);
    }

    /** @deprecated Use {@link #getPart(String)}. */
    @Deprecated
    public RocketPart getById(String id) {
        return getPart(id);
    }

    /**
     * Resolve parts by id; skips unknown ids. Use with
     * {@link RocketAssemblerMultiblock#buildFromParts}.
     */
    public List<RocketPart> getPartsByIds(Iterable<String> ids) {
        if (ids == null) {
            return Collections.emptyList();
        }
        List<RocketPart> out = new ArrayList<RocketPart>();
        for (String id : ids) {
            RocketPart p = getPart(id);
            if (p != null) {
                out.add(p);
            }
        }
        return out;
    }

    public Collection<RocketPartDefinition> getAllDefinitions() {
        return Collections.unmodifiableCollection(definitions.values());
    }

    public Collection<RocketPart> getAllParts() {
        return Collections.unmodifiableCollection(parts.values());
    }

    /** @deprecated Use {@link #getAllParts()}. */
    @Deprecated
    public Collection<RocketPart> getAll() {
        return getAllParts();
    }
}
