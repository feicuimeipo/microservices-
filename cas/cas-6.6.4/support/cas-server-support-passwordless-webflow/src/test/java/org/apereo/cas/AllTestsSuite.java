package org.apereo.cas;

import org.apereo.cas.web.flow.AcceptPasswordlessAuthenticationActionTests;
import org.apereo.cas.web.flow.DetermineMultifactorPasswordlessAuthenticationActionTests;
import org.apereo.cas.web.flow.DisplayBeforePasswordlessAuthenticationActionTests;
import org.apereo.cas.web.flow.PasswordlessAuthenticationWebflowConfigurerTests;
import org.apereo.cas.web.flow.PasswordlessCasWebflowLoginContextProviderTests;
import org.apereo.cas.web.flow.PrepareForPasswordlessAuthenticationActionTests;
import org.apereo.cas.web.flow.VerifyPasswordlessAccountAuthenticationActionTests;
import org.apereo.cas.web.flow.delegation.PasswordlessDelegatedClientAuthenticationWebflowStateContributorTests;
import org.apereo.cas.web.flow.delegation.PasswordlessDelegatedClientIdentityProviderAuthorizerTests;
import org.apereo.cas.web.flow.delegation.PasswordlessDetermineDelegatedAuthenticationActionTests;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * This is {@link AllTestsSuite}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0-RC3
 */
@SelectClasses({
    PasswordlessDelegatedClientIdentityProviderAuthorizerTests.class,
    PasswordlessDelegatedClientAuthenticationWebflowStateContributorTests.class,
    PasswordlessCasWebflowLoginContextProviderTests.class,
    PasswordlessAuthenticationWebflowConfigurerTests.class,
    DisplayBeforePasswordlessAuthenticationActionTests.class,
    VerifyPasswordlessAccountAuthenticationActionTests.class,
    PasswordlessDetermineDelegatedAuthenticationActionTests.class,
    DetermineMultifactorPasswordlessAuthenticationActionTests.class,
    AcceptPasswordlessAuthenticationActionTests.class,
    PrepareForPasswordlessAuthenticationActionTests.class
})
@Suite
public class AllTestsSuite {
}
