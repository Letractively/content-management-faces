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

import javax.faces.application.Resource;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.FacesRenderer;
import javax.faces.render.Renderer;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: billreh
 * Date: 1/6/11
 * Time: 11:45 AM
 */
@FacesRenderer(componentFamily = "javax.faves.Output", rendererType = "ContentResource")
public class ContentResourceRenderer extends Renderer {
    private static final String BASE_RESOURCE_PATH = "/WEB-INF/classes/META-INF/resources";
    private static final String resourceLibrary = "cmfDynamicResources";

    private static Map<String,Integer> resourceMap = new HashMap<String, Integer>();


    public static void encodeDynamicResource(FacesContext context, ContentResource component) throws IOException {
        String path = context.getExternalContext().getRealPath(BASE_RESOURCE_PATH);
        File file = new File(path);

        File base = new File(file, resourceLibrary);
        if(!base.exists()) {
            //noinspection ResultOfMethodCallIgnored
            base.mkdirs();
        }

        String name = component.getNamespace().replaceAll("\\.", "-")+ "-" + component.getName() +
                ("style".equals(component.getType()) ? ".css" : ".js");

        File resource = new File(base, name);
        if(!resource.exists()) {
            //noinspection ResultOfMethodCallIgnored
            resource.createNewFile();
        }
        Integer hash = resourceMap.get(name);
        Integer componentHashCode = component.getContent().hashCode();
        if(hash == null || !hash.equals(componentHashCode)) {
            writeStringToFile(resource, component.getContent());
            resourceMap.put(name, component.getContent().hashCode());
        }

        Resource res = context.getApplication().getResourceHandler().createResource(name, resourceLibrary);
        path = res.getRequestPath();

        context.getResponseWriter().write("<link type=\"text/css\" rel=\"stylesheet\" href=\"" + path + "\"/>");
    }


    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        encodeDynamicResource(context, (ContentResource) component);
    }


    private static void writeStringToFile(File file, String string) throws java.io.IOException{
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            outputStream.write(string.getBytes());
        } finally {
            try { if(outputStream != null)outputStream.close(); } catch(Exception ignore) {}
        }

    }
}
