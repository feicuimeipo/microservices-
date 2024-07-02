const puppeteer = require('puppeteer');
const path = require('path');
const cas = require('../../cas.js');

async function cleanUp(spDir) {
    await cas.logg("Stopping SP process...");
    await cas.stopGradleApp(spDir);
}

(async () => {
    let spDir = path.join(__dirname, '/wsfed-sp');
    await cas.launchWsFedSp(spDir);
    await cas.waitFor('https://localhost:9876/fediz', async () => {
        const browser = await puppeteer.launch(cas.browserOptions());
        const page = await cas.newPage(browser);

        console.log("Trying without an exising SSO session...");
        await cas.goto(page, "https://localhost:9876/fediz");
        await page.waitForTimeout(2000);
        await page.waitForSelector('#logincas', {visible: true});
        await cas.click(page, "#logincas");
        await page.waitForTimeout(2000);
        await page.waitForSelector('#username', {visible: true});
        await cas.loginWith(page, "casuser", "Mellon");
        await page.waitForResponse(response => response.status() === 200);
        await page.waitForTimeout(2000);
        console.log(`Page URL: ${page.url()}`);
        await cas.assertInnerText(page, "#principalId", "casuser");
        await cas.assertVisibility(page, "#assertion");
        await page.waitForTimeout(2000);
        await cas.assertInnerText(page, "#claim0", "http://schemas.xmlsoap.org/claims/EmailAddress:casuser@example.org");
        await cas.assertInnerText(page, "#claim1", "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname:casuser");
        await cas.assertInnerText(page, "#claim2", "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress:CAS@example.org");

        console.log("Trying with an exising SSO session...");
        await cas.goto(page, "https://localhost:8443/cas/logout");
        await cas.goto(page, "https://localhost:8443/cas/login");
        await cas.loginWith(page, "casuser", "Mellon");
        await cas.assertCookie(page);
        await cas.goto(page, "https://localhost:9876/fediz");
        await page.waitForTimeout(2000);
        await page.waitForSelector('#logincas', {visible: true});
        await cas.click(page, "#logincas");
        await page.waitForTimeout(2000);
        console.log(`Page URL: ${page.url()}`);
        await cas.assertInnerText(page, "#principalId", "casuser");
        await cas.assertVisibility(page, "#assertion");
        await page.waitForTimeout(2000);
        await cas.assertInnerText(page, "#claim0", "http://schemas.xmlsoap.org/claims/EmailAddress:casuser@example.org");
        await cas.assertInnerText(page, "#claim1", "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname:casuser");
        await cas.assertInnerText(page, "#claim2", "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress:CAS@example.org");

        await browser.close();
        await cleanUp(spDir);
    }, async error => {
        await cleanUp(spDir);
        await cas.logg(error);
        throw error;
    })
})();

