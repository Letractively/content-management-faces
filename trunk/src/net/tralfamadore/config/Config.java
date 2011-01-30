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

import net.tralfamadore.cmf.TestContentManager;
import net.tralfamadore.persistence.JpaEntityManagerProvider;

import java.util.Map;

/**
 * User: billreh
 * Date: 1/30/11
 * Time: 10:02 AM
 */
public class Config {
    private ConfigFile configFile;
    private boolean initialized = false;

    /**
     * A private class to hold our single instance.
     */
    private static class ConfigHolder {
        public static final Config instance = new Config();
    }

    /**
     * Called to get a reference to the singleton instance.
     *
     * @return the instance.
     */
    public static Config getInstance() {
        return ConfigHolder.instance;
    }

    private Config() { }

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

    public String getContentManager() {
        String contentManager = configFile.getContentManager();

        if(contentManager == null)
            contentManager = TestContentManager.class.getName();

        return contentManager;
    }

    public String getEntityManagerProvider() {
        String entityManagerProvider = configFile.getEntityManagerProvider();

        if(entityManagerProvider == null)
            entityManagerProvider = JpaEntityManagerProvider.class.getName();

        return entityManagerProvider;
    }

    public Map<String, String> getPersistenceProperties() {
        return configFile.getPersistenceProperties();
    }
}
