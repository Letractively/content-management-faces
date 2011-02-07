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

package net.tralfamadore.component.content;

import javax.faces.component.FacesComponent;
import javax.faces.component.UIOutput;

/**
 * User: billreh
 * Date: 1/6/11
 * Time: 11:48 AM
 */
@FacesComponent(value = "ContentResource")
public class ContentResource extends UIOutput {

    protected enum PropertyKeys {
        type,
        content,
        name,
        namespace
    }

    public String getType() {
        return (String) getStateHelper().eval(PropertyKeys.type);
    }

    public void setType(String type) {
        getStateHelper().put(PropertyKeys.type, type);
    }

    public String getContent() {
        return (String) getStateHelper().eval(PropertyKeys.content);
    }

    public void setContent(String content) {
        getStateHelper().put(PropertyKeys.content, content);
    }

    public String getName() {
        return (String) getStateHelper().eval(PropertyKeys.name);
    }

    public void setName(String name) {
        getStateHelper().put(PropertyKeys.name, name);
    }

    public String getNamespace() {
        return (String) getStateHelper().eval(PropertyKeys.namespace);
    }

    public void setNamespace(String namespace) {
        getStateHelper().put(PropertyKeys.namespace, namespace);
    }
}

