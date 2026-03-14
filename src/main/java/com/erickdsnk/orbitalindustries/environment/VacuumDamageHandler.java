package com.erickdsnk.orbitalindustries.environment;

/**
 * Placeholder for vacuum damage when not in sealed environment. No gameplay implementation yet.
 *
 * TODO: Hook into player tick or Forge LivingUpdateEvent; check AtmosphereManager/OxygenSystem
 * and apply damage when in vacuum without oxygen.
 */
public final class VacuumDamageHandler {

    /**
     * TODO: Called from event handler; check if entity is in vacuum and apply damage.
     */
    public void onLivingUpdate(Object entity) {
        // Scaffolding only.
    }
}
