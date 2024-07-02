const puppeteer = require('puppeteer');
const cas = require('../../cas.js');
const assert = require("assert");
const path = require("path");

(async () => {
    const browser = await puppeteer.launch(cas.browserOptions());
    try {
        const page = await cas.newPage(browser);
        let response = await cas.goto(page, "https://localhost:8443/cas/login");
        await page.waitForTimeout(3000);
        console.log(`${response.status()} ${response.statusText()}`);
        assert(response.ok());

        await cas.loginWith(page, "casuser", "Mellon");
        await page.waitForTimeout(1000);
        await cas.assertCookie(page);
        await cas.assertPageTitle(page, "CAS - Central Authentication Service Log In Successful");
        await cas.assertInnerText(page, '#content div h2', "Log In Successful");

        await cas.goto(page, "https://localhost:8443/cas/logout");
        let url = await page.url();
        console.log(`Page url: ${url}`);
        assert(url === "https://localhost:8443/cas/logout");
        await page.waitForTimeout(1000);
        await cas.assertCookie(page, false);

        console.log("Logging in using external SAML2 identity provider...");
        await cas.goto(page, "https://localhost:8443/cas/login");
        await page.waitForTimeout(1000);
        await cas.click(page, "li #SAML2Client");
        await page.waitForNavigation();
        await cas.loginWith(page, "user1", "password");
        await page.waitForTimeout(4000);
        await cas.assertCookie(page);
    } finally {
        await cas.removeDirectory(path.join(__dirname, '/saml-md'));
        await browser.close();
    }
})();
