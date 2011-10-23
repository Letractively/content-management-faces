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

package net.tralfamadore.config;

import mockit.Mocked;
import mockit.NonStrictExpectations;
import org.junit.Test;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

/**
 * User: billreh
 * Date: 10/22/11
 * Time: 3:58 AM
 */
public class ConfigFileTest {
    @Mocked
    FacesContext facesContext;
    @Mocked
    ExternalContext externalContext;

    @Test
    public void testConfigFile() throws Exception {
        File cfg = new File("test/net/tralfamadore/config/test-cmf-config.xml");

        final InputStream inputStream = new FileInputStream(cfg);

        try {
            new NonStrictExpectations() {{
                FacesContext.getCurrentInstance(); result = facesContext;
                facesContext.getExternalContext(); result = externalContext;
                externalContext.getResourceAsStream(anyString); result = inputStream;
            }};

            ConfigFile configFile = new ConfigFile();

            assertNull(configFile.getCustomLoginUrl());
            assertEquals("net.tralfamadore.cmf.JpaContentManager", configFile.getContentManager());
            assertEquals("net.tralfamadore.persistence.JpaEntityManagerProvider", configFile.getEntityManagerProvider());
            assertEquals("jdbc:postgresql://localhost/cmf5", configFile.getPersistenceProperties().get("javax" +
                    ".persistence.jdbc.url"));
            assertEquals("postgres", configFile.getPersistenceProperties().get("javax.persistence.jdbc.user"));
            assertEquals("postgres", configFile.getPersistenceProperties().get("javax.persistence.jdbc.password"));
            assertEquals("org.postgresql.Driver", configFile.getPersistenceProperties().get("javax.persistence.jdbc" +
                    ".driver"));
        } finally {
            inputStream.close();
        }
    }
}
