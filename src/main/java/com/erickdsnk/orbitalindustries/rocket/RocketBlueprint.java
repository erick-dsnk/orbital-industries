package com.erickdsnk.orbitalindustries.rocket;

import java.util.Collections;
import java.util.List;

/**
 * Represents a constructed rocket: list of parts and the aggregated
 * {@link RocketStats}. Built by applying each part's
 * {@link RocketPart#modifyStats}
 * to a fresh RocketStats instance.
 */
public final class RocketBlueprint {

    private final List<RocketPart> parts;
    private final RocketStats stats;

    /**
     * Builds a blueprint from the given parts. Stats are computed by applying
     * each part's modifyStats to a new RocketStats instance.
     */
    public RocketBlueprint(List<RocketPart> parts) {
        if (parts == null || parts.isEmpty()) {
            this.parts = Collections.emptyList();
            this.stats = new RocketStats();
        } else {
            this.parts = Collections.unmodifiableList(new java.util.ArrayList<RocketPart>(parts));
            RocketStats s = new RocketStats();
            for (RocketPart part : this.parts) {
                part.modifyStats(s);
            }
            this.stats = s;
        }
    }

    public List<RocketPart> getParts() {
        return parts;
    }

    /**
     * Returns a copy of the aggregated stats so the blueprint remains immutable.
     */
    public RocketStats getStats() {
        return stats.copy();
    }
}
