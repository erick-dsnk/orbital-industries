package com.erickdsnk.orbitalindustries.space;

import com.erickdsnk.orbitalindustries.OrbitalIndustriesAPI;

import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Applies per-dimension gravity by scaling player vertical motion each tick.
 * Earth = 1.0, orbit = 0.1 (from GravityManager / Planet data). Runs on server
 * only.
 *
 * TODO: More accurate physics (fall damage, jump height) and integration with
 * GravityManager/Planet config; this is a placeholder for low-gravity feel.
 */
public final class GravityTickHandler {

    private static final double EARTH_GRAVITY = 1.0;

    @cpw.mods.fml.common.eventhandler.SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END)
            return;
        EntityPlayer player = event.player;
        if (player.worldObj == null || player.worldObj.isRemote)
            return;
        if (OrbitalIndustriesAPI.gravityManager == null)
            return;

        int dimensionId = player.worldObj.provider.dimensionId;
        double multiplier = OrbitalIndustriesAPI.gravityManager.getGravityMultiplier(dimensionId);
        if (multiplier >= EARTH_GRAVITY)
            return;

        // Placeholder: scale vertical motion by gravity so low-gravity dimensions feel
        // floaty.
        player.motionY *= multiplier;
    }
}
