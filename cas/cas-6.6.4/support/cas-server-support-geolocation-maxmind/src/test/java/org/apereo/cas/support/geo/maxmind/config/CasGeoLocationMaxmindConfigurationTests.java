package org.apereo.cas.support.geo.maxmind.config;

import org.apereo.cas.authentication.adaptive.geo.GeoLocationService;
import org.apereo.cas.config.CasGeoLocationConfiguration;
import org.apereo.cas.support.geo.config.CasGeoLocationMaxmindConfiguration;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;

import static org.junit.jupiter.api.Assertions.*;

/**                                                            
 * This is {@link CasGeoLocationMaxmindConfigurationTests}.
 *
 * @author Misagh Moayyed
 * @since 6.2.0
 */
@SpringBootTest(classes = {
    RefreshAutoConfiguration.class,
    CasGeoLocationConfiguration.class,
    CasGeoLocationMaxmindConfiguration.class
})
@Tag("GeoLocation")
public class CasGeoLocationMaxmindConfigurationTests {
    @Autowired
    @Qualifier(GeoLocationService.BEAN_NAME)
    private GeoLocationService geoLocationService;

    @Test
    public void verifyOperation() {
        assertNotNull(geoLocationService);
    }
}
