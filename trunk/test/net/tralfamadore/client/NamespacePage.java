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

package net.tralfamadore.client;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

import static net.tralfamadore.client.SeleniumUtils.*;

/**
 * User: billreh
 * Date: 10/19/11
 * Time: 2:47 AM
 */
public class NamespacePage {
    private WebDriver webDriver;

    public NamespacePage(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    public void addNamespace(String namespaceName) {
        findButtonByName(webDriver, "New Namespace ...").click(); pause(500);
        findInputById(webDriver, "namespaceName").sendKeys(namespaceName); pause(500);
        findButtonByName(webDriver, "Save").click(); pause(500);
    }

    public void removeNamespace(String namespaceName) {
        List<WebElement> elements = webDriver.findElements(By.tagName("button"));
        for(WebElement element : elements) {
            if(("Delete " + namespaceName).equals(element.getAttribute("alt"))) {
                element.click();
                break;
            }
        }
        pause(500);
        elements = webDriver.findElements(By.tagName("button")); // Dialog has appeared w/Yes and No buttons
        for(WebElement element : elements) {
            if("Yes".equals(element.getText())) {
                element.click();
                break;
            }
        }
        pause(500);
    }

    public boolean hasHeader(String text) {
        List<WebElement> elements = webDriver.findElements(By.tagName("h1"));
        for(WebElement element : elements) {
            if(text.equals(element.getText()))
                return true;
        }
        return false;
    }
}
