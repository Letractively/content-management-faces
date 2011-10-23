/*
 * Copyright (c) 2011 Bill Reh.
 *
 * This file is part of Content Management Faces.
 *
 * Content Management Faces is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package net.integration;

import org.junit.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * User: billreh
 * Date: 10/19/11
 * Time: 2:09 AM
 */
public class CmfClientTest {
    private static ChromeDriverService service;

    private WebDriver webDriver;

    @BeforeClass
    public static void startService() throws IOException {
        service = new ChromeDriverService.Builder().usingChromeDriverExecutable(new File("/usr/local/bin/chromedriver"))
                .usingAnyFreePort().build();
        service.start();
    }

    @AfterClass
    public static void stopService() {
        service.stop();
    }

    @Before
    public void startDriver() {
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        webDriver = new RemoteWebDriver(service.getUrl(), capabilities);
    }

    @After
    public void endDriver() {
        webDriver.quit();
    }

    @Test
    public void testNavigateNamespace() {
        StartPage startPage = new StartPage(webDriver);

        NamespacePage namespacePage = startPage.goToNamespacePage("net");
        assertTrue(namespacePage.hasHeader("net"));
        namespacePage = startPage.goToNamespacePage("tralfamadore");
        assertTrue(namespacePage.hasHeader("net.tralfamadore"));
        namespacePage = startPage.goToNamespacePage("page1");
        assertTrue(namespacePage.hasHeader("net.tralfamadore.site.page1"));
    }

    @Test
    public void testAddRemoveNamespace() throws Exception {
        String namespaceName = "com";
        StartPage startPage = new StartPage(webDriver);

        assertNull(SeleniumUtils.findLinkByText(webDriver, namespaceName));

        NamespacePage namespacePage = startPage.goToNamespacePage("page1");

        namespacePage.addNamespace(namespaceName);

        assertNotNull(SeleniumUtils.findLinkByText(webDriver, namespaceName));

        namespacePage = startPage.goToNamespacePage(namespaceName);

        namespacePage.removeNamespace(namespaceName);

        assertNull(SeleniumUtils.findLinkByText(webDriver, namespaceName));
    }
}
