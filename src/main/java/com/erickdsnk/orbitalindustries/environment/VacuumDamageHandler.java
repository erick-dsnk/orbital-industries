package com.erickdsnk.orbitalindustries.environment;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import com.erickdsnk.orbitalindustries.core.OIModLogger;
import com.erickdsnk.orbitalindustries.planet.AtmosphereType;
import com.erickdsnk.orbitalindustries.planet.Planet;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

/**
 * Applies environmental effects based on atmosphere type: vacuum damage in
 * NONE, poison placeholder in TOXIC. Runs on player tick (server only).
 *
 * TODO: Will be replaced by full oxygen system (oxygen tanks, sealed bases,
 * pressurized rooms). Do not implement oxygen tanks or equipment yet.
 * TODO: Future support for temperature mechanics, radiation.
 */
public final class VacuumDamageHandler {

    private static final OIModLogger LOG = new OIModLogger("VacuumDamage");

    /** Ticks between vacuum damage applications (small for testing). */
    private static final int VACUUM_DAMAGE_INTERVAL = 40;
    /** Ticks between TOXIC poison applications. */
    private static final int TOXIC_EFFECT_INTERVAL = 80;
    /** Half-hearts per vacuum damage tick. */
    private static final float VACUUM_DAMAGE_AMOUNT = 1.0f;
    /** Poison duration in ticks (seconds * 20). */
    private static final int TOXIC_POISON_DURATION = 60;
    private static final int TOXIC_POISON_AMPLIFIER = 0;
    /** Only log environment debug every N ticks to avoid spam. */
    private static final int DEBUG_LOG_INTERVAL = 100;

    private final EnvironmentManager environmentManager;

    public VacuumDamageHandler(EnvironmentManager environmentManager) {
        this.environmentManager = environmentManager;
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START)
            return;
        EntityPlayer player = event.player;
        World world = player.worldObj;
        if (world == null || world.isRemote)
            return;
        if (environmentManager == null)
            return;

        Planet planet = environmentManager.getCurrentPlanet(world);
        AtmosphereType atmosphereType = environmentManager.getAtmosphereType(world);
        int dimensionId = world.provider.dimensionId;

        // Debug: player planet, atmosphere type (throttled)
        if (player.ticksExisted % DEBUG_LOG_INTERVAL == 0) {
            LOG.debug(String.format("Player planet=%s, dim=%d, atmosphere=%s",
                    planet != null ? planet.getName() : "none",
                    dimensionId,
                    atmosphereType.name()));
        }

        switch (atmosphereType) {
            case NONE:
                // Vacuum: apply slow damage over time. Placeholder until oxygen system.
                // TODO: Replace with full oxygen system (oxygen tanks, sealed bases,
                // pressurized rooms). Then only damage when not in sealed area and no oxygen.
                if (player.ticksExisted % VACUUM_DAMAGE_INTERVAL == 0) {
                    player.attackEntityFrom(DamageSource.generic, VACUUM_DAMAGE_AMOUNT);
                    LOG.debug("Vacuum damage applied to player in dim " + dimensionId);
                }
                break;
            case THIN:
                // No damage yet. Pressure mechanics may be added later.
                break;
            case BREATHABLE:
                // No effects.
                break;
            case TOXIC:
                // Placeholder: apply slow poison. Full toxic mechanics later.
                if (player.ticksExisted % TOXIC_EFFECT_INTERVAL == 0) {
                    player.addPotionEffect(
                            new PotionEffect(Potion.poison.getId(), TOXIC_POISON_DURATION, TOXIC_POISON_AMPLIFIER));
                }
                break;
            default:
                break;
        }
    }
}
