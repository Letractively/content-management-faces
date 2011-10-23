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

package net.integration;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import net.tralfamadore.cmf.ContentManager;
import net.tralfamadore.cmf.TestContentManager;

import javax.faces.context.FacesContext;

/**
 * User: billreh
 * Date: 10/19/11
 * Time: 10:01 AM
 */
public class TestModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(new TypeLiteral<ContentManager>(){}).toProvider(ContentManagerMockProvider.class);
        bind(FacesContext.class).to(MockFacesContext.class).asEagerSingleton();
    }

    public static class ContentManagerMockProvider implements Provider<ContentManager> {
        @Override
        public ContentManager get() {
            return TestContentManager.getInstance();
        }
    }
}
