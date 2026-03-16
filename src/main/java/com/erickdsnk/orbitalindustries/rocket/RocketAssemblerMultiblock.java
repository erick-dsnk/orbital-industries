package com.erickdsnk.orbitalindustries.rocket;

import java.util.List;

/**
 * Placeholder for the rocket assembler multiblock. Eventually will detect parts
 * in the world and build a blueprint; for now only builds from a given list of
 * parts.
 */
public final class RocketAssemblerMultiblock {

    /**
     * Build a rocket blueprint from the given parts. No multiblock detection or
     * world interaction yet.
     */
    public RocketBlueprint buildFromParts(List<RocketPart> parts) {
        return new RocketBlueprint(parts);
    }
}
