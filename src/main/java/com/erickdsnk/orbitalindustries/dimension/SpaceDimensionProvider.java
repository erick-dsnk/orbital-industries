package com.erickdsnk.orbitalindustries.dimension;

/**
 * Base/descriptor for a space dimension. Holds dimension ID and metadata; subclasses
 * register with DimensionRegistry. Actual world behavior is in SpaceWorldProvider.
 *
 * TODO: Optional sky renderer hook for custom space sky.
 * TODO: Spawn and world gen configuration; keep data-driven (e.g. from Planet).
 */
public abstract class SpaceDimensionProvider {

    private final int dimensionId;

    protected SpaceDimensionProvider(int dimensionId) {
        this.dimensionId = dimensionId;
    }

    public int getDimensionId() {
        return dimensionId;
    }
}
