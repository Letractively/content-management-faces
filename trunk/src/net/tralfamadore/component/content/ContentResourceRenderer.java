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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import javax.faces.render.Renderer;
import java.io.IOException;

/**
 * User: billreh
 * Date: 1/6/11
 * Time: 11:45 AM
 */
@FacesRenderer(componentFamily = "javax.faves.Output", rendererType = "ContentResource")
public class ContentResourceRenderer extends Renderer {
    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ContentResource contentResource = (ContentResource) component;
        if("script".equals(contentResource.getType()))
            encodeScript(context, contentResource);
        else if("style".equals(contentResource.getType()))
            encodeStyle(context, contentResource);
    }

    protected void encodeStyle(FacesContext context, ContentResource component) throws IOException {
        ResponseWriter responseWriter = context.getResponseWriter();
        responseWriter.write("<style type=\"text/css\">");
        responseWriter.write(component.getContent());
        responseWriter.write("</style>");
        responseWriter.flush();
    }


    protected void encodeScript(FacesContext context, ContentResource component) throws IOException {
        ResponseWriter responseWriter = context.getResponseWriter();
        responseWriter.write("<script type=\"text/javascript\">");
        responseWriter.write(component.getContent());
        responseWriter.write("</script>");
        responseWriter.flush();
    }
}
