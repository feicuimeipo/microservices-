const puppeteer = require('puppeteer');
const cas = require('../../cas.js');

(async () => {
    const browser = await puppeteer.launch(cas.browserOptions());
    const page = await cas.newPage(browser);

    await cas.goto(page, "https://localhost:8443/cas/login");
    await cas.loginWith(page, "casuser", "Mellon");
    await cas.assertCookie(page);
    await cas.assertCookie(page, true, "CASWebflowCookie");
    await cas.assertInnerText(page, '#content div h2', "Log In Successful");
    await browser.close();
})();
