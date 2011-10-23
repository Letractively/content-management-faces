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

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static net.integration.SeleniumUtils.pause;

/**
 * User: billreh
 * Date: 10/19/11
 * Time: 2:45 AM
 */
public class StartPage {
    private WebDriver webDriver;

    public StartPage(WebDriver webDriver) {
        this.webDriver = webDriver;
        webDriver.get("http://localhost:8080/cmf/cmf/");
        ((JavascriptExecutor)webDriver).executeScript("window.resizeTo(1024, 768)");
        pause(500);
    }

    public NamespacePage goToNamespacePage(String namespaceName) {
        WebElement webElement = webDriver.findElement(By.partialLinkText(namespaceName));
        webElement.click();
        pause(500);
        return new NamespacePage(webDriver);
    }
}
