package org.slf4j.impl;

import ch.qos.logback.classic.util.LogbackMDCAdapter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.spi.MDCAdapter;

@Slf4j
public class StaticMDCBinder {
    private LogbackMDCAdapter logbackMDCAdapter;
    /**
     * The unique instance of this class.
     */
    public static final StaticMDCBinder SINGLETON = new StaticMDCBinder();

    private StaticMDCBinder() {
        logbackMDCAdapter = new LogbackMDCAdapter();
    }

    /**
     * Currently this method always returns an instance of
     * {@link StaticMDCBinder}.
     */
    public MDCAdapter getMDCA() {
        log.info("===自定义，重写--LogbackMDCAdapter====");
        return logbackMDCAdapter;
    }
}
