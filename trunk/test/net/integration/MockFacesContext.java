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

package net.integration;

import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * User: billreh
 * Date: 10/19/11
 * Time: 12:22 PM
 */
public class MockFacesContext extends FacesContext {
    @Override
    public Application getApplication() {
        return null;
    }

    @Override
    public Iterator<String> getClientIdsWithMessages() {
        return null;
    }

    @Override
    public ExternalContext getExternalContext() {
        return new ExternalContext() {

            @Override
            public void dispatch(String path) throws IOException {
            }

            @Override
            public String encodeActionURL(String url) {
                return null;
            }

            @Override
            public String encodeNamespace(String name) {
                return null;
            }

            @Override
            public String encodeResourceURL(String url) {
                return null;
            }

            @Override
            public Map<String, Object> getApplicationMap() {
                return null;
            }

            @Override
            public String getAuthType() {
                return null;
            }

            @Override
            public Object getContext() {
                return null;
            }

            @Override
            public String getInitParameter(String name) {
                return null;
            }

            @Override
            public Map getInitParameterMap() {
                return null;
            }

            @Override
            public String getRemoteUser() {
                return null;
            }

            @Override
            public Object getRequest() {
                return null;
            }

            @Override
            public String getRequestContextPath() {
                return null;
            }

            @Override
            public Map<String, Object> getRequestCookieMap() {
                return null;
            }

            @Override
            public Map<String, String> getRequestHeaderMap() {
                return null;
            }

            @Override
            public Map<String, String[]> getRequestHeaderValuesMap() {
                return null;
            }

            @Override
            public Locale getRequestLocale() {
                return null;
            }

            @Override
            public Iterator<Locale> getRequestLocales() {
                return null;
            }

            @Override
            public Map<String, Object> getRequestMap() {
                return null;
            }

            @Override
            public Map<String, String> getRequestParameterMap() {
                return null;
            }

            @Override
            public Iterator<String> getRequestParameterNames() {
                return null;
            }

            @Override
            public Map<String, String[]> getRequestParameterValuesMap() {
                return null;
            }

            @Override
            public String getRequestPathInfo() {
                return null;
            }

            @Override
            public String getRequestServletPath() {
                return null;
            }

            @Override
            public URL getResource(String path) throws MalformedURLException {
                return null;
            }

            @Override
            public InputStream getResourceAsStream(String path) {
                return null;
            }

            @Override
            public Set<String> getResourcePaths(String path) {
                return null;
            }

            @Override
            public Object getResponse() {
                return null;
            }

            @Override
            public Object getSession(boolean create) {
                return null;
            }

            @Override
            public Map<String, Object> getSessionMap() {
                return null;
            }

            @Override
            public Principal getUserPrincipal() {
                return null;
            }

            @Override
            public boolean isUserInRole(String role) {
                return false;
            }

            @Override
            public void log(String message) {
            }

            @Override
            public void log(String message, Throwable exception) {
            }

            @Override
            public void redirect(String url) throws IOException {
            }
        };
    }

    @Override
    public FacesMessage.Severity getMaximumSeverity() {
        return null;
    }

    @Override
    public Iterator<FacesMessage> getMessages() {
        return null;
    }

    @Override
    public Iterator<FacesMessage> getMessages(String clientId) {
        return null;
    }

    @Override
    public RenderKit getRenderKit() {
        return null;
    }

    @Override
    public boolean getRenderResponse() {
        return false;
    }

    @Override
    public boolean getResponseComplete() {
        return false;
    }

    @Override
    public ResponseStream getResponseStream() {
        return null;
    }

    @Override
    public void setResponseStream(ResponseStream responseStream) {
    }

    @Override
    public ResponseWriter getResponseWriter() {
        return null;
    }

    @Override
    public void setResponseWriter(ResponseWriter responseWriter) {
    }

    @Override
    public UIViewRoot getViewRoot() {
        return null;
    }

    @Override
    public void setViewRoot(UIViewRoot root) {
    }

    @Override
    public void addMessage(String clientId, FacesMessage message) {
    }

    @Override
    public void release() {
    }

    @Override
    public void renderResponse() {
    }

    @Override
    public void responseComplete() {
    }
}
