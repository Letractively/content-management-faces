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

package net.tralfamadore.cmf;

import mockit.Mock;
import mockit.MockClass;
import mockit.Mockit;
import net.tralfamadore.config.CmfContext;
import net.tralfamadore.config.ConfigFile;
import net.tralfamadore.persistence.EntityManagerProvider;
import net.tralfamadore.persistence.JpaEntityManagerProvider;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

/**
 * User: billreh
 * Date: 2/11/11
 * Time: 4:38 AM
 */
public class JpaContentManagerTest {
    private JpaContentManager contentManager;

    @Before
    public void setUp() {
        Mockit.setUpMocks(new MockCmfContext());
        contentManager = new JpaContentManager();
    }

    @Test
    public void testNamespaceMethods() throws Exception {
        contentManager.saveNamespace(Namespace.createFromString("com"));
        contentManager.saveNamespace(Namespace.createFromString("com.site"));
        contentManager.saveNamespace(Namespace.createFromString("net"));
        contentManager.saveNamespace(Namespace.createFromString("net.tralfamadore"));
        contentManager.saveNamespace(Namespace.createFromString("net.tralfamadore.site"));
        List<Namespace> namespaces = contentManager.loadAllNamespaces();
        assertEquals(5, namespaces.size());
        assertEquals("net.tralfamadore.site", namespaces.get(4).getFullName());
        namespaces = contentManager.loadNamespace(Namespace.createFromString("net.tralfamadore"));
        assertEquals(3, namespaces.size());
        assertEquals("net.tralfamadore.site", namespaces.get(2).getFullName());

        contentManager.deleteNamespace(Namespace.createFromString("net.tralfamadore.site"));
        namespaces = contentManager.loadAllNamespaces();
        assertEquals(4, namespaces.size());
        assertEquals("net.tralfamadore", namespaces.get(3).getFullName());
    }

    @Test
    public void testContentMethods() throws Exception {
        contentManager.saveNamespace(Namespace.createFromString("com"));
        contentManager.saveNamespace(Namespace.createFromString("com.site"));

        Content content = makeNewContent("com.site", "theContent", "blah blah blah");
        contentManager.saveContent(content);
        content = makeNewContent("com.site", "moreContent", "unk unk unk");
        contentManager.saveContent(content);
        content = makeNewContent("com", "baseContent", "moo moo moo");
        contentManager.saveContent(content);

        List<Content> contentList = contentManager.loadAllContent();
        assertEquals(3, contentList.size());

        contentList = contentManager.loadContent(Namespace.createFromString("com.site"));
        assertEquals(2, contentList.size());

        content = contentManager.loadContent(Namespace.createFromString("com"), "baseContent");
        assertEquals(content.getContent(), "moo moo moo");

        contentManager.deleteContent(content);
        contentList = contentManager.loadAllContent();
        assertEquals(2, contentList.size());

        content = contentManager.loadContent(Namespace.createFromString("com"), "baseContent");
        assertNull(content);
    }


    @Test
    public void testStyleMethods() throws Exception {
        contentManager.saveNamespace(Namespace.createFromString("com"));
        contentManager.saveNamespace(Namespace.createFromString("com.site"));

        Style style = makeNewStyle("com.site", "theStyle", "blah blah blah");
        contentManager.saveStyle(style);
        style = makeNewStyle("com.site", "moreStyle", "unk unk unk");
        contentManager.saveStyle(style);
        style = makeNewStyle("com", "baseStyle", "moo moo moo");
        contentManager.saveStyle(style);

        List<Style> styleList = contentManager.loadAllStyles();
        assertEquals(3, styleList.size());

        styleList = contentManager.loadStyle(Namespace.createFromString("com.site"));
        assertEquals(2, styleList.size());

        style = contentManager.loadStyle(Namespace.createFromString("com"), "baseStyle");
        assertEquals(style.getStyle(), "moo moo moo");

        contentManager.deleteStyle(style);
        styleList = contentManager.loadAllStyles();
        assertEquals(2, styleList.size());

        style = contentManager.loadStyle(Namespace.createFromString("com"), "baseStyle");
        assertNull(style);
    }

    @Test
    public void testStyleAndContent() throws Exception {
        contentManager.saveNamespace(Namespace.createFromString("com"));
        contentManager.saveNamespace(Namespace.createFromString("com.site"));

        Style style = makeNewStyle("com.site", "theStyle", "bold");
        contentManager.saveStyle(style);
        style = makeNewStyle("com.site", "moreStyle", "red");
        contentManager.saveStyle(style);
        style = makeNewStyle("com", "baseStyle", "Arial");
        contentManager.saveStyle(style);

        Content content = makeNewContent("com.site", "theContent", "blah blah blah");
        contentManager.saveContent(content);
        content = makeNewContent("com.site", "moreContent", "unk unk unk");
        contentManager.saveContent(content);
        content = makeNewContent("com", "baseContent", "moo moo moo");
        contentManager.saveContent(content);

        contentManager.associateWithContent(content, style);
        contentManager.associateWithContent(content, contentManager.loadStyle(Namespace.createFromString("com.site"),
                "moreStyle"));
        List<Style> styles = contentManager.loadStylesForContent(content);
        assertEquals(2, styles.size());

        contentManager.disassociateWithContent(content, style);
        styles = contentManager.loadStylesForContent(content);
        assertEquals(1, styles.size());

        contentManager.associateWithContent(content, style);
        styles = contentManager.loadStylesForContent(content);
        assertEquals(2, styles.size());

        contentManager.deleteStyle(style);
        styles = contentManager.loadStylesForContent(content);
        assertEquals(1, styles.size());

        contentManager.deleteContent(content);
        List<Content> contentList = contentManager.loadAllContent();
        styles = contentManager.loadAllStyles();
        assertEquals(2, contentList.size());
        assertEquals(2, styles.size());
    }

    private Style makeNewStyle(String namespace, String name, String contents) {
        Style style = new Style();
        style.setNamespace(Namespace.createFromString(namespace));
        style.setName(name);
        style.setStyle(contents);
        return style;
    }

    private Content makeNewContent(String namespace, String name, String contents) {
        Content content = new Content();
        content.setNamespace(Namespace.createFromString(namespace));
        content.setName(name);
        content.setContent(contents);
        content.setDateModified(new Date());
        content.setDateCreated(new Date());
        return content;
    }

    @MockClass(realClass = CmfContext.class)
    public static class MockCmfContext {
        ConfigFile configFile;
        EntityManagerProvider entityManagerProvider;
        MockCmfContext() {}

        @Mock
        public EntityManagerProvider getEntityManagerProvider() {
            if(entityManagerProvider == null) {
                entityManagerProvider = new JpaEntityManagerProvider();
            }
            return entityManagerProvider;
        }

        @Mock
        public ConfigFile getConfigFile() {
            return new MockConfigFile();
        }
    }

    public static class MockConfigFile extends ConfigFile {
        Map<String,String> properties = new HashMap<String, String>();

        MockConfigFile() {
            super(false);
            properties.put("javax.persistence.jdbc.driver", "org.apache.derby.jdbc.EmbeddedDriver");
            properties.put("javax.persistence.jdbc.url", "jdbc:derby:memory:cmf;create=true");
        }

        @Override
        public Map<String, String> getPersistenceProperties() {
            return properties;
        }
    }
}
