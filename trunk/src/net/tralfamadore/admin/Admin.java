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

import javax.el.MethodExpression;
import javax.faces.FacesException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.behavior.AjaxBehavior;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.MethodExpressionActionListener;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

/**
 * User: billreh
 * Date: 1/19/11
 * Time: 1:33 AM
 */
@ManagedBean
@SessionScoped
public class Admin {
    @ManagedProperty(value = "#{tree}")
    private Tree tree;
    @ManagedProperty(value = "#{editor}")
    private Editor editor;
    @ManagedProperty(value = "#{styleScriptEditor}")
    private StyleScriptEditor styleScriptEditor;
    private ContentTreeNode currentNode;
    private StyleTreeNode currentStyleNode;


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

    public void saveEditorContent(ActionEvent e) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map requestMap = context.getExternalContext().getRequestParameterMap();
        String value = (String)requestMap.get("ckCode");
        currentNode.getContent().setContent(value);
        ContentManager contentManager = TestContentManager.getInstance();
        contentManager.saveContent(currentNode.getContent());
    }

    public void updateEditorContent(ActionEvent e) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map requestMap = context.getExternalContext().getRequestParameterMap();
        String name = (String) requestMap.get("name");
        String namespace = (String) requestMap.get("namespace");
        if(namespace != null) {
            Namespace ns = Namespace.createFromString(namespace);
            currentNode = tree.getTreeModel().findContentNode(ns, name, tree.getTreeModel().root());
            editor.setValue(currentNode.getContent().getContent());
            editor.clearHiddenStyles();

            addStylesForEditor();
        }
    }

    public void deleteContent(ActionEvent e) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map requestMap = context.getExternalContext().getRequestParameterMap();
        String name = (String) requestMap.get("name");
        String namespace = (String) requestMap.get("namespace");

        if(namespace != null) {
            ContentManager contentManager = TestContentManager.getInstance();
            Content content = contentManager.loadContent(Namespace.createFromString(namespace), name);
            contentManager.deleteContent(content);
            tree.createTreeModel();
        }
    }

    public void addNamespace(ActionEvent e) {
        Namespace namespace;
        String parentNamespace = tree.getSelectedNamespaceString();

        if(parentNamespace == null)
            namespace = Namespace.createFromString(tree.getNewNamespace());
        else
            namespace = Namespace.createFromString(tree.getSelectedNamespaceString() + tree.getNewNamespace());

        ContentManager contentManager = TestContentManager.getInstance();
        contentManager.saveNamespace(namespace);
        tree.createTreeModel();
        tree.setNewNamespace(null);
    }


    public void selectNamespace(ActionEvent e) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map requestMap = context.getExternalContext().getRequestParameterMap();
        String value = (String)requestMap.get("namespace");
        tree.setSelectedNamespace(value == null || value.isEmpty() ? null : Namespace.createFromString(value));
    }

    public void deleteNamespace(ActionEvent e) {
        selectNamespace(e);

        if(tree.getSelectedNamespace() == null)
            return;

        ContentManager contentManager = TestContentManager.getInstance();
        contentManager.deleteNamespace(tree.getSelectedNamespace());
        tree.createTreeModel();
    }

    public void newContent(ActionEvent e) {
        Content content = new Content();
        Date now = new Date();
        content.setDateCreated(now);
        content.setDateModified(now);
        content.setNamespace(tree.getSelectedNamespace());
        content.setName(tree.getNewContentName());
        ContentManager contentManager = TestContentManager.getInstance();
        contentManager.saveContent(content);
        tree.createTreeModel();
        tree.setNewContentName(null);
    }

    public void newStyle(ActionEvent e) {
        Style style= new Style();
        style.setNamespace(tree.getSelectedNamespace());
        style.setName(tree.getNewStyleName());
        ContentManager contentManager = TestContentManager.getInstance();
        contentManager.saveStyle(style);
        tree.createTreeModel();
        tree.setNewContentName(null);
    }

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

    public void saveStyleEditor(ActionEvent e) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map requestMap = context.getExternalContext().getRequestParameterMap();
        String value = (String)requestMap.get("cssEditor");
        currentStyleNode.getStyle().setStyle(value);
        ContentManager contentManager = TestContentManager.getInstance();
        contentManager.saveStyle(currentStyleNode.getStyle());
    }

    public void deleteStyle(ActionEvent e) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map requestMap = context.getExternalContext().getRequestParameterMap();
        String name = (String) requestMap.get("name");
        String namespace = (String) requestMap.get("namespace");

        if(namespace != null) {
            ContentManager contentManager = TestContentManager.getInstance();
            Style style = contentManager.loadStyle(Namespace.createFromString(namespace), name);
            contentManager.deleteStyle(style);
            tree.createTreeModel();
        }
    }

    public void associateStyle(ActionEvent e) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map requestMap = context.getExternalContext().getRequestParameterMap();
        String name = (String) requestMap.get("name");
        String namespace = (String) requestMap.get("namespace");

        if(namespace != null) {
            ContentManager contentManager = TestContentManager.getInstance();
            Style style = contentManager.loadStyle(Namespace.createFromString(namespace), name);
            Content content = currentNode.getContent();
            contentManager.associateWithContent(content, style);
            HtmlCommandButton newStyle = createStyleButton(style);
            newStyle.setTransient(true);
            addStyle(newStyle);
            styleScriptEditor.setValue(style.getStyle());

            addStylesForEditor();
        }
    }

    private void addStylesForEditor() {
        TestContentManager contentManager = TestContentManager.getInstance();
        editor.clearHiddenStyles();
        for(Style style : contentManager.loadStylesForContent(currentNode.getContent())) {
            editor.addHiddenStyle(style.getStyle());
        }
    }

    private void addStyle(HtmlCommandButton newStyle) {
        boolean found = false;
        for(UIComponent c : styleScriptEditor.getStyles().getChildren()) {
            if(c.getId().equals(newStyle.getId())) {
                found = true;
                break;
            }
        }

        if(!found)
            styleScriptEditor.getStyles().getChildren().add(newStyle);
        styleScriptEditor.getStyles().getChildren().add(newStyle);
    }

    private HtmlCommandButton createStyleButton(Style style) {
        FacesContext context = FacesContext.getCurrentInstance();
        HtmlCommandButton button = new HtmlCommandButton();
        AjaxBehavior ajaxBehavior = new AjaxBehavior();
        MethodExpression methodExpression = context.getApplication().getExpressionFactory().createMethodExpression(
                context.getELContext(), "#{admin.loadStyleEditor}", null, new Class[] { ActionEvent.class });

        button.setValue(style.getName());
        button.setStyleClass("styleBubble");
        button.setId(style.getNamespace().getFullName().replaceAll("\\.", "_") + "-" + style.getName());
        button.addActionListener(new MethodExpressionActionListener(methodExpression));
        ajaxBehavior.setRender(Arrays.asList("cssEditor", "stylePanel", "hiddenStyles"));
        ajaxBehavior.setOnevent("doTheStyleThing2");
        button.addClientBehavior("action", ajaxBehavior);

        return button;
    }

    @SuppressWarnings({"unchecked"})
    public void loadStyleEditor(ActionEvent e) {
        String id = e.getComponent().getId();
        String[] namespaceAndName = id.split("-");
        if(namespaceAndName == null || namespaceAndName.length != 2)
            throw new FacesException("cannot determine style from id [" + id + "]");

        Namespace ns = Namespace.createFromString(namespaceAndName[0].replaceAll("_", "."));
        currentStyleNode = tree.getTreeModel().findStyleNode(ns, namespaceAndName[1], tree.getTreeModel().root());
        styleScriptEditor.setValue(currentStyleNode.getStyle().getStyle());
    }
}
