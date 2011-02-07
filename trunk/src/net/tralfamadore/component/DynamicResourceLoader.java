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

package net.tralfamadore.component;

import javax.faces.application.Resource;
import javax.faces.context.FacesContext;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: billreh
 * Date: 2/7/11
 * Time: 5:33 AM
 *
 * This class provides functionality to dynamically create new jsf resources during runtime.
 */
public class DynamicResourceLoader {
    private static final String BASE_RESOURCE_PATH = "/WEB-INF/classes/META-INF/resources";

    private static Map<String,Integer> resourceMap = new HashMap<String, Integer>();


    /**
     * Creates a jsf resource file with the given <code>content</code>, <code>name</code>, and <code>library</code>,
     * creating the library and a file resource if need be, and filling it with the contents of the
     * <code>content</code> String.
     *
     * This method will "cache" the content, only changing the file contents if the hashCode of the <code>content</code>
     * String has changed.
     *
     * @param context The current FacesContext
     * @param library The library name (will be created if it does not exist)
     * @param name The resource name (will be created if it does not exist)
     * @param content The content to go in the resource.
     *
     * @throws IOException If there's some sort of IO Exception :)
     *
     * @return The name of the resource location such as "com-mysite-siteStyle.css.jsf?ln=cmfDynamicResources".  This
     * can be put into the href/src attribute of a style/script tag in the header to point to the resource.
     */
    public static String encodeDynamicResource(FacesContext context, String library, String name, String content)
            throws IOException
    {
        // Get the path to the resources directory
        String path = context.getExternalContext().getRealPath(BASE_RESOURCE_PATH);
        File file = new File(path);

        // Create the library if it doesn't exist
        File base = new File(file, library);
        if(!base.exists()) {
            //noinspection ResultOfMethodCallIgnored
            base.mkdirs();
        }

        /* get the hash code of the content and any stored version of the content we have */
        Integer hash = resourceMap.get(name);
        Integer componentHashCode = content.hashCode();
        /*
           we prepend the hash code of the content String to make the name unique because jsf (or possibly glassfish?)
           caches it when it changes even when we don't want it to.  The response header changes to indicate that the
           server should serve up a the file anew, but it serves up the old content anyway.  Note that if the resource
           contents haven't changed, this will stay the same (i.e. we aren't constantly recreating the file, we
           only make/change it when the content changes)
        */
        String resourceName = componentHashCode + name;

        // if the hash code of the string has changed, remove the old resource and make the new one
        if(hash == null || !hash.equals(componentHashCode)) {
            File oldResource = new File(base, hash + name);
            if(oldResource.exists()) {
                //noinspection ResultOfMethodCallIgnored
                oldResource.delete();
            }

            File resource = new File(base, resourceName);
            if(!resource.exists()) {
                //noinspection ResultOfMethodCallIgnored
                resource.createNewFile();
            }

            writeStringToFile(resource, content);
            resourceMap.put(name, content.hashCode());
        }

        Resource res = context.getApplication().getResourceHandler().createResource(resourceName, library);

        if(res != null) {
            res.getResponseHeaders().entrySet();
            return res.getRequestPath();
        }

        return null;
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
