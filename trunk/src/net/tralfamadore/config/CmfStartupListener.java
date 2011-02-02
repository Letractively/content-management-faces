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

import javax.faces.event.AbortProcessingException;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;

/**
 * User: billreh
 * Date: 1/30/11
 * Time: 10:00 AM
 */
public class CmfStartupListener implements SystemEventListener {
    @Override
    public void processEvent(SystemEvent systemEvent) throws AbortProcessingException {
        CmfContext cmfContext = CmfContext.getInstance();
        if(!cmfContext.isInitialized()) {
            ConfigFile configFile = new ConfigFile();
            cmfContext.setConfigFile(configFile);
            cmfContext.setInitialized(true);
        }
    }

    @Override
    public boolean isListenerForSource(Object o) {
        return !CmfContext.getInstance().isInitialized();
    }
}
