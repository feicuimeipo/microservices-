package org.apereo.cas.configuration.model.support.pac4j;

import org.apereo.cas.configuration.support.RequiresModule;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * This is {@link Pac4jDelegatedAuthenticationWindowsLiveProperties}.
 *
 * @author Misagh Moayyed
 * @since 6.4.0
 */
@RequiresModule(name = "cas-server-support-pac4j-webflow")
@Getter
@Setter
@Accessors(chain = true)
public class Pac4jDelegatedAuthenticationWindowsLiveProperties extends Pac4jIdentifiableClientProperties {

    private static final long serialVersionUID = -5663033494303169583L;

    public Pac4jDelegatedAuthenticationWindowsLiveProperties() {
        setClientName("Windows Live");
    }
}
