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

import net.tralfamadore.config.CmfContext;
import net.tralfamadore.config.ConfigFile;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

/**
 * User: billreh
 * Date: 1/30/11
 * Time: 9:06 AM
 */
public class JpaEntityManagerProvider implements EntityManagerProvider {
    private EntityManager em;
    private EntityManagerFactory emFactory;


    @Override
    public void shutdown() {
        if(em != null)
            em.close();

        if(emFactory != null)
            emFactory.close();
    }

    @Override
    public EntityManager get() {
        if(em == null) {
            ConfigFile configFile = CmfContext.getInstance().getConfigFile();

            Map<String,String> properties = new HashMap<String,String>();

            for(Map.Entry<String,String> entry : configFile.getPersistenceProperties().entrySet()) {
                properties.put(entry.getKey(), entry.getValue());
            }

            emFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, properties);
            em = emFactory.createEntityManager();
        }

        return em;
    }
}
