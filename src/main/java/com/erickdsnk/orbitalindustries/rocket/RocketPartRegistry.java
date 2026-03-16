package com.erickdsnk.orbitalindustries.rocket;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Registry of rocket parts by id. Populated by {@link RocketPartLoader} from
 * JSON; modpack creators can add or override parts via config.
 */
public final class RocketPartRegistry {

    private final Map<String, RocketPart> byId = new LinkedHashMap<String, RocketPart>();

    public void register(RocketPart part, String id) {
        if (part == null || id == null || id.isEmpty())
            return;
        byId.put(id, part);
    }

    public RocketPart getById(String id) {
        return id != null ? byId.get(id) : null;
    }

    /**
     * Resolve parts by id; skips unknown ids. Use with
     * {@link RocketAssemblerMultiblock#buildFromParts}.
     */
    public List<RocketPart> getPartsByIds(Iterable<String> ids) {
        if (ids == null)
            return Collections.emptyList();
        List<RocketPart> out = new ArrayList<RocketPart>();
        for (String id : ids) {
            RocketPart p = getById(id);
            if (p != null)
                out.add(p);
        }
        return out;
    }

    public Collection<RocketPart> getAll() {
        return Collections.unmodifiableCollection(byId.values());
    }
}
