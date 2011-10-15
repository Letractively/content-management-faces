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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * User: billreh
 * Date: 1/19/11
 * Time: 6:26 PM
 *
 * A test implementation of the {@link ContentManager} interface which keeps all content in memory.
 */
public class TestContentManager implements ContentManager {
    /** list of all namespaces */
    private final List<Namespace> namespaces = new Vector<Namespace>();

    /** list of all content */
    private final List<Content> contentList = new Vector<Content>();

    /** list of all scripts */
    private final List<Script> scripts = new Vector<Script>();

    /** list of all styles */
    private final List<Style> styles = new Vector<Style>();

    /**
     * A private class to hold our single INSTANCE.
     */
    private static class SingletonHolder {
        public static final TestContentManager INSTANCE = new TestContentManager();
    }

    /**
     * Called to get a reference to the singleton INSTANCE.
     *
     * @return the INSTANCE.
     */
    public static TestContentManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private TestContentManager() { }

    /**
     * @see ContentManager#loadAllNamespaces()
     */
    @Override
    public List<Namespace> loadAllNamespaces() {
        return namespaces;
    }

    /**
     * @see ContentManager#loadNamespace(Namespace)
     */
    @Override
    public List<Namespace> loadNamespace(Namespace namespace) {
        List<Namespace> namespacesForNamespace = new Vector<Namespace>();

        for(Namespace n : namespaces) {
                namespacesForNamespace.add(n);
                Namespace tmp = n.getParent();
                Namespace ns = tmp;
                while(tmp != null) {
                    if(tmp.equals(namespace)) {
                        Namespace nss = ns;
                        while(ns != null) {
                            namespacesForNamespace.add(ns);
                            ns = ns.getParent();
                        }
                        ns = nss;
                    }
                    tmp = tmp.getParent();
                }
            }

        return namespacesForNamespace;
    }

    /**
     * @see ContentManager#saveNamespace(Namespace)
     */
    @Override
    public void saveNamespace(Namespace namespace) {
        if(!namespaces.contains(namespace))
            namespaces.add(namespace);
    }

    /**
     * @see ContentManager#deleteNamespace(Namespace)
     */
    @Override
    public void deleteNamespace(Namespace namespace) {
        List<Content> found = loadContent(namespace);
        if(!found.isEmpty()) {
            throw new RuntimeException("Namespace " + namespace + " not empty: " + found);
        }

        Iterator<Namespace> it = namespaces.iterator();
        //noinspection WhileLoopReplaceableByForEach
        while(it.hasNext()) {
            Namespace n = it.next();
            if(n.getFullName().equals(namespace.getFullName())) {
                namespaces.remove(n);
                return;
            }
        }
    }

    /**
     * @see ContentManager#loadAllContent()
     */
    @Override
    public List<Content> loadAllContent() {
        return contentList;
    }

    /**
     * @see ContentManager#loadContent(Namespace)
     */
    @Override
    public List<Content> loadContent(Namespace namespace) {
        List<Content> contentForNamespace = new Vector<Content>();

        for(Content content : contentList)
            if(namespace.equals(content.getNamespace()))
                contentForNamespace.add(content);

        return contentForNamespace;
    }

    /**
     * @see ContentManager#loadContent(Namespace, String)
     */
    @Override
    public Content loadContent(Namespace namespace, String name) {
        for(Content content : contentList)
            if(namespace.equals(content.getNamespace()) && name.equals(content.getName()))
                return content;

        return null;
    }

    /**
     * @see ContentManager#saveContent(Content)
     */
    @Override
    public void saveContent(Content content) {
        for(Content c : contentList) {
            if(content.getNamespace().equals(c.getNamespace()) && content.getName().equals(c.getName())) {
                content.setContent(c.getContent());
                return;
            }
        }

        contentList.add(content);
    }

