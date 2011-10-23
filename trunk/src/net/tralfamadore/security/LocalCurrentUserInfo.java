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

import net.tralfamadore.admin.util.CurrentUser;

import javax.el.ELResolver;
import javax.faces.context.FacesContext;

/**
 * User: billreh
 * Date: 2/11/11
 * Time: 8:28 PM
 */
public class LocalCurrentUserInfo implements CurrentUserInfo {

    @Override
    public String getCurrentUser() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ELResolver elResolver = FacesContext.getCurrentInstance().getApplication().getELResolver();
        Object o = elResolver.getValue(facesContext.getELContext(), null, "currentUser");
        CurrentUser user = (CurrentUser) o;

//        facesContext.getExternalContext().getSessionMap().get("cmfCurrentUser");

        return user == null ? null : user.getUser();
    }
}
