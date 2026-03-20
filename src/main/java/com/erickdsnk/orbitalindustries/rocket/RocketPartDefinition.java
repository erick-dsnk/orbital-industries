package com.erickdsnk.orbitalindustries.rocket;

/**
 * Data model for a rocket part loaded from JSON. Omitted JSON fields
 * deserialize
 * as zero/false defaults via Gson (primitives).
 */
public final class RocketPartDefinition {

    private String id;
    private RocketPartType type;
    private double thrust;
    private double fuelCapacity;
    private double mass;
    private int navigationTier;
    private double maxRange;
    /** Optional item icon (e.g. {@code orbitalindustries:my_part}). */
    private String icon;

    public RocketPartDefinition() {
    }

    public RocketPartDefinition(String id, RocketPartType type, double thrust,
            double fuelCapacity, double mass, int navigationTier, double maxRange) {
        this.id = id;
        this.type = type;
        this.thrust = thrust;
        this.fuelCapacity = fuelCapacity;
        this.mass = mass;
        this.navigationTier = navigationTier;
        this.maxRange = maxRange;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RocketPartType getType() {
        return type;
    }

    public void setType(RocketPartType type) {
        this.type = type;
    }

    public double getThrust() {
        return thrust;
    }

    public void setThrust(double thrust) {
        this.thrust = thrust;
    }

    public double getFuelCapacity() {
        return fuelCapacity;
    }

    public void setFuelCapacity(double fuelCapacity) {
        this.fuelCapacity = fuelCapacity;
    }

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public int getNavigationTier() {
        return navigationTier;
    }

    public void setNavigationTier(int navigationTier) {
        this.navigationTier = navigationTier;
    }

    public double getMaxRange() {
        return maxRange;
    }

    public void setMaxRange(double maxRange) {
        this.maxRange = maxRange;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
