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

package net.tralfamadore.cmf;

import java.util.List;

/**
 * User: billreh
 * Date: 1/19/11
 * Time: 6:15 PM
 */
public interface ContentManager {
    public List<Namespace> loadAllNamespaces();
    public List<Namespace> loadNamespace(Namespace namespace);
    public void saveNamespace(Namespace namespace);
    public void deleteNamespace(Namespace namespace);

    public List<Content> loadAllContent();
    public List<Content> loadContent(Namespace namespace);
    public Content loadContent(Namespace namespace, String name);
    public void saveContent(Content content);
    public void deleteContent(Content content);

    public List<Script> loadAllScripts();
    public List<Script> loadScript(Namespace namespace);
    public Script loadScript(Namespace namespace, String name);
    public void saveScript(Script script);
    public void deleteScript(Script script);

    public List<Style> loadAllStyles();
    public List<Style> loadStyle(Namespace namespace);
    public Style loadStyle(Namespace namespace, String name);
    public void saveStyle(Style style);
    public void deleteStyle(Style style);

    public void associateWithContent(Content content, Script script);
    public List<Script> loadScriptsForContent(Content content);
    public void disassociateWithContent(Content content, Script script);

    public void associateWithContent(Content content, Style style);
    public List<Style> loadStylesForContent(Content content);
    public void disassociateWithContent(Content content, Style style);
}
