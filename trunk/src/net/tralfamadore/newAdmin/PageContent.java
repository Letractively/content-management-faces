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

package net.tralfamadore.newAdmin;

import net.tralfamadore.cmf.BaseContent;
import net.tralfamadore.cmf.Content;
import net.tralfamadore.cmf.Namespace;
import net.tralfamadore.cmf.Style;
import net.tralfamadore.viewScope.ViewScoped;

import javax.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.Vector;

/**
 * User: billreh
 * Date: 10/11/11
 * Time: 8:43 PM
 */
@Named
@ViewScoped
public class PageContent implements Serializable {
    private BaseContent theContent;
    private BaseContent contentToRemove;
    private List<BaseContent> namespaceContents = new Vector<BaseContent>();

    public BaseContent getBaseContent() {
        return theContent;
    }

    public Namespace getNamespace() {
        if(theContent instanceof Namespace)
            return (Namespace) theContent;
        else
            return theContent.getNamespace();
    }

    public Content getContent() {
        return (Content) theContent;
    }

    public Style getStyle() {
        return (Style) theContent;
    }

    public boolean isHasNamespace() {
        return theContent instanceof Namespace;
    }

    public boolean isHasContent() {
        return theContent instanceof Content;
    }

    public boolean isHasStyle() {
        return theContent instanceof Style;
    }

    public boolean isContentHasStyles() {
        return !getContent().getStyles().isEmpty();
    }

    public String getType() {
        return theContent.getClass().getSimpleName().toLowerCase();
    }

    public void setTheContent(BaseContent theContent) {
        this.theContent = theContent;
    }

    public List<BaseContent> getNamespaceContents() {
        return namespaceContents;
    }

    public void setNamespaceContents(List<BaseContent> namespaceContents) {
        this.namespaceContents = namespaceContents;
    }

    public BaseContent getContentToRemove() {
        return contentToRemove;
    }

    public void setContentToRemove(BaseContent contentToRemove) {
        this.contentToRemove = contentToRemove;
    }
}
