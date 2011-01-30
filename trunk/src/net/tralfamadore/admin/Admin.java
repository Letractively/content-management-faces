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

package net.tralfamadore.admin;

import net.tralfamadore.cmf.*;
import net.tralfamadore.persistence.EntityManagerProvider;
import net.tralfamadore.persistence.EntityManagerProviderFactory;
import net.tralfamadore.persistence.entity.ContentEntity;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.persistence.EntityManager;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * User: billreh
 * Date: 1/19/11
 * Time: 1:33 AM
 *
 * The main backing bean used for the admin page (META-INF/resources/admin/index.xhtml and the pages it includes.
 */
@ManagedBean
@SessionScoped
@SuppressWarnings({"UnusedParameters"})
public class Admin {
    /** bean used to hold information about the tree */
    @ManagedProperty(value = "#{tree}")
    private Tree tree;

    /** bean used to hold information about the editor */
    @ManagedProperty(value = "#{editor}")
    private Editor editor;

    /** bean used to hold information about the style editor */
    @ManagedProperty(value = "#{styleScriptEditor}")
    private StyleScriptEditor styleScriptEditor;

    /** the currently selected content node */
    private ContentTreeNode currentNode;

    /** the currently selected style node */
    private StyleTreeNode currentStyleNode;

    /** the content manager */
    private final ContentManager contentManager = TestContentManager.getInstance();


    /* getters and setters */

    public Editor getEditor() {
        return editor;
    }

    public void setEditor(Editor editor) {
        this.editor = editor;
    }

    public StyleScriptEditor getStyleScriptEditor() {
        return styleScriptEditor;
    }

    public void setStyleScriptEditor(StyleScriptEditor styleScriptEditor) {
        this.styleScriptEditor = styleScriptEditor;
    }

    public Tree getTree() {
        return tree;
    }

    public void setTree(Tree tree) {
        this.tree = tree;
    }


    /* action listeners */

    /**
     * Action listener that saves the editor content.
     *
     * @param e event info
     */
    public void saveEditorContent(ActionEvent e) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map requestMap = context.getExternalContext().getRequestParameterMap();
        String value = (String)requestMap.get("ckCode");
        currentNode.getContent().setContent(value);
        contentManager.saveContent(currentNode.getContent());
    }

    /**
     * Action listener that updates the editor content from the currently selected tree node.
     *
     * @param e event info
     */
    public void updateEditorContent(ActionEvent e) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map requestMap = context.getExternalContext().getRequestParameterMap();
        String name = (String) requestMap.get("name");
        String namespace = (String) requestMap.get("namespace");
        if(namespace != null) {
            Namespace ns = Namespace.createFromString(namespace);
            currentNode = tree.getTreeModel().findContentNode(ns, name, tree.getTreeModel().root());
            editor.setValue(currentNode.getContent().getContent());

            addStylesForEditor();
        }
    }

    /**
     * Action listener that deletes the currently selected content.
     *
     * @param e event info
     */
    public void deleteContent(ActionEvent e) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map requestMap = context.getExternalContext().getRequestParameterMap();
        String name = (String) requestMap.get("name");
        String namespace = (String) requestMap.get("namespace");

        if(namespace != null) {
            Content content = contentManager.loadContent(Namespace.createFromString(namespace), name);
            contentManager.deleteContent(content);
            tree.createTreeModel();
        }
    }

    /**
     * Action listener to save namespace.
     *
     * @param e event info
     */
    public void addNamespace(ActionEvent e) {
        Namespace namespace;
        String parentNamespace = tree.getSelectedNamespaceString();

        if(parentNamespace == null)
            namespace = Namespace.createFromString(tree.getNewNamespace());
        else
            namespace = Namespace.createFromString(tree.getSelectedNamespaceString() + tree.getNewNamespace());

        contentManager.saveNamespace(namespace);
        tree.createTreeModel();
        tree.setNewNamespace(null);
    }

    /**
     * Action listener called to update fields before the one of the save namespace/content/style panel are shown.
     *
     * @param e event info
     */
    public void selectNamespace(ActionEvent e) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map requestMap = context.getExternalContext().getRequestParameterMap();
        String value = (String)requestMap.get("namespace");
        tree.setSelectedNamespace(value == null || value.isEmpty() ? null : Namespace.createFromString(value));
    }

    /**
     * Action listener to delete namespace.
     *
     * @param e event info
     */
    public void deleteNamespace(ActionEvent e) {
        selectNamespace(e);

        if(tree.getSelectedNamespace() == null)
            return;

        contentManager.deleteNamespace(tree.getSelectedNamespace());
        tree.createTreeModel();
    }

    /**
     * Action listener to save content.
     *
     * @param e event info
     */
    public void newContent(ActionEvent e) {
        Content content = new Content();
        Date now = new Date();
        content.setDateCreated(now);
        content.setDateModified(now);
        content.setNamespace(tree.getSelectedNamespace());
        content.setName(tree.getNewContentName());
        contentManager.saveContent(content);
        tree.createTreeModel();
        tree.setNewContentName(null);
    }

    /**
     * Action listener to save content.
     *
     * @param e event info
     */
    public void newStyle(ActionEvent e) {
        Style style= new Style();
        style.setNamespace(tree.getSelectedNamespace());
        style.setName(tree.getNewStyleName());
        contentManager.saveStyle(style);
        tree.createTreeModel();
        tree.setNewStyleName(null);
    }

    /**
     * Action listener called to update the style editor before it is shown.
     *
     * @param e event info
     */
    public void updateStyleEditor(ActionEvent e) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map requestMap = context.getExternalContext().getRequestParameterMap();
        String name = (String) requestMap.get("name");
        String namespace = (String) requestMap.get("namespace");
        if(namespace != null) {
            Namespace ns = Namespace.createFromString(namespace);
            currentStyleNode = tree.getTreeModel().findStyleNode(ns, name, tree.getTreeModel().root());
            styleScriptEditor.setValue(currentStyleNode.getStyle().getStyle());
        }
    }

    /**
     * Action listener to save style editor content.
     *
     * @param e event info
     */
    public void saveStyleEditor(ActionEvent e) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map requestMap = context.getExternalContext().getRequestParameterMap();
        String value = (String)requestMap.get("cssEditor");
        currentStyleNode.getStyle().setStyle(value);
        contentManager.saveStyle(currentStyleNode.getStyle());
    }

    /**
     * Action listener to delete a style.
     *
     * @param e event info
     */
    public void deleteStyle(ActionEvent e) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map requestMap = context.getExternalContext().getRequestParameterMap();
        String name = (String) requestMap.get("name");
        String namespace = (String) requestMap.get("namespace");

        if(namespace != null) {
            Style style = contentManager.loadStyle(Namespace.createFromString(namespace), name);
            contentManager.deleteStyle(style);
            tree.createTreeModel();
            addStylesForEditor();
        }
    }

    /**
     * Action listener to associate a style with content.
     *
     * @param e event info
     */
    public void associateStyle(ActionEvent e) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map requestMap = context.getExternalContext().getRequestParameterMap();
        String name = (String) requestMap.get("name");
        String namespace = (String) requestMap.get("namespace");

        if(namespace != null) {
            Style style = contentManager.loadStyle(Namespace.createFromString(namespace), name);
            Content content = currentNode.getContent();
            contentManager.associateWithContent(content, style);
            styleScriptEditor.setValue(style.getStyle());

            addStylesForEditor();
        }
    }


    /* helper methods */

    private void addStylesForEditor() {
        List<Style> styles = editor.getStyles();
        styles.clear();
        editor.clearCurrentStyles();
        for(Style style : contentManager.loadStylesForContent(currentNode.getContent())) {
            editor.addCurrentStyle(style.getStyle());
            styles.add(style);
        }


    }

    public void loadStyleEditor(ActionEvent e) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map requestMap = context.getExternalContext().getRequestParameterMap();
        String name = (String) requestMap.get("name");
        String namespace = (String) requestMap.get("namespace");

        currentStyleNode = tree.getTreeModel().findStyleNode(Namespace.createFromString(namespace),
                name, tree.getTreeModel().root());
        styleScriptEditor.setValue(currentStyleNode.getStyle().getStyle());
    }

    @SuppressWarnings({"unchecked"})
    public void testEntityManager(ActionEvent e) {
        EntityManagerProviderFactory emProviderFactory = EntityManagerProviderFactory.getInstance();
        EntityManagerProvider emProvider = emProviderFactory.get();
        EntityManager em = emProvider.get();

        String s = ((ContentEntity)em.createQuery("select c from content c").getSingleResult()).getContent();

        System.out.println("success: " + s);
    }
}
