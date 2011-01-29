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

import net.tralfamadore.cmf.ContentManager;
import net.tralfamadore.cmf.Namespace;
import net.tralfamadore.cmf.TestContentManager;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import javax.faces.render.Renderer;
import java.io.IOException;

/**
 * User: billreh
 * Date: 1/19/11
 * Time: 3:01 AM
 */
@FacesRenderer(rendererType = "Content", componentFamily = "javax.faces.Output")
public class ContentRenderer extends Renderer {
    protected final ContentManager contentManager = TestContentManager.getInstance();
    private String content;

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        content = getContent((Content)component);
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        if(content == null)
            super.encodeChildren(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter responseWriter = context.getResponseWriter();
        if(content != null) {
            responseWriter.startElement("span", component);
            responseWriter.write(content);
            responseWriter.endElement("span");
            content = null;
        }
        responseWriter.flush();
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    /**
     * Retrieve the content from wherever we're storing it.
     *
     * @param content The content component
     *
     * @return The found content, or null.
     */
    protected String getContent(Content content) {
        Namespace namespace = Namespace.createFromString(content.getNamespace());
        String name = content.getName();
        net.tralfamadore.cmf.Content c = contentManager.loadContent(namespace, name);
        return c == null ? null : c.getContent();
    }
}
