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

package net.tralfamadore;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AddNamespaceContentAndStyleTest {
    private Selenium selenium;

    @Before
    public void setUp() throws Exception {
        selenium = new DefaultSelenium("localhost", 4444, "*firefox", "http://localhost:8080/cmf/cmf/admin/index.jsf");
        selenium.start();
    }

    @Test
    public void testAddNamespaceContentAndStyle() throws Exception {
        selenium.open("http://localhost:8080/cmf/cmf/admin/index.jsf");
        // Add .net namespace
        selenium.click("//form[@id='theForm']/table/tbody/tr[1]/td/div/div[2]");
        pause(250);
        selenium.click("emptyPathButton");
        pause(250);
        selenium.type("in", "net");
        selenium.click("submitNamespace");
        pause(250);
        assertTrue(selenium.isTextPresent("net"));
        // Add tralfamadore namespace
        selenium.click("//td[@id='ygtvcontentel2']/span/span[1]");
        pause(250);
        selenium.click("j_idt17");
        pause(250);
        selenium.type("in", "tralfamadore");
        selenium.click("submitNamespace");
        pause(250);
        assertTrue(selenium.isTextPresent("tralfamadore"));
        // Add site namespace
        selenium.click("//td[@id='ygtvcontentel5']/span/span[1]");
        pause(250);
        selenium.click("//input[@id='j_idt17' and @name='j_idt17' and @type='image' and @onclick=\"mojarra.ab(this,event,'action',0,'in namespace',{'onevent':hideEditorAndShowNamespace(),'namespace':'net.tralfamadore'});return false\"]");
        pause(250);
        selenium.type("in", "site");
        selenium.click("submitNamespace");
        pause(250);
        assertTrue(selenium.isTextPresent("site"));
        // Add page1 namespace
        selenium.click("//td[@id='ygtvcontentel9']/span/span[1]");
        pause(250);
        selenium.click("//input[@id='j_idt17' and @name='j_idt17' and @type='image' and @onclick=\"mojarra.ab(this,event,'action',0,'in namespace',{'onevent':hideEditorAndShowNamespace(),'namespace':'net.tralfamadore.site'});return false\"]");
        pause(250);
        selenium.type("in", "page1");
        selenium.click("submitNamespace");
        pause(250);
        assertTrue(selenium.isTextPresent("page1"));
        // Add article content
        selenium.click("//td[@id='ygtvcontentel14']/span/span[1]");
        pause(250);
        selenium.click("//input[@id='j_idt21' and @name='j_idt21' and @type='image' and @onclick=\"mojarra.ab(this,event,'action',0,'content contentNamespace',{'onevent':hideEditorAndShowContent,'namespace':'net.tralfamadore.site.page1'});return false\"]");
        pause(250);
        selenium.type("content", "article");
        selenium.click("submitContent");
        pause(250);
        assertTrue(selenium.isTextPresent("article"));
        // Add style
        selenium.click("//td[@id='ygtvcontentel19']/span/span[1]");
        pause(250);
        selenium.click("//input[@id='j_idt23' and @name='j_idt23' and @type='image' and @onclick=\"mojarra.ab(this,event,'action',0,'style content contentNamespace namespace styleNamespace',{'onevent':hideEditorAndShowStyle,'namespace':'net.tralfamadore.site.page1'});return false\"]");
        pause(250);
        selenium.type("style", "style");
        selenium.click("submitStyle");
        pause(250);
        assertTrue(selenium.isTextPresent("style"));
        // Edit style
        selenium.click("//td[@id='ygtvcontentel27']/span/span[1]");
        pause(250);
        selenium.click("j_idt41");
        pause(250);
        selenium.typeKeys("//body[@class='editbox']", "p { color: green; }");
        selenium.click("styleEditSubmit");
        pause(250);
        // Edit content
        selenium.click("//td[@id='ygtvcontentel26']/span/span[1]");
        pause(250);
        selenium.click("j_idt30");
        pause(450);
        selenium.typeKeys("//body[@contenteditable='true']", "Hello");
        selenium.click("submit");
        pause(250);
        selenium.click("//td[@id='ygtvcontentel27']/span/span[1]");
        pause(250);
        selenium.click("j_idt47");
        pause(250);
        // Open content page
        selenium.open("http://localhost:8080/cmf/contentTest.jsf", "");
        pause(250);
        // Check the content
        assertTrue(selenium.isTextPresent("Hello"));
        pause(250);
        // Check the style (color should be green)
        String s = selenium.getEval("window.getComputedStyle(" +
                "window.document.getElementById('theContent').firstChild, null).getPropertyValue('color')");
        pause(250);
        // we get the color in rgb format
        assertEquals("rgb(0, 128, 0)", s);
        pause(250);
        selenium.click("j_idt7:resetDb");
    }

    private void pause(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            System.out.println("Sleep interrupted");
        }
    }

    @After
    public void tearDown() throws Exception {
        selenium.stop();
    }
}
