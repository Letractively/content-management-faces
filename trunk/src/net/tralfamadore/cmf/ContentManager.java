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

import net.tralfamadore.persistence.entity.GroupEntity;

import java.util.List;

/**
 * User: billreh
 * Date: 1/19/11
 * Time: 6:15 PM
 *
 * This interface represents the link between your content and the outside world.  It is where the content is retrieved
 * from /written to the data store.
 */
public interface ContentManager {
    /**
     * Load a list of all namespaces.
     *
     * @return List of all of the {@link Namespace} objects.
     */
    public List<Namespace> loadAllNamespaces();

    /**
     * Load a list of all namespaces under and including <code>namespace</code>.
     * @param namespace The {@link Namespace} object to search under.
     *
     * @return List of all of the {@link Namespace} objects under and including <code>namespace</code>.
     */
    public List<Namespace> loadNamespace(Namespace namespace);

    /**
     * Save a namespace.
     *
     * @param namespace The namespace to save.
     */
    public void saveNamespace(Namespace namespace);

    /**
     * Delete a namespace.
     *
     * @param namespace The namespace to delete.
     */
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

    public void saveGroup(GroupEntity group);
    public List<Namespace> loadChildNamespaces(Namespace namespace);
}
