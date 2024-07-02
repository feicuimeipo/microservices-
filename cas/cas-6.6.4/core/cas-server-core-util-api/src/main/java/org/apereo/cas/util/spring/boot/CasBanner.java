package org.apereo.cas.util.spring.boot;

import org.springframework.boot.Banner;
import org.springframework.core.env.Environment;

import java.util.Formatter;

/**
 * This is {@link CasBanner}.
 *
 * @author Misagh Moayyed
 * @since 6.6.0
 */
public interface CasBanner extends Banner {
    /**
     * Gets title.
     *
     * @return the title
     */
    String getTitle();

    /**
     * Inject environment info.
     *
     * @param formatter   the formatter
     * @param environment the environment
     * @param sourceClass the source class
     */
    default void injectEnvironmentInfo(final Formatter formatter,
                                       final Environment environment,
                                       final Class<?> sourceClass) {
    }
}
