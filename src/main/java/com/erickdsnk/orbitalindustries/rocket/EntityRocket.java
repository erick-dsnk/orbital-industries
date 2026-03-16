package com.erickdsnk.orbitalindustries.rocket;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.erickdsnk.orbitalindustries.OrbitalIndustriesAPI;
import com.erickdsnk.orbitalindustries.core.ConfigManager;
import com.erickdsnk.orbitalindustries.transport.RocketInterface;
import com.erickdsnk.orbitalindustries.transport.TeleportManager;

/**
 * Rocket entity. Holds a blueprint, fuel, launch state, and passenger.
 * Implements
 * {@link RocketInterface} for
 * {@link com.erickdsnk.orbitalindustries.transport.LaunchManager}.
 * Minimal implementation: state machine and teleport to orbit dimension.
 */
public class EntityRocket extends Entity implements RocketInterface {

    private static final int ORBIT_FUEL_COST = 0;
    private static final int COUNTDOWN_TICKS = 60;
    private static final int ASCENT_TICKS = 40;

    private RocketBlueprint blueprint;
    private double fuel;
    private LaunchState launchState = LaunchState.IDLE;
    private int stateTicks;

    public EntityRocket(World world) {
        super(world);
        preventEntitySpawning = true;
        setSize(1.0F, 2.0F);
    }

    public EntityRocket(World world, RocketBlueprint blueprint, double fuel) {
        this(world);
        this.blueprint = blueprint;
        this.fuel = fuel;
    }

    @Override
    protected void entityInit() {
    }

    /** Current passenger (rider); in 1.7.10 this is riddenByEntity. */
    public Entity getPassenger() {
        return riddenByEntity;
    }

    public RocketBlueprint getBlueprint() {
        return blueprint;
    }

    public void setBlueprint(RocketBlueprint blueprint) {
        this.blueprint = blueprint;
    }

    public double getFuel() {
        return fuel;
    }

    public void setFuel(double fuel) {
        this.fuel = fuel;
    }

    public LaunchState getLaunchState() {
        return launchState;
    }

    public void setLaunchState(LaunchState launchState) {
        this.launchState = launchState;
        this.stateTicks = 0;
    }

    /**
     * Start the launch sequence (countdown). Call from LaunchManager or
     * interaction.
     */
    public void startLaunch() {
        if (launchState == LaunchState.IDLE && canLaunch()) {
            setLaunchState(LaunchState.COUNTDOWN);
        }
    }

    @Override
    public boolean canLaunch() {
        if (blueprint == null || launchState != LaunchState.IDLE) {
            return false;
        }
        int required = getRequiredFuelForDestination();
        return fuel >= required && getPassenger() != null;
    }

    @Override
    public int getDestinationDimensionId() {
        return ConfigManager.getOrbitDimensionId();
    }

    @Override
    public int getFuelLevel() {
        return (int) Math.floor(fuel);
    }

    @Override
    public int getRequiredFuelForDestination() {
        return ORBIT_FUEL_COST;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (worldObj.isRemote) {
            return;
        }
        switch (launchState) {
            case COUNTDOWN:
                stateTicks++;
                if (stateTicks >= COUNTDOWN_TICKS) {
                    setLaunchState(LaunchState.LAUNCH);
                }
                break;
            case LAUNCH:
                setLaunchState(LaunchState.ASCENT);
                break;
            case ASCENT:
                stateTicks++;
                if (stateTicks >= ASCENT_TICKS) {
                    setLaunchState(LaunchState.ORBIT);
                }
                break;
            case ORBIT:
                transferToOrbit();
                setLaunchState(LaunchState.LANDED);
                break;
            default:
                break;
        }
    }

    private void transferToOrbit() {
        Entity passenger = getPassenger();
        if (passenger == null || !(passenger instanceof EntityPlayerMP)) {
            return;
        }
        TeleportManager teleportManager = OrbitalIndustriesAPI.teleportManager;
        if (teleportManager == null) {
            return;
        }
        int orbitDimId = ConfigManager.getOrbitDimensionId();
        teleportManager.teleportToDimension(passenger, orbitDimId, posX, 64.0, posZ);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tag) {
        fuel = tag.getDouble("Fuel");
        int stateOrdinal = tag.getInteger("LaunchState");
        LaunchState[] states = LaunchState.values();
        if (stateOrdinal >= 0 && stateOrdinal < states.length) {
            launchState = states[stateOrdinal];
        }
        stateTicks = tag.getInteger("StateTicks");
        // Blueprint not persisted in this minimal implementation
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tag) {
        tag.setDouble("Fuel", fuel);
        tag.setInteger("LaunchState", launchState.ordinal());
        tag.setInteger("StateTicks", stateTicks);
    }
}
