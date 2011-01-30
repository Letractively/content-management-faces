package net.tralfamadore.cmf;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * User: billreh
 * Date: 1/19/11
 * Time: 7:40 PM
 */
public class TestContentManagerTest {
    @Test
    public void testNamespace() {
        ContentManager contentManager = TestContentManager.getInstance();

        // save namespace
        Namespace namespace = Namespace.createFromString("net.tralfamadore.site.page1");
        contentManager.saveNamespace(namespace);
        assertTrue(contentManager.loadNamespace(namespace).contains(namespace));

        //delete namespace
        contentManager.deleteNamespace(namespace);
        assertFalse(contentManager.loadNamespace(namespace).contains(namespace));

        // save new namespace
        Namespace namespace1 = Namespace.createFromString("net.tralfamadore.site.page1.subSection");
        contentManager.saveNamespace(namespace1);
        namespace = Namespace.createFromString("net.tralfamadore.site.page1");

        // both namespaces should show up here, along with all parents
        List<Namespace> ns = contentManager.loadNamespace(namespace);
        assertTrue(ns.contains(namespace1));
        assertTrue(ns.contains(namespace));
        assertTrue(ns.contains(Namespace.createFromString("net.tralfamadore.site")));
        assertTrue(ns.contains(Namespace.createFromString("net.tralfamadore")));
        assertTrue(ns.contains(Namespace.createFromString("net")));
    }

    @Test
    public void testContent() {
        ContentManager contentManager = TestContentManager.getInstance();
        Namespace namespace = Namespace.createFromString("net.tralfamadore.site.page1");

        // save content
        Content content = new Content();
        content.setNamespace(namespace);
        content.setName("field1");
        content.setContent("Contents of field1");
        contentManager.saveContent(content);
        assertEquals(content, contentManager.loadContent(namespace, "field1"));

        // update content
        content = new Content();
        content.setNamespace(namespace);
        content.setName("field1");
        content.setContent("New Contents of field1");
        contentManager.saveContent(content);
        assertEquals(content, contentManager.loadContent(namespace, "field1"));

        // save some more
        Content newContent = new Content();
        newContent.setNamespace(namespace);
        newContent.setName("field2");
        newContent.setContent("Contents of field2");
        contentManager.saveContent(newContent);
        assertEquals(2, contentManager.loadContent(namespace).size());
        assertTrue(contentManager.loadContent(namespace).contains(content));
        assertTrue(contentManager.loadContent(namespace).contains(newContent));

        // delete it
        contentManager.deleteContent(newContent);
        assertEquals(1, contentManager.loadContent(namespace).size());
        assertTrue(contentManager.loadContent(namespace).contains(content));
        assertFalse(contentManager.loadContent(namespace).contains(newContent));
        assertNull(contentManager.loadContent(namespace, "field2"));
    }

    @Test
    public void testScript() {
        ContentManager contentManager = TestContentManager.getInstance();
        Namespace namespace = Namespace.createFromString("net.tralfamadore.site.page1");

        // save script
        Script script = new Script();
        script.setNamespace(namespace);
        script.setName("field1");
        script.setScript("Script of field1");
        contentManager.saveScript(script);
        assertEquals(script, contentManager.loadScript(namespace, "field1"));

        // update content
        script = new Script();
        script.setNamespace(namespace);
        script.setName("field1");
        script.setScript("New Script of field1");
        contentManager.saveScript(script);
        assertEquals(script, contentManager.loadScript(namespace, "field1"));

        // save some more
        Script newScript = new Script();
        newScript.setNamespace(namespace);
        newScript.setName("field2");
        newScript.setScript("Script of field2");
        contentManager.saveScript(newScript);
        assertEquals(2, contentManager.loadScript(namespace).size());
        assertTrue(contentManager.loadScript(namespace).contains(script));
        assertTrue(contentManager.loadScript(namespace).contains(newScript));

        // delete it
        contentManager.deleteScript(newScript);
        assertEquals(1, contentManager.loadScript(namespace).size());
        assertTrue(contentManager.loadScript(namespace).contains(script));
        assertFalse(contentManager.loadScript(namespace).contains(newScript));
        assertNull(contentManager.loadScript(namespace, "field2"));
    }

