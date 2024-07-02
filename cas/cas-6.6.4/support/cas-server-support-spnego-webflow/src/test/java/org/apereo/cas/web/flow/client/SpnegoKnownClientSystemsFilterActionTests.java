package org.apereo.cas.web.flow.client;

import org.apereo.cas.util.RegexUtils;

import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.webflow.action.EventFactorySupport;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.test.MockRequestContext;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@link BaseSpnegoKnownClientSystemsFilterAction}
 * and {@link HostNameSpnegoKnownClientSystemsFilterAction}.
 *
 * @author Sean Baker sean.baker@usuhs.edu
 * @author Misagh Moayyed
 * @since 4.1
 */
@Tag("Spnego")
public class SpnegoKnownClientSystemsFilterActionTests {

    private static final String ALTERNATE_REMOTE_IP = "74.125.136.102";

    @Test
    public void ensureRemoteIpShouldBeChecked() throws Exception {
        val action =new BaseSpnegoKnownClientSystemsFilterAction(RegexUtils.createPattern("^192\\.158\\..+"),
            StringUtils.EMPTY, 0);

        val ctx = new MockRequestContext();
        val req = new MockHttpServletRequest();
        req.setRemoteAddr("192.158.5.781");
        val extCtx = new ServletExternalContext(
            new MockServletContext(), req,
            new MockHttpServletResponse());
        ctx.setExternalContext(extCtx);

        val ev = action.doExecute(ctx);
        assertEquals(new EventFactorySupport().yes(this).getId(), ev.getId());
    }

    @Test
    public void ensureRemoteIpShouldNotBeChecked() throws Exception {
        val action = new BaseSpnegoKnownClientSystemsFilterAction(RegexUtils.createPattern("^192\\.158\\..+"),
                StringUtils.EMPTY, 0);

        val ctx = new MockRequestContext();
        val req = new MockHttpServletRequest();
        req.setRemoteAddr("193.158.5.781");
        val extCtx = new ServletExternalContext(
            new MockServletContext(), req,
            new MockHttpServletResponse());
        ctx.setExternalContext(extCtx);

        val ev = action.doExecute(ctx);
        assertNotEquals(new EventFactorySupport().yes(this).getId(), ev.getId());
    }

    @Test
    public void ensureAltRemoteIpHeaderShouldBeChecked() throws Exception {
        val action = new BaseSpnegoKnownClientSystemsFilterAction(RegexUtils.createPattern("^74\\.125\\..+"),
                "alternateRemoteIp", 120);

        val ctx = new MockRequestContext();
        val req = new MockHttpServletRequest();
        req.setRemoteAddr("555.555.555.555");
        req.addHeader("alternateRemoteIp", ALTERNATE_REMOTE_IP);
        val extCtx = new ServletExternalContext(
            new MockServletContext(), req,
            new MockHttpServletResponse());
        ctx.setExternalContext(extCtx);

        val ev = action.doExecute(ctx);
        assertEquals(new EventFactorySupport().yes(this).getId(), ev.getId());
    }

    @Test
    public void ensureHostnameShouldDoSpnego() throws Exception {
        val action = new HostNameSpnegoKnownClientSystemsFilterAction(RegexUtils.createPattern(".+"),
                StringUtils.EMPTY, 0, "\\w+\\.\\w+\\.\\w+");

        val ctx = new MockRequestContext();
        val req = new MockHttpServletRequest();
        req.setRemoteAddr(ALTERNATE_REMOTE_IP);
        val extCtx = new ServletExternalContext(
            new MockServletContext(), req,
            new MockHttpServletResponse());
        ctx.setExternalContext(extCtx);

        val ev = action.doExecute(ctx);
        assertEquals(new EventFactorySupport().yes(this).getId(), ev.getId());
    }

    @Test
    public void ensureHostnameAndIpShouldDoSpnego() throws Exception {
        val action =
            new HostNameSpnegoKnownClientSystemsFilterAction(RegexUtils.createPattern("74\\..+"),
                StringUtils.EMPTY, 0, "\\w+\\.\\w+\\.\\w+");

        val ctx = new MockRequestContext();
        val req = new MockHttpServletRequest();
        req.setRemoteAddr(ALTERNATE_REMOTE_IP);
        val extCtx = new ServletExternalContext(
            new MockServletContext(), req,
            new MockHttpServletResponse());
        ctx.setExternalContext(extCtx);

        val ev = action.doExecute(ctx);
        assertEquals(new EventFactorySupport().yes(this).getId(), ev.getId());

    }

    @Test
    public void verifyIpMismatchWhenCheckingHostnameForSpnego() throws Exception {
        val action =
            new HostNameSpnegoKnownClientSystemsFilterAction(RegexUtils.createPattern("14\\..+"),
                StringUtils.EMPTY, 0, "\\w+\\.\\w+\\.\\w+");

        val ctx = new MockRequestContext();
        val req = new MockHttpServletRequest();
        req.setRemoteAddr(ALTERNATE_REMOTE_IP);
        val extCtx = new ServletExternalContext(
            new MockServletContext(), req,
            new MockHttpServletResponse());
        ctx.setExternalContext(extCtx);

        val ev = action.doExecute(ctx);
        assertEquals(new EventFactorySupport().no(this).getId(), ev.getId());

    }
}
