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

package net.tralfamadore.client;

import atunit.*;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provider;
import net.tralfamadore.admin.AdminController;
import net.tralfamadore.admin.PageContent;
import net.tralfamadore.cmf.ContentManager;
import net.tralfamadore.cmf.Namespace;
import net.tralfamadore.cmf.TestContentManager;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.easymock.EasyMock.*;

/**
 * User: billreh
 * Date: 10/19/11
 * Time: 9:01 AM
 */
@RunWith(AtUnit.class)
@Container(Container.Option.GUICE)
@MockFramework(MockFramework.Option.EASYMOCK)
public class AtTest implements Module {
    @Inject @Unit
    AdminController adminController;

    @Mock
    PageContent pageContent;


    @Override
    public void configure(Binder b) {
        b.install(new TestModule());
    }

    public static class ContentManagerMockProvider implements Provider<ContentManager> {
        @Override
        public ContentManager get() {
            return TestContentManager.getInstance();
        }
    }

    @Ignore
    @Test
    public void testStart() throws Exception {
        Namespace parent = Namespace.createFromString("net.tralfamadore");
        expect(pageContent.getNamespace()).andReturn(parent);
        expect(pageContent.setNamespaceToAdd(new Namespace(parent, ""))).andReturn(null);
        expect(pageContent.getNamespaceToAdd()).andReturn(Namespace.createFromString("net.tralfamadore.site"));
        expect(pageContent.setAddingNamespace(true)).andReturn(null);
        replay(pageContent);
        adminController.addNewNamespace();
        verify(pageContent);
    }
}
