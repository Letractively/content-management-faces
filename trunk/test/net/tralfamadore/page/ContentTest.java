package net.tralfamadore.page;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.apache.cactus.ServletTestCase;
import org.jboss.jsfunit.jsfsession.JSFClientSession;
import org.jboss.jsfunit.jsfsession.JSFServerSession;
import org.jboss.jsfunit.jsfsession.JSFSession;

import java.io.IOException;

/**
 * User: billreh
 * Date: 1/18/11
 * Time: 2:30 AM
 */
public class ContentTest extends ServletTestCase {
    public static Test suite() {
        return new TestSuite(ContentTest.class);
    }


    public void testAdmin() throws IOException {
        JSFSession session = new JSFSession("/cmf/admin/index.jsf");
        JSFServerSession serverSession = session.getJSFServerSession();
        JSFClientSession clientSession = session.getJSFClientSession();

        clientSession.setValue("derbyPath", "/Users/billreh/tmpDb");
        clientSession.click("submitDerbyPath");

        assertTrue(clientSession.getPageAsText().contains("Success"));
    }
}
