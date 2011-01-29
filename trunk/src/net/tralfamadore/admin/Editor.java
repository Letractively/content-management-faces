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
 * Date: 1/18/11
 * Time: 2:28 AM
 */
@ManagedBean
@SessionScoped
public class Editor implements Serializable {
    private String value;
    private String hiddenStyles;


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void addHiddenStyle(String style) {
        hiddenStyles = hiddenStyles + " " + style;
    }

    public void clearHiddenStyles() {
        hiddenStyles = "";
    }

    public String getHiddenStyles() {
        return hiddenStyles;
    }

    public void setHiddenStyles(String hiddenStyles) {
        this.hiddenStyles = hiddenStyles;
    }
}
