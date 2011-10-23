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

package net.tralfamadore.security;

import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import net.tralfamadore.admin.util.CurrentUser;
import org.apache.http.auth.BasicUserPrincipal;
import org.junit.Test;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.faces.application.Application;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.security.Principal;

import static junit.framework.Assert.assertEquals;

/**
 * User: billreh
 * Date: 10/21/11
 * Time: 6:18 PM
 */
public class CurrentUserInfoTest {
    @Mocked
    FacesContext facesContext;
    @Mocked
    ELResolver elResolver;
    @Mocked
    ELContext elContext;
    @Mocked
    Application application;
    @Mocked
    ExternalContext externalContext;


    @Test
    public void testLocalCurrentUserInfo() {
        final String userName = "testUser";
        final CurrentUser currentUser = new CurrentUser();
        currentUser.setUser(userName);

        new NonStrictExpectations() {{
            FacesContext.getCurrentInstance(); result = facesContext;
            facesContext.getApplication(); result = application;
            facesContext.getELContext(); result = elContext;
            application.getELResolver(); result = elResolver;
            elResolver.getValue(elContext, null, "currentUser"); result = currentUser;
        }};

        CurrentUserInfo currentUserInfo = new LocalCurrentUserInfo();
        assertEquals(userName, currentUserInfo.getCurrentUser());

        new Verifications() {{
            elResolver.getValue(elContext, null, "currentUser"); times = 1;
        }};
    }

    @Test
    public void testPrincipalCurrentUserInfo() {
        final String userName = "testUser";
        final Principal principal = new BasicUserPrincipal(userName);
        new NonStrictExpectations() {{
            FacesContext.getCurrentInstance(); result = facesContext;
            facesContext.getExternalContext(); result = externalContext;
            externalContext.getUserPrincipal(); result = principal;
        }};

        CurrentUserInfo currentUserInfo = new PrincipalCurrentUserInfo();
        assertEquals(userName, currentUserInfo.getCurrentUser());

        new Verifications() {{
            externalContext.getUserPrincipal(); times = 1;
        }};
    }
}
