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

import net.tralfamadore.cmf.ContentManager;
import net.tralfamadore.cmf.TestContentManager;
import net.tralfamadore.persistence.EntityManagerProvider;
import net.tralfamadore.security.SecurityType;

/**
 * User: billreh
 * Date: 1/30/11
 * Time: 10:02 AM
 */
public class CmfContext {
    private ConfigFile configFile;
    private boolean initialized = false;
    private ContentManager contentManager;
    private EntityManagerProvider entityManagerProvider;
    private SecurityType securityType = SecurityType.NONE;
    private boolean embeddedDbNeedsConfig = false;
    private String customLoginUrl;

    public String getCustomLoginUrl() {
        return configFile.getCustomLoginUrl();
    }

    public String getCurrentUser() {
        return securityType.getCurrentUserInfo().getCurrentUser();
    }

    /**
     * A private class to hold our single INSTANCE.
     */
    private static class CmfContextHolder {
        public static final CmfContext INSTANCE = new CmfContext();
    }

    /**
     * Called to get a reference to the singleton INSTANCE.
     *
     * @return the INSTANCE.
     */
    public static CmfContext getInstance() {
        return CmfContextHolder.INSTANCE;
    }

    private CmfContext() { }

    public ConfigFile getConfigFile() {
        return configFile;
    }

    public void setConfigFile(ConfigFile configFile) {
        this.configFile = configFile;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public boolean isEmbeddedDbNeedsConfig() {
        return embeddedDbNeedsConfig;
    }

    public void setEmbeddedDbNeedsConfig(boolean embeddedDbNeedsConfig) {
        this.embeddedDbNeedsConfig = embeddedDbNeedsConfig;
    }

    public SecurityType getSecurityType() {
        return securityType;
    }

    public void setSecurityType(SecurityType securityType) {
        this.securityType = securityType;
    }

    public ContentManager getContentManager() {
        if(contentManager == null) {
            String contentManagerName = configFile.getContentManager();

            try {
                contentManager =
                        (ContentManager) getClass().getClassLoader().loadClass(contentManagerName).newInstance();
            } catch(Exception e) {
                e.printStackTrace();
                contentManager = TestContentManager.getInstance();
            }
        }

        return contentManager;
    }

    public EntityManagerProvider getEntityManagerProvider() {
        if(entityManagerProvider == null) {
            String entityManagerProviderName = configFile.getEntityManagerProvider();
            try {
                entityManagerProvider =(EntityManagerProvider)
                        getClass().getClassLoader().loadClass(entityManagerProviderName).newInstance();
            } catch(Exception e) {
                throw new RuntimeException(e);
            }
        }

        return entityManagerProvider;
    }
}