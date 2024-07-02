const puppeteer = require('puppeteer');
const cas = require('../../cas.js');

(async () => {
    const browser = await puppeteer.launch(cas.browserOptions());
    const page = await cas.newPage(browser);

    await cas.goto(page, "https://host.k3d.internal");
    await page.waitForTimeout(5000);
    await cas.assertPageTitle(page, "SonarQube");
    await cas.assertInnerText(page, 'h1.login-title',"Log In to SonarQube");
    //await cas.sleep(5000);
    await cas.click(page,'a.identity-provider-link');
    //await cas.sleep(5000);
    await cas.loginWith(page, "casuser", "Mellon");
    //await cas.sleep(5000);
    // hit strapi endpoint that triggers CAS login to get JWT
    //await cas.goto(page, "https://host.k3d.internal");
    await page.waitForTimeout(3000);
    await cas.assertInnerTextContains(page, 'ul.global-navbar-menu',"Projects");
    await browser.close();
})();
