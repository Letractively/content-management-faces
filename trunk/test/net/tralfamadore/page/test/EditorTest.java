package net.tralfamadore.page.test;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.apache.cactus.ServletTestCase;
import org.jboss.jsfunit.jsfsession.JSFServerSession;
import org.jboss.jsfunit.jsfsession.JSFSession;

import java.io.IOException;

/**
 * User: billreh
 * Date: 1/18/11
 * Time: 2:30 AM
 */
public class EditorTest extends ServletTestCase {
    public static Test suite() {
        return new TestSuite(EditorTest.class);
    }


    public void testEditor() throws IOException {
        JSFSession session = new JSFSession("/contentTest.jsf");
        JSFServerSession serverSession = session.getJSFServerSession();

        assertEquals("/editorTest.xhtml", serverSession.getCurrentViewID());
    }
}
