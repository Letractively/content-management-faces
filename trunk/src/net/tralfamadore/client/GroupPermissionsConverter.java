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

import net.tralfamadore.cmf.GroupPermissions;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 * User: billreh
 * Date: 2/28/11
 * Time: 1:47 AM
 */
public class GroupPermissionsConverter implements Converter {
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        try {
            Object o = component.getAttributes().get("groupPermissions." + value);
            return o;
        } catch(Exception e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if(value.equals(""))
            return "";
        GroupPermissions groupPermissions = (GroupPermissions) value;
        component.getAttributes().put("groupPermissions." + groupPermissions.getGroup(), groupPermissions);
        return groupPermissions.getGroup();
    }
}
