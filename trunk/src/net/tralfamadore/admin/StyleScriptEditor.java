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
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package net.tralfamadore.admin;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;

/**
 * User: billreh
 * Date: 1/19/11
 * Time: 3:27 AM
 *
 * This managed bean is a backing bean for the admin page and is also used by the {@link Admin} managed bean.  It
 * represents the current contents of the style editor.
 */
@ManagedBean
@SessionScoped
public class StyleScriptEditor implements Serializable {
    /** the contents of the style editor */
    private String value;


    /* getters and setters */

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