    /**
     * @see ContentManager#deleteContent(Content)
     */
    @Override
    public void deleteContent(Content content) {
        Iterator<Content> it = contentList.iterator();
        //noinspection WhileLoopReplaceableByForEach
        while(it.hasNext()) {
            Content c = it.next();
            if(c.getNamespace().equals(content.getNamespace()) && c.getName().equals(content.getName())) {
                contentList.remove(content);
                return;
            }
        }
    }

    /**
     * @see ContentManager#loadAllScripts()
     */
    @Override
    public List<Script> loadAllScripts() {
        return scripts;
    }

    /**
     * @see ContentManager#loadScript(Namespace)
     */
    @Override
    public List<Script> loadScript(Namespace namespace) {
        List<Script> scriptsForNamespace = new Vector<Script>();

        for(Script script : scripts)
            if(namespace.equals(script.getNamespace()))
                scriptsForNamespace.add(script);

        return scriptsForNamespace;
    }

    /**
     * @see ContentManager#loadScript(Namespace, String)
     */
    @Override
    public Script loadScript(Namespace namespace, String name) {
        for(Script script : scripts)
            if(namespace.equals(script.getNamespace()) && name.equals(script.getName()))
                return script;

        return null;
    }

    /**
     * @see ContentManager#saveScript(Script)
     */
    @Override
    public void saveScript(Script script) {
        for(Script s : scripts) {
            if(script.getNamespace().equals(s.getNamespace()) && script.getName().equals(s.getName())) {
                script.setScript(s.getScript());
                return;
            }
        }

        scripts.add(script);
    }

    /**
     * @see ContentManager#deleteScript(Script)
     */
    @Override
    public void deleteScript(Script script) {
        Iterator<Script> it = scripts.iterator();
        //noinspection WhileLoopReplaceableByForEach
        while(it.hasNext()) {
            Script c = it.next();
            if(c.getNamespace().equals(script.getNamespace()) && c.getName().equals(script.getName())) {
                scripts.remove(script);
                return;
            }
        }
    }

    /**
     * @see ContentManager#loadAllStyles()
     */
    @Override
    public List<Style> loadAllStyles() {
        return styles;
    }

    /**
     * @see ContentManager#loadStyle(Namespace)
     */
    @Override
    public List<Style> loadStyle(Namespace namespace) {
        List<Style> stylesForNamespace = new Vector<Style>();

        for(Style style : styles)
            if(namespace.equals(style.getNamespace()))
                stylesForNamespace.add(style);

        return stylesForNamespace;
    }

    /**
     * @see ContentManager#loadStyle(Namespace, String)
     */
    @Override
    public Style loadStyle(Namespace namespace, String name) {
        for(Style style : styles)
            if(namespace.equals(style.getNamespace()) && name.equals(style.getName()))
                return style;

        return null;
    }

    /**
     * @see ContentManager#saveStyle(Style)
     */
    @Override
    public void saveStyle(Style style) {
        for(Style s : styles) {
            if(style.getNamespace().equals(s.getNamespace()) && style.getName().equals(s.getName())) {
                style.setStyle(s.getStyle());
                return;
            }
        }

        styles.add(style);
    }

    /**
     * @see ContentManager#deleteStyle(Style)
     */
    @Override
    public void deleteStyle(Style style) {
        Iterator<Style> it = styles.iterator();
        //noinspection WhileLoopReplaceableByForEach
        while(it.hasNext()) {
            Style c = it.next();
            if(c.getNamespace().equals(style.getNamespace()) && c.getName().equals(style.getName())) {
                styles.remove(style);
                break;
            }
        }
    }

    @Override
    public void saveGroup(GroupEntity group) {
        throw new RuntimeException("Implement me!");
    }

    @Override
    public List<Namespace> loadChildNamespaces(Namespace namespace) {
        return null;
    }

    @Override
    public List<String> getAllGroups() {
        return Arrays.asList("cmfAdmin", "billreh", "users", "user1", "user2", "user3");
    }
}
