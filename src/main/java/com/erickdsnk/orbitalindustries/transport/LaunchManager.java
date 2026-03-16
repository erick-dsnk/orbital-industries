package com.erickdsnk.orbitalindustries.transport;

import com.erickdsnk.orbitalindustries.rocket.EntityRocket;

/**
 * Launch logic for rockets. Validates via {@link RocketInterface#canLaunch()}
 * and
 * starts the launch sequence; {@link EntityRocket} handles countdown, ascent,
 * and teleport to orbit in its tick.
 */
public final class LaunchManager {

    public boolean canLaunch(RocketInterface rocket) {
        return rocket != null && rocket.canLaunch();
    }

    /**
     * Start the launch sequence. For {@link EntityRocket}, this triggers countdown
     * and ascent; the entity then teleports the passenger to the orbit dimension.
     */
    public void launch(RocketInterface rocket) {
        if (rocket == null || !canLaunch(rocket)) {
            return;
        }
        if (rocket instanceof EntityRocket) {
            ((EntityRocket) rocket).startLaunch();
        }
    }
}
