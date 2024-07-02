package org.apereo.cas.audit.spi;

import org.apereo.cas.util.RandomUtils;

import lombok.val;
import org.apereo.inspektr.audit.AuditActionContext;
import org.apereo.inspektr.audit.AuditTrailManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is {@link BaseAuditConfigurationTests}.
 *
 * @author Timur Duehr
 * @since 6.0.0
 */
@SuppressWarnings("JavaUtilDate")
public abstract class BaseAuditConfigurationTests {
    private static final String USER = RandomUtils.randomAlphanumeric(6);

    public abstract AuditTrailManager getAuditTrailManager();

    @BeforeEach
    public void onSetUp() {
        val auditTrailManager = getAuditTrailManager();
        auditTrailManager.removeAll();
        val ctx = new AuditActionContext(USER, "TEST", "TEST",
            "CAS", new Date(), "1.2.3.4",
            "1.2.3.4", "GoogleChrome");
        auditTrailManager.record(ctx);
    }

    @Test
    public void verifyAuditByDate() {
        val time = LocalDate.now(ZoneOffset.UTC).minusDays(2);
        val criteria = Map.<AuditTrailManager.WhereClauseFields, Object>of(AuditTrailManager.WhereClauseFields.DATE, time);
        val results = getAuditTrailManager().getAuditRecords(criteria);
        assertFalse(results.isEmpty());
    }

    @Test
    public void verifyAuditByPrincipal() {
        val time = LocalDate.now(ZoneOffset.UTC).minusDays(2);
        val criteria = Map.<AuditTrailManager.WhereClauseFields, Object>of(
            AuditTrailManager.WhereClauseFields.DATE, time,
            AuditTrailManager.WhereClauseFields.PRINCIPAL, USER);
        val results = getAuditTrailManager().getAuditRecords(criteria);
        assertFalse(results.isEmpty());
    }
}
