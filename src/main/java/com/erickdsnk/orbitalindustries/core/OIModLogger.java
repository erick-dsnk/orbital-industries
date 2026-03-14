package com.erickdsnk.orbitalindustries.core;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import com.erickdsnk.orbitalindustries.OrbitalIndustries;

/**
 * Logger wrapper for consistent prefixed logging across subsystems.
 * Delegates to the mod's main logger.
 */
public final class OIModLogger {

    private static final String PREFIX = "[" + OrbitalIndustries.MODID + "] ";
    private final Logger log;

    public OIModLogger(String subsystem) {
        this.log = OrbitalIndustries.LOG;
    }

    public void info(String msg) {
        log.info(PREFIX + msg);
    }

    public void warn(String msg) {
        log.warn(PREFIX + msg);
    }

    public void error(String msg) {
        log.error(PREFIX + msg);
    }

    public void debug(String msg) {
        log.log(Level.DEBUG, PREFIX + msg);
    }
}
