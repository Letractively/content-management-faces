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

import java.util.*;

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

    /** mapping of content namespace with content name appended to list of styles for that content */
    private final Map<String,List<Style>> styleToContent = new HashMap<String, List<Style>>();
    /** mapping of content namespace with content name appended to list of styles for that content */
    private final Map<Content,List<Script>> scriptToContent = new HashMap<Content, List<Script>>();


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

        for(Map.Entry<String, List<Style>> entry : this.styleToContent.entrySet()) {
            it = entry.getValue().iterator();
            //noinspection WhileLoopReplaceableByForEach
            while(it.hasNext()) {
                Style c = it.next();
                if(c.getNamespace().equals(style.getNamespace()) && c.getName().equals(style.getName())) {
                    entry.getValue().remove(style);
                    break;
                }
            }
        }
    }

    /**
     * @see ContentManager#associateWithContent(Content, Script)
     */
    @Override
    public void associateWithContent(Content content, Script script) {
        // First update the content object
        for(Script s : content.getScripts()) {
            boolean found = false;
            if(s.getNamespace().equals(script.getNamespace()) && s.getName().equals(script.getName())) {
                s.setScript(script.getScript());
                found = true;
            }
            // need to add it
            if(!found)
                content.getScripts().add(script);
        }

        List<Script> scriptsForContent = scriptToContent.get(content);

        // If nothing is there, put it there
        if(scriptsForContent == null) {
            scriptsForContent = new Vector<Script>();
            scriptsForContent.add(script);
            scriptToContent.put(content, scriptsForContent);
            return;
        }

        // If it's there, update it
        for(Script s : scriptsForContent) {
            if(s.getNamespace().equals(script.getNamespace()) && s.getName().equals(script.getName())) {
                s.setScript(script.getScript());
                return;
            }
        }

        // otherwise add it
        scriptsForContent.add(script);
    }

    /**
     * @see ContentManager#loadScriptsForContent(Content)
     */
    @Override
    public List<Script> loadScriptsForContent(Content content) {
        List<Script> scripts = content == null ? null : scriptToContent.get(loadContent(content.getNamespace(), content.getName()));
        return scripts == null ? new Vector<Script>() : scripts;
    }

    /**
     * @see ContentManager#associateWithContent(Content, Style)
     */
    @Override
    public void associateWithContent(Content content, Style style) {
        // First update the content object
        for(Style s : content.getStyles()) {
            boolean found = false;
            if(s.getNamespace().equals(style.getNamespace()) && s.getName().equals(style.getName())) {
                s.setStyle(style.getStyle());
                found = true;
            }
            // need to add it
            if(!found)
                content.getStyles().add(style);
        }

        List<Style> stylesForContent = styleToContent.get(content.getNamespace().getFullName() + '.' + content.getName());

        // If nothing is there, put it there
        if(stylesForContent == null) {
            stylesForContent = new Vector<Style>();
            stylesForContent.add(style);
            styleToContent.put(content.getNamespace().getFullName() + '.' + content.getName(), stylesForContent);
            return;
        }

        // If it's there, update it
        for(Style s : stylesForContent) {
            if(s.getNamespace().equals(style.getNamespace()) && s.getName().equals(style.getName())) {
                s.setStyle(style.getStyle());
                return;
            }
        }

        // otherwise add it
        stylesForContent.add(style);
    }

    /**
     * @see ContentManager#loadStylesForContent(Content)
     */
    @Override
    public List<Style> loadStylesForContent(Content content) {
        List<Style> styles = content == null ? null : styleToContent.get(content.getNamespace().getFullName() + '.' + content.getName());
        return styles == null ? new Vector<Style>() : styles;
    }

    /**
     * @see ContentManager#disassociateWithContent(Content, Script)
     */
    @Override
    public void disassociateWithContent(Content content, Script script) {
        // First update the content object
        for(Script s : content.getScripts()) {
            boolean found = false;
            if(s.getNamespace().equals(script.getNamespace()) && s.getName().equals(script.getName())) {
                found = true;
            }
            // need to remove it
            if(found)
                content.getScripts().remove(script);
        }

        List<Script> scriptsForContent = scriptToContent.get(content);

        // it's not there
        if(scriptsForContent == null)
            return;

        // If it's there, remove it
        Iterator<Script> it = scriptsForContent.iterator();
        //noinspection WhileLoopReplaceableByForEach
        while(it.hasNext()) {
            Script s = it.next();
            if(s.getNamespace().equals(script.getNamespace()) && s.getName().equals(script.getName())) {
                scriptsForContent.remove(s);
                return;
            }
        }
    }

    /**
     * @see ContentManager#disassociateWithContent(Content, Style)
     */
    @Override
    public void disassociateWithContent(Content content, Style style) {
        // First update the content object
        for(Style s : content.getStyles()) {
            boolean found = false;
            if(s.getNamespace().equals(style.getNamespace()) && s.getName().equals(style.getName())) {
                found = true;
            }
            // need to remove it
            if(found)
                content.getStyles().remove(style);
        }

        List<Style> stylesForContent = styleToContent.get(content.getNamespace().getFullName() + '.' + content.getName());

        // it's not there
        if(stylesForContent == null)
            return;

        // If it's there, remove it
        Iterator<Style> it = stylesForContent.iterator();
        //noinspection WhileLoopReplaceableByForEach
        while(it.hasNext()) {
            Style s = it.next();
            if(s.getNamespace().equals(style.getNamespace()) && s.getName().equals(style.getName())) {
                stylesForContent.remove(s);
                return;
            }
        }
    }
}
