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
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * User: billreh
 * Date: 10/19/11
 * Time: 4:14 AM
 */
public class SeleniumUtils {
    public static WebElement findButtonByName(WebDriver webDriver, String name) {
        List<WebElement> elements = webDriver.findElements(By.tagName("button"));

        for(WebElement element : elements) {
            if(name.equals(element.getText()))
                return element;
        }

        return null;
    }

    public static WebElement findInputById(WebDriver webDriver, String id) {
        List<WebElement> elements = webDriver.findElements(By.tagName("input"));

        for(WebElement element : elements) {
            if(element.getAttribute("id").matches(".*:" + id + "$"))
                return element;
        }

        return null;
    }

    public static WebElement findLinkByText(WebDriver webDriver, String text) {
        List<WebElement> elements = webDriver.findElements(By.tagName("a"));

        for(WebElement element : elements) {
            if(text.equals(element.getText()))
                return element;
        }

        return null;
    }

    public static void pause(int ms) {
        pause((long)ms);
    }

    public static void pause(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static <T>T pause(T t, int time) {
        SeleniumUtils.pause(time);
        return t;
    }
}
