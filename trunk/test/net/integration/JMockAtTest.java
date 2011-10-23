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

import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import net.tralfamadore.admin.AdminController;
import net.tralfamadore.admin.PageContent;
import net.tralfamadore.cmf.Namespace;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.faces.context.FacesContext;
import javax.inject.Inject;

//import org.jmock.Expectations;
//import org.jmock.Mockery;

/**
 * User: billreh
 * Date: 10/19/11
 * Time: 9:01 AM
 */
@RunWith(GuiceIntegration.class)
public class JMockAtTest {
    @Inject
    AdminController adminController;

    @Mocked
    FacesContext facesContext;

    @Mocked
    PageContent pageContent;

    @Test
    public void testStart() throws Exception {
        final Namespace parent = Namespace.createFromString("net.tralfamadore");

        new NonStrictExpectations() {
            {
                pageContent.getNamespace(); result = parent;
                pageContent.getNamespaceToAdd(); result = Namespace.createFromString("net.tralfamadore.site");
            }
        };

        adminController.addNewNamespace();

        new Verifications() {
            {
                pageContent.getNamespace(); times = 1;
                pageContent.setNamespaceToAdd(new Namespace(parent, "")); times = 1;
                pageContent.getNamespaceToAdd(); times = 1;
                pageContent.setAddingNamespace(true); times = 1;
            }
        };
    }
}
