package com.erickdsnk.orbitalindustries.transport;

/**
 * Interface for rocket-like vehicles. Concrete rocket types (entities or blocks) implement this.
 *
 * TODO: Implementations provide canLaunch(), getDestination(), getFuelLevel() for LaunchManager.
 */
public interface RocketInterface {

    boolean canLaunch();

    int getDestinationDimensionId();

    int getFuelLevel();

    int getRequiredFuelForDestination();
}