    @Test
    public void testStyle() {
        ContentManager contentManager = TestContentManager.getInstance();
        Namespace namespace = Namespace.createFromString("net.tralfamadore.site.page1");

        // save style
        Style style = new Style();
        style.setNamespace(namespace);
        style.setName("field1");
        style.setStyle("Style of field1");
        contentManager.saveStyle(style);
        assertEquals(style, contentManager.loadStyle(namespace, "field1"));

        // update style
        style = new Style();
        style.setNamespace(namespace);
        style.setName("field1");
        style.setStyle("New Style of field1");
        contentManager.saveStyle(style);
        assertEquals(style, contentManager.loadStyle(namespace, "field1"));

        // save some more
        Style newStyle = new Style();
        newStyle.setNamespace(namespace);
        newStyle.setName("field2");
        newStyle.setStyle("Style of field2");
        contentManager.saveStyle(newStyle);
        assertEquals(2, contentManager.loadStyle(namespace).size());
        assertTrue(contentManager.loadStyle(namespace).contains(style));
        assertTrue(contentManager.loadStyle(namespace).contains(newStyle));

        // delete it
        contentManager.deleteStyle(newStyle);
        assertEquals(1, contentManager.loadStyle(namespace).size());
        assertTrue(contentManager.loadStyle(namespace).contains(style));
        assertFalse(contentManager.loadStyle(namespace).contains(newStyle));
        assertNull(contentManager.loadStyle(namespace, "field2"));
    }

    @Test
    public void testScriptToContent() {
        ContentManager contentManager = TestContentManager.getInstance();
        Namespace namespace = Namespace.createFromString("net.tralfamadore.site.page1");

        // save content
        Content content = new Content();
        content.setNamespace(namespace);
        content.setName("field1");
        content.setContent("New Contents of field1");
        contentManager.saveContent(content);
        assertEquals(content, contentManager.loadContent(namespace, "field1"));

        // save script
        Script script = new Script();
        script.setNamespace(namespace);
        script.setName("field1");
        script.setScript("Script of field1");
        contentManager.saveScript(script);
        assertEquals(script, contentManager.loadScript(namespace, "field1"));

        contentManager.associateWithContent(content, script);
        assertEquals(1, contentManager.loadScriptsForContent(content).size());
        assertTrue(contentManager.loadScriptsForContent(content).contains(script));

        // save new script
        Script newScript = new Script();
        newScript.setNamespace(namespace);
        newScript.setName("field2");
        newScript.setScript("Script of field2");
        contentManager.saveScript(newScript);
        assertEquals(script, contentManager.loadScript(namespace, "field1"));
        assertEquals(newScript, contentManager.loadScript(namespace, "field2"));

        contentManager.associateWithContent(content, newScript);
        assertEquals(2, contentManager.loadScriptsForContent(content).size());
        assertTrue(contentManager.loadScriptsForContent(content).contains(script));
        assertTrue(contentManager.loadScriptsForContent(content).contains(newScript));

        contentManager.disassociateWithContent(content, newScript);
        assertEquals(1, contentManager.loadScriptsForContent(content).size());
        assertTrue(contentManager.loadScriptsForContent(content).contains(script));
        assertFalse(contentManager.loadScriptsForContent(content).contains(newScript));
    }

    @Test
    public void testStyleToContent() {
        ContentManager contentManager = TestContentManager.getInstance();
        Namespace namespace = Namespace.createFromString("net.tralfamadore.site.page1");

        // save content
        Content content = new Content();
        content.setNamespace(namespace);
        content.setName("field1");
        content.setContent("New Contents of field1");
        contentManager.saveContent(content);
        assertEquals(content, contentManager.loadContent(namespace, "field1"));

        // save style
        Style style = new Style();
        style.setNamespace(namespace);
        style.setName("field1");
        style.setStyle("Style of field1");
        contentManager.saveStyle(style);
        assertEquals(style, contentManager.loadStyle(namespace, "field1"));

        contentManager.associateWithContent(content, style);
        assertEquals(1, contentManager.loadStylesForContent(content).size());
        assertTrue(contentManager.loadStylesForContent(content).contains(style));

        // save new style
        Style newStyle = new Style();
        newStyle.setNamespace(namespace);
        newStyle.setName("field2");
        newStyle.setStyle("Style of field2");
        contentManager.saveStyle(newStyle);
        assertEquals(style, contentManager.loadStyle(namespace, "field1"));
        assertEquals(newStyle, contentManager.loadStyle(namespace, "field2"));

        contentManager.associateWithContent(content, newStyle);
        assertEquals(2, contentManager.loadStylesForContent(content).size());
        assertTrue(contentManager.loadStylesForContent(content).contains(style));
        assertTrue(contentManager.loadStylesForContent(content).contains(newStyle));

        contentManager.disassociateWithContent(content, newStyle);
        assertEquals(1, contentManager.loadStylesForContent(content).size());
        assertTrue(contentManager.loadStylesForContent(content).contains(style));
        assertFalse(contentManager.loadStylesForContent(content).contains(newStyle));
    }
}
