package net.tralfamadore.cmf;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * User: billreh
 * Date: 1/18/11
 * Time: 11:06 PM
 */
public class NamespaceTest {
    @Test
    public void testNamespace() {
        // test static create, nodeName() and fullName()
        Namespace namespace = Namespace.createFromString("net.tralfamadore.editor");
        assertEquals("net.tralfamadore.editor", namespace.getFullName());
        assertEquals("editor", namespace.getNodeName());

        // test two arg constructor, create, nodeName() and fullName()
        Namespace namespace2 = new Namespace(namespace, "subNode");
        assertEquals("net.tralfamadore.editor.subNode", namespace2.getFullName());
        assertEquals("subNode", namespace2.getNodeName());


        // test the equals
        assertNotSame(namespace, namespace2);
        namespace = Namespace.createFromString(namespace2.getFullName());
        assertEquals(namespace, namespace2);

        List<Namespace> namespaces = namespace.getParentNamespaces();
        assertEquals(4, namespaces.size());
        Namespace ns = Namespace.createFromString("net");
        assertTrue(namespaces.contains(ns));
        ns = new Namespace(ns, "tralfamadore");
        assertTrue(namespaces.contains(ns));
        ns = new Namespace(ns, "editor");
        assertTrue(namespaces.contains(ns));
        ns = new Namespace(ns, "subNode");
        assertTrue(namespaces.contains(ns));

        // test one arg constructor, create, nodeName() and fullName()
        namespace = new Namespace("subNode");
        assertNull(namespace.getParent());
        assertEquals("subNode", namespace.getFullName());
        assertEquals("subNode", namespace.getNodeName());

        // test no arg constructor, create, nodeName() and fullName()
        namespace = new Namespace();
        assertNull(namespace.getNodeName());
        assertNull(namespace.getParent());
        assertNull(namespace.getFullName());
    }
}
