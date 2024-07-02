package org.apereo.cas.notifications.sms;

import org.apereo.cas.config.CasCoreNotificationsConfiguration;
import org.apereo.cas.notifications.CommunicationsManager;

import lombok.val;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.autoconfigure.mail.MailSenderValidatorAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is {@link GroovySmsSenderTests}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0
 */
@SpringBootTest(classes = {
    RefreshAutoConfiguration.class,
    CasCoreNotificationsConfiguration.class,
    MailSenderAutoConfiguration.class,
    MailSenderValidatorAutoConfiguration.class
},
    properties = "cas.sms-provider.groovy.location=classpath:/GroovySmsSender.groovy")
@Tag("Groovy")
public class GroovySmsSenderTests {
    @Autowired
    @Qualifier(CommunicationsManager.BEAN_NAME)
    private CommunicationsManager communicationsManager;

    @Test
    public void verifyOperation() {
        assertTrue(communicationsManager.isSmsSenderDefined());
        val smsRequest = SmsRequest.builder().from("CAS")
            .to("1234567890").text("Hello CAS").build();
        assertTrue(communicationsManager.sms(smsRequest));
    }
}
