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

import net.tralfamadore.admin.tree.ContentTreeNode;
import net.tralfamadore.admin.tree.StyleTreeNode;
import net.tralfamadore.cmf.*;
import net.tralfamadore.config.CmfContext;
import net.tralfamadore.persistence.JpaEntityManagerProvider;
import net.tralfamadore.security.SecurityPhaseListener;

import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.security.Principal;
import java.security.Provider;
import java.security.Security;
import java.util.*;

/**
 * User: billreh
 * Date: 1/19/11
 * Time: 1:33 AM
 *
 * The main backing bean used for the admin page (META-INF/resources/admin/index.xhtml) and the pages it includes.
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

    /** the context manager */
    private final CmfContext cmfContext = CmfContext.getInstance();

    /** the content manager */
    private final ContentManager contentManager = cmfContext.getContentManager();

    /** true if we need to configure the embedded database */
    private boolean embeddedDbNeedsConfig = cmfContext.isEmbeddedDbNeedsConfig();

    /** the path to the embedded db directory */
    private String derbyPath;


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

    public boolean isEmbeddedDbNeedsConfig() {
        return embeddedDbNeedsConfig;
    }

    public void setEmbeddedDbNeedsConfig(boolean embeddedDbNeedsConfig) {
        this.embeddedDbNeedsConfig = embeddedDbNeedsConfig;
    }

    public String getDerbyPath() {
        return derbyPath;
    }

    public void setDerbyPath(String derbyPath) {
        this.derbyPath = derbyPath;
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
        String curUser = CmfContext.getInstance().getCurrentUser();
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
     * Action listener to delete a style.
     *
     * @param e event info
     */
    public void disassociateStyle(ActionEvent e) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map requestMap = context.getExternalContext().getRequestParameterMap();
        String name = (String) requestMap.get("name");
        String namespace = (String) requestMap.get("namespace");

        if(namespace != null) {
            Style style = contentManager.loadStyle(Namespace.createFromString(namespace), name);
            contentManager.disassociateWithContent(currentNode.getContent(), style);
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

    /**
     * Action listener that is called when the style editor is loaded.
     *
     * @param e event info
     */
    public void loadStyleEditor(ActionEvent e) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map requestMap = context.getExternalContext().getRequestParameterMap();
        String name = (String) requestMap.get("name");
        String namespace = (String) requestMap.get("namespace");

        currentStyleNode = tree.getTreeModel().findStyleNode(Namespace.createFromString(namespace),
                name, tree.getTreeModel().root());
        styleScriptEditor.setValue(currentStyleNode.getStyle().getStyle());
    }

    /**
     * Action listener that creates the embedded database from the initial screen.
     *
     * @param e event info
     */
    public void createEmbeddedDb(ActionEvent e) {
        String jdbc = "jdbc:derby:";
        String props = ";create=true";
        Properties properties = new Properties();

        properties.put("javax.persistence.jdbc.driver", "org.apache.derby.jdbc.EmbeddedDriver");
        properties.put("javax.persistence.jdbc.url", jdbc + derbyPath + props);

        File file = new File(derbyPath);
        if(!file.getParentFile().exists()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Invalid path: " + derbyPath + ".  Directory " + file.getParentFile().getAbsolutePath()
                            + " does not exist.", null));
            derbyPath = null;
            throw new FacesException();
        }

        ((JpaEntityManagerProvider)this.cmfContext.getEntityManagerProvider()).createEmbeddedDb(properties);

        embeddedDbNeedsConfig = false;

        tree.createTreeModel();
    }

    public void resetDb(ActionEvent e) {
        Principal principal = FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();

        if(principal != null)
            System.out.println(principal.getName());
        else
            System.out.println("not logged in");

        JpaEntityManagerProvider jpaEmp = (JpaEntityManagerProvider) this.cmfContext.getEntityManagerProvider();
        jpaEmp.dropEmbeddedTables();
        jpaEmp.createEmbeddedTables();
    }


    /* helper methods */

    /**
     * Load styles for the current content node.
     */
    private void addStylesForEditor() {
        List<Style> styles = editor.getStyles();
        styles.clear();
        editor.clearCurrentStyles();
        for(Style style : contentManager.loadStylesForContent(currentNode.getContent())) {
            editor.addCurrentStyle(style.getStyle());
            styles.add(style);
        }


    }

    public static HttpServletRequest getRequest() {
        Object request = FacesContext.getCurrentInstance().getExternalContext().getRequest();
        return request instanceof HttpServletRequest
                ? (HttpServletRequest) request : null;
    }

    private PhaseListener thePhaseListener = new SecurityPhaseListener();

    public PhaseListener getThePhaseListener() {
        return thePhaseListener;
    }

    public void setThePhaseListener(PhaseListener thePhaseListener) {
        this.thePhaseListener = thePhaseListener;
    }

    @ManagedProperty(value = "#{currentUser}")
    private CurrentUser theUser;

    public String getCurrentUser() {
        return theUser.getUser();
    }

    public CurrentUser getTheUser() {
        return theUser;
    }

    public void setTheUser(CurrentUser theUser) {
        this.theUser = theUser;
    }

    private String loginName;
    private String password;

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String login() {
        FacesContext facesContext = FacesContext.getCurrentInstance();

        if(loginName == null || loginName.isEmpty())
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "user name is empty", ""));
        if(password == null || password.isEmpty())
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "password is empty", ""));

        if(facesContext.getMessages().hasNext())
            return "login";

        if(!loginName.equals("cmfAdmin")) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid user name", ""));
            return "login";
        }

        if(!password.equals("cmfAdmin")) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid password", ""));
            return "login";
        }

        theUser.setUser(loginName);

        for(Provider provider : Security.getProviders()) {
            System.out.println(provider.getName() + ": " + provider.getInfo());
        }

        return "index";
    }

    public List<GroupPermissions> getCurrentContentGroupPermissions() {
        try {
            return currentNode.getContent().getGroupPermissionsList();
        } catch(NullPointerException ignore) { }

        return new Vector<GroupPermissions>();
    }

    public List<GroupPermissions> getCurrentStyleGroupPermissions() {
        try {
            return currentStyleNode.getStyle().getGroupPermissionsList();
        } catch(NullPointerException ignore) { }

        return new Vector<GroupPermissions>();
    }
}
