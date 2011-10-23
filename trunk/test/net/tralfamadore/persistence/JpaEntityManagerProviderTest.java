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

package net.tralfamadore.persistence;

import mockit.Cascading;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import net.tralfamadore.cmf.JpaContentManager;
import net.tralfamadore.config.CmfContext;
import net.tralfamadore.config.ConfigFile;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.junit.Test;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.File;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * User: billreh
 * Date: 10/22/11
 * Time: 8:05 PM
 */
public class JpaEntityManagerProviderTest {
    @Mocked @Cascading
    JpaEntityManager jpaEntityManager;
    @Mocked @Cascading
    JpaContentManager contentManager;
    @Mocked
    CmfContext cmfContext;
    @Mocked
    EntityManagerFactory entityManagerFactory;
    @Mocked
    Persistence persistence;
    @Mocked
    FacesContext facesContext;
    @Mocked
    ExternalContext externalContext;


    @Test
    public void testGetWithEntityManager() throws Exception {
        JpaEntityManagerProvider jpaEntityManagerProvider = new JpaEntityManagerProvider();
        jpaEntityManagerProvider.em = jpaEntityManager;
        assertEquals(jpaEntityManager, jpaEntityManagerProvider.get());
    }

    @Test
    public void testGetWithoutEntityManager() throws Exception {
        final ConfigFile config = new ConfigFile(false);
        config.getPersistenceProperties().put("javax.persistence.jdbc.driver", "org.apache.derby.jdbc.EmbeddedDriver");
        config.getPersistenceProperties().put("javax.persistence.jdbc.url", "jdbc:derby:cmf;create=true");

        new NonStrictExpectations() {{
            CmfContext.getInstance(); result = cmfContext;
            cmfContext.getConfigFile(); result = config;
            Persistence.createEntityManagerFactory(EntityManagerProvider.PERSISTENCE_UNIT_NAME,
                    config.getPersistenceProperties()); result = entityManagerFactory;
            entityManagerFactory.createEntityManager(); result = jpaEntityManager;
        }};

        JpaEntityManagerProvider jpaEntityManagerProvider = new JpaEntityManagerProvider();
        assertEquals(jpaEntityManager, jpaEntityManagerProvider.get());

        new Verifications() {{
            cmfContext.setEmbeddedDbNeedsConfig(true); times = 0;
            cmfContext.setInMemory(true); times = 0;
        }};
    }

    @Test
    public void testGetWithoutEntityManagerEmbeddedMem() throws Exception {
        final ConfigFile config = new ConfigFile(false);
        config.getPersistenceProperties().put("javax.persistence.jdbc.driver", "org.apache.derby.jdbc.EmbeddedDriver");
        config.getPersistenceProperties().put("javax.persistence.jdbc.url", "jdbc:derby:memory:cmf;create=true");

        new NonStrictExpectations() {{
            CmfContext.getInstance(); result = cmfContext;
            cmfContext.getConfigFile(); result = config;
            Persistence.createEntityManagerFactory(EntityManagerProvider.PERSISTENCE_UNIT_NAME,
                    config.getPersistenceProperties()); result = entityManagerFactory;
            entityManagerFactory.createEntityManager(); result = jpaEntityManager;
        }};

        JpaEntityManagerProvider jpaEntityManagerProvider = new JpaEntityManagerProvider();
        assertEquals(jpaEntityManager, jpaEntityManagerProvider.get());

        new Verifications() {{
            cmfContext.setEmbeddedDbNeedsConfig(true); times = 1;
            cmfContext.setInMemory(true); times = 1;
        }};
    }

    @Test
    public void testCreateEmbeddedDb() throws Exception {
        final Properties properties = new Properties();
        properties.put("javax.persistence.jdbc.url", "cmf.tested");

        new NonStrictExpectations() {{
            CmfContext.getInstance(); result = cmfContext;
            cmfContext.getContentManager(); result = contentManager;
            Persistence.createEntityManagerFactory(EntityManagerProvider.PERSISTENCE_UNIT_NAME, properties);
                result = entityManagerFactory;
            entityManagerFactory.createEntityManager(); result = jpaEntityManager;
            FacesContext.getCurrentInstance(); result = facesContext;
            facesContext.getExternalContext(); result = externalContext;
            externalContext.getRealPath(anyString); result = "test/net/tralfamadore/config/test-cmf-config.xml";
        }};

        JpaEntityManagerProvider jpaEntityManagerProvider = new JpaEntityManagerProvider();
        jpaEntityManagerProvider.createEmbeddedDb(properties);

        // put the file back in its original state
        File file = new File("test/net/tralfamadore/config/test-cmf-config.xml");
        String newUrl = "jdbc:derby:memory:cmf;create=true";
        String contents = JpaEntityManagerProvider.readFileAsString(file);
        assertTrue(contents.indexOf("cmf.tested") > 0);
        contents = contents.replaceAll("cmf.tested", newUrl);
        JpaEntityManagerProvider.writeStringToFile(file, contents);
    }
}
