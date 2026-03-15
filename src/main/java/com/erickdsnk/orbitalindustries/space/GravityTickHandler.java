package com.erickdsnk.orbitalindustries.space;

import com.erickdsnk.orbitalindustries.OrbitalIndustriesAPI;

import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;

import net.minecraft.entity.player.EntityPlayer;

import net.minecraftforge.event.entity.living.LivingFallEvent;

/**
 * Applies per-dimension gravity from Planet data (via GravityManager). Earth =
 * 1.0,
 * orbit = 0.1. Runs at tick START so our adjustment is applied before vanilla
 * gravity;
 * we add a positive offset so that when vanilla subtracts its gravity, the net
 * effect
 * is scaled (e.g. 0.1g in orbit). Runs on both client and server so the player
 * sees
 * reduced gravity locally. Also scales fall damage by planet gravity.
 *
 * TODO: Jump height scaling per planet gravity.
 */
public final class GravityTickHandler {

    /**
     * Vanilla gravity applied to motionY per tick when falling (approx. 0.08 in
     * 1.7.10).
     */
    private static final double VANILLA_GRAVITY_PER_TICK = 0.08;

    private static final double EARTH_GRAVITY = 1.0;

    @cpw.mods.fml.common.eventhandler.SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START)
            return;
        EntityPlayer player = event.player;
        if (player.worldObj == null || OrbitalIndustriesAPI.gravityManager == null)
            return;

        int dimensionId = player.worldObj.provider.dimensionId;
        double multiplier = OrbitalIndustriesAPI.gravityManager.getGravityMultiplier(dimensionId);
        if (multiplier >= EARTH_GRAVITY)
            return;

        // Only scale when player is in air; on ground vanilla handles motionY.
        if (!player.onGround) {
            // Pre-adjust so that when vanilla subtracts VANILLA_GRAVITY_PER_TICK this tick,
            // the net change is -VANILLA_GRAVITY_PER_TICK * multiplier (reduced gravity).
            player.motionY += VANILLA_GRAVITY_PER_TICK * (1.0 - multiplier);
        }
    }

    /**
     * Scales fall damage by the planet's gravity multiplier so low-gravity
     * dimensions
     * (e.g. orbit) inflict proportionally less damage. We scale the fall distance
     * so the damage formula (based on distance) yields proportionally less damage.
     * Runs on server only.
     */
    @cpw.mods.fml.common.eventhandler.SubscribeEvent
    public void onLivingFall(LivingFallEvent event) {
        if (event.entity.worldObj == null || event.entity.worldObj.isRemote)
            return;
        if (OrbitalIndustriesAPI.gravityManager == null)
            return;
        int dimensionId = event.entity.worldObj.provider.dimensionId;
        double gravityMultiplier = OrbitalIndustriesAPI.gravityManager.getGravityMultiplier(dimensionId);
        if (gravityMultiplier >= EARTH_GRAVITY)
            return;
        float distance = event.distance;
        event.distance = (float) (distance * gravityMultiplier);
    }
}
