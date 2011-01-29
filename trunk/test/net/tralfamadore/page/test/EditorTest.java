package net.tralfamadore.page.test;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.apache.cactus.ServletTestCase;
import org.jboss.jsfunit.jsfsession.JSFClientSession;
import org.jboss.jsfunit.jsfsession.JSFServerSession;
import org.jboss.jsfunit.jsfsession.JSFSession;

import javax.faces.component.UIInput;
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
        JSFSession session = new JSFSession("/editorTest.jsf");
        JSFServerSession serverSession = session.getJSFServerSession();
        JSFClientSession clientSession = session.getJSFClientSession();

        assertEquals("/editorTest.xhtml", serverSession.getCurrentViewID());

        clientSession.setValue("input", "Bob rules");
        clientSession.click("button");

        assertEquals("/editorTest.xhtml", serverSession.getCurrentViewID());

        UIInput input = (UIInput) serverSession.findComponent("input");
        assertEquals("Bob rules", serverSession.getManagedBeanValue("#{editor.value}"));
        assertEquals("Bob rules", serverSession.getManagedBeanValue("#{admin.editor.value}"));
        assertEquals("Bob rules", clientSession.getElement("input").getAttribute("value"));
        assertEquals("Bob rules", input.getValue());
    }
}
