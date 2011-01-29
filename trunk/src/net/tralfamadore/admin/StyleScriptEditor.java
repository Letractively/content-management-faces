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
import javax.faces.component.UIComponent;
import javax.faces.component.UIPanel;
import java.io.Serializable;
import java.util.List;
import java.util.Vector;

/**
 * User: billreh
 * Date: 1/19/11
 * Time: 3:27 AM
 */
@ManagedBean
@SessionScoped
public class StyleScriptEditor implements Serializable {
    private UIPanel styles;
    private String value;


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public UIPanel getStyles() {
        List<String> ids = new Vector<String>();
        List<UIComponent> children = new Vector<UIComponent>();
        if(styles != null) {
            for(UIComponent c : styles.getChildren()) {
                if(ids.contains(c.getId()))
                    continue;
                ids.add(c.getId());
                children.add(c);
            }
            styles.getChildren().clear();
            for(UIComponent c : children) {
                styles.getChildren().add(c);
            }
        }
        return styles;
    }

    public void setStyles(UIPanel styles) {
        this.styles = styles;
    }
}
