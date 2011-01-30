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

import net.tralfamadore.cmf.Style;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.List;
import java.util.Vector;

/**
 * User: billreh
 * Date: 1/18/11
 * Time: 2:28 AM
 *
 * This managed bean is a backing bean for the admin page and is also used by the {@link Admin} managed bean.  It
 * represents the current contents of the editor and the styles associated with it.
 */
@ManagedBean
@SessionScoped
public class Editor implements Serializable {
    /** the content value in the editor */
    private String value;

    /** styles that the current content in the editor is using */
    private String currentStyles;

    private List<Style> styles = new Vector<Style>();


    /* getters and setters */

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCurrentStyles() {
        return currentStyles;
    }

    public void setCurrentStyles(String currentStyles) {
        this.currentStyles = currentStyles;
    }

    public List<Style> getStyles() {
        return styles;
    }

    public void setStyles(List<Style> styles) {
        this.styles = styles;
    }

    /**
     * Clear the list of styles.
     */
    public void clearCurrentStyles() {
        currentStyles = "";
    }

    /**
     * Add a style to the list of current styles.
     *
     * @param style the css to add.
     */
    public void addCurrentStyle(String style) {
        currentStyles = currentStyles + " " + style;
    }
}
