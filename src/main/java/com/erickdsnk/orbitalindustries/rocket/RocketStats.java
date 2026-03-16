package com.erickdsnk.orbitalindustries.rocket;

/**
 * Combined capabilities of a rocket. Mutable so parts can apply deltas via
 * {@link RocketPart#modifyStats(RocketStats)}. Used when building a
 * {@link RocketBlueprint}.
 */
public class RocketStats {

    private double thrust;
    private double fuelCapacity;
    private double mass;
    private int navigationTier;
    private double maxRange;

    public RocketStats() {
        this(0.0, 0.0, 0.0, 0, 0.0);
    }

    public RocketStats(double thrust, double fuelCapacity, double mass, int navigationTier, double maxRange) {
        this.thrust = thrust;
        this.fuelCapacity = fuelCapacity;
        this.mass = mass;
        this.navigationTier = navigationTier;
        this.maxRange = maxRange;
    }

    public double getThrust() {
        return thrust;
    }

    public void setThrust(double thrust) {
        this.thrust = thrust;
    }

    public void addThrust(double delta) {
        this.thrust += delta;
    }

    public double getFuelCapacity() {
        return fuelCapacity;
    }

    public void setFuelCapacity(double fuelCapacity) {
        this.fuelCapacity = fuelCapacity;
    }

    public void addFuelCapacity(double delta) {
        this.fuelCapacity += delta;
    }

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public void addMass(double delta) {
        this.mass += delta;
    }

    public int getNavigationTier() {
        return navigationTier;
    }

    public void setNavigationTier(int navigationTier) {
        this.navigationTier = navigationTier;
    }

    public void addNavigationTier(int delta) {
        this.navigationTier += delta;
    }

    public double getMaxRange() {
        return maxRange;
    }

    public void setMaxRange(double maxRange) {
        this.maxRange = maxRange;
    }

    public void addMaxRange(double delta) {
        this.maxRange += delta;
    }

    /**
     * Returns a copy of this stats instance for building blueprints without
     * mutating the original.
     */
    public RocketStats copy() {
        return new RocketStats(thrust, fuelCapacity, mass, navigationTier, maxRange);
    }
}
