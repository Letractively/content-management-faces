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

import net.tralfamadore.config.Config;

/**
 * User: billreh
 * Date: 1/30/11
 * Time: 6:25 AM
 */
public class EntityManagerProviderFactory {
    private EntityManagerProvider entityManagerProvider;

    private static class EntityManagerProviderFactoryHolder {
        private static final EntityManagerProviderFactory INSTANCE = new EntityManagerProviderFactory();
    }

    public static EntityManagerProviderFactory getInstance() {
        return EntityManagerProviderFactoryHolder.INSTANCE;
    }

    private EntityManagerProviderFactory() { }


    @SuppressWarnings({"unchecked"})
    public EntityManagerProvider get() {
        if(entityManagerProvider == null) {
            String className = Config.getInstance().getEntityManagerProvider();
            try {
                Class<?> clazz = getClass().getClassLoader().loadClass(className);
                entityManagerProvider = (EntityManagerProvider) clazz.newInstance();
            } catch(Exception e) {
                throw new RuntimeException(e);
            }
        }

        return entityManagerProvider;
    }
}
