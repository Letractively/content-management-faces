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

package net.tralfamadore.admin;

import com.google.code.ckJsfEditor.Toolbar;
import com.google.code.ckJsfEditor.component.Editor;
import net.tralfamadore.cmf.*;
import net.tralfamadore.config.CmfContext;
import net.tralfamadore.persistence.JpaEntityManagerProvider;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.DragDropEvent;
import org.primefaces.model.TreeNode;

import javax.enterprise.context.RequestScoped;
import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.util.*;

/**
 * User: billreh
 * Date: 10/11/11
 * Time: 8:41 PM
 */
@Named("adminController")
@RequestScoped
public class AdminController {
    @Inject
    private Toolbar editorToolbar;
    @Inject
    private PageContent pageContent;
    @Inject
    private TheTree theTree;
    @Inject
    private FacesContext facesContext;

    /** true if we need to configure the embedded database */
    private boolean embeddedDbNeedsConfig = CmfContext.getInstance().isEmbeddedDbNeedsConfig();
    /** the path to the embedded db directory */
    private String derbyPath;
    private String contentCss;
    private String incomingNamespace;
    private String incomingContentName;
    private String selectedGroup;
    private String newNamespace;
    private String newContentName;
    private String newStyleName;
    private DataTable groupsDataTable;
    private DataTable dialogGroupsDataTable;
    private DataTable contentDialogGroupsDataTable;
    private DataTable styleDialogGroupsDataTable;
    private Editor editor;
    private UIComponent stylePanel;
    private UIComponent theTreeComponent;
    private UIComponent dropper;

    public void loadNamespace() {
        if(isAjaxRequest())
            return;
        loadHelper("namespace");
        fetchNamespaceContents();
    }

    public void loadContent() {
        if(isAjaxRequest())
            return;
        loadHelper("content");
        makeContentCss();
    }

    public void loadStyle() {
        if(isAjaxRequest())
            return;
        loadHelper("style");
    }

    public void addNewTopLevelNamespace() {
        pageContent.setNamespaceToAdd(new Namespace());
        pageContent.getNamespaceToAdd().setGroupPermissionsList(newDefaultGroupPermissions());
        pageContent.setAddingNamespace(true);
        pageContent.setAddingTopLevelNamespace(true);
    }

    public void addNewNamespace() {
        Namespace parent = pageContent.getNamespace();
        pageContent.setNamespaceToAdd(new Namespace(parent, ""));
        pageContent.getNamespaceToAdd().setGroupPermissionsList(newDefaultGroupPermissions());
        pageContent.setAddingNamespace(true);
    }

    public void addNewContent() {
        pageContent.setAddingContent(true);
        pageContent.setContentToAdd(new Content());
        pageContent.getContentToAdd().setGroupPermissionsList(newDefaultGroupPermissions());
    }

    public void addNewStyle() {
        pageContent.setAddingStyle(true);
        pageContent.setStyleToAdd(new Style());
        pageContent.getStyleToAdd().setGroupPermissionsList(newDefaultGroupPermissions());
    }

    public void addGroup() {
        BaseContent content = pageContent.getBaseContent();
        content.getGroupPermissionsList().add(new GroupPermissions(selectedGroup, true, false, false, false));
        saveBaseContent();
    }

    public void addGroupToNewContent() {
        pageContent.addGroupToNewContent(new GroupPermissions(selectedGroup, true, false, false, false));
    }

    public void addContent() {
        Content content = pageContent.getContentToAdd();
        content.setName(newContentName);
        content.setNamespace(pageContent.getNamespace());
        content.setDateCreated(new Date());
        content.setDateModified(content.getDateCreated());
        theTree.getContentHolder().add(content);
        theTree.getContentManager().saveContent(content);
        pageContent.setAddingContent(false);
        pageContent.getNamespaceContents().add(content);
        theTree.createTreeModel();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Content " + content.getFullName() + " added.", ""));
    }

    public void addStyle() {
        Style style = pageContent.getStyleToAdd();
        style.setName(newStyleName);
        style.setNamespace(pageContent.getNamespace());
        theTree.getContentHolder().add(style);
        theTree.getContentManager().saveStyle(style);
        pageContent.setAddingStyle(false);
        pageContent.getNamespaceContents().add(style);
        theTree.createTreeModel();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Style " + style.getFullName() + " added.", ""));
    }

    public void removeGroup(ActionEvent e) {
        String groupName = (String) e.getComponent().getAttributes().get("group");
        BaseContent content = pageContent.getBaseContent();

        for(Iterator<GroupPermissions> it = content.getGroupPermissionsList().iterator(); it.hasNext(); ) {
            GroupPermissions groupPermissions = it.next();
            if(groupPermissions.getGroup().equals(groupName)) {
                it.remove();
                break;
            }
        }
        saveBaseContent();

        String clientId = groupsDataTable.getClientId(facesContext);
        RequestContext requestContext = RequestContext.getCurrentInstance();
        if(clientId.matches(".*[0-9]+$"))
            clientId = clientId.replaceAll(":[0-9]+$", "");
        requestContext.addPartialUpdateTarget(clientId);
    }

    public void removeGroupForNewContent(ActionEvent e) {
        String groupName = (String) e.getComponent().getAttributes().get("group");
        BaseContent content = pageContent.getBaseContentToAdd();

        for(Iterator<GroupPermissions> it = content.getGroupPermissionsList().iterator(); it.hasNext(); ) {
            GroupPermissions groupPermissions = it.next();
            if(groupPermissions.getGroup().equals(groupName)) {
                it.remove();
                break;
            }
        }

        String clientId = dialogGroupsDataTable.getClientId(facesContext);
        RequestContext requestContext = RequestContext.getCurrentInstance();
        if(clientId.matches(".*[0-9]+$"))
            clientId = clientId.replaceAll(":[0-9]+$", "");
        requestContext.addPartialUpdateTarget(clientId);
    }

    public void permissionChanged() {
        saveBaseContent();
    }

    public void saveNewNamespace() {
        pageContent.getNamespaceToAdd().setNodeName(newNamespace);
        theTree.getContentManager().saveNamespace(pageContent.getNamespaceToAdd());
        pageContent.getNamespaceContents().add(pageContent.getNamespaceToAdd());
        pageContent.setAddingNamespace(false);
        pageContent.setAddingTopLevelNamespace(false);
        newNamespace = null;
        theTree.createTreeModel();
    }

    public void saveContent() {
        Content content = pageContent.getContent();
        theTree.getContentManager().saveContent(content);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Content " + content.getName() + " saved successfully.", ""));
    }

    public void saveStyle() {
        Style style = pageContent.getStyle();
        theTree.getContentManager().saveStyle(style);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Style " + style.getName() + " saved successfully.", ""));
    }

    public void styleDrop(DragDropEvent event) {
        Map<String,String> paramMap = facesContext.getExternalContext().getRequestParameterMap();
        String namespace = paramMap.get("styleNamespace");
        String styleName = paramMap.get("styleName");
        Style style = theTree.getContentManager().loadStyle(new Namespace(namespace), styleName);
        Content content = pageContent.getContent();

        if(!content.getStyles().contains(style)) {
            content.getStyles().add(style);
            theTree.getContentManager().saveContent(content);

            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addPartialUpdateTarget(event.getDropId());
            requestContext.addPartialUpdateTarget(stylePanel.getClientId(facesContext));
            requestContext.addPartialUpdateTarget(editor.getClientId(facesContext));
            requestContext.addPartialUpdateTarget(dropper.getClientId(facesContext));
            requestContext.addPartialUpdateTarget(theTreeComponent.getClientId(facesContext));
        }
        makeContentCss();
    }

    public void removeStyle(ActionEvent event) {
        UIComponent component = (UIComponent) event.getSource();
        String namespace = (String) component.getAttributes().get("namespace");
        String name = (String) component.getAttributes().get("styleName");
        Style style = (Style) theTree.getContentHolder().find(new ContentKey(name, namespace, "style")).getData();
        Content content = pageContent.getContent();

        if(content.getStyles().contains(style)) {
            content.getStyles().remove(style);
            theTree.getContentManager().saveContent(content);
        }
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addPartialUpdateTarget(editor.getClientId(facesContext));
        requestContext.addPartialUpdateTarget(stylePanel.getClientId(facesContext));
        requestContext.addPartialUpdateTarget(dropper.getClientId(facesContext));
        requestContext.addPartialUpdateTarget(theTreeComponent.getClientId(facesContext));
        makeContentCss();
    }

    public void removeBaseContent() {
        BaseContent content = pageContent.getContentToRemove();
        ContentManager contentManager = theTree.getContentManager();

        if(content instanceof Namespace) {
            Namespace namespace = content.getNamespace();
            if(contentManager.loadChildNamespaces(namespace).isEmpty()
                    && contentManager.loadStyle(namespace).isEmpty()
                    && contentManager.loadContent(namespace).isEmpty())
            {
                contentManager.deleteNamespace(namespace);
                pageContent.getNamespaceContents().remove(namespace);
                theTree.createTreeModel();
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "You can only delete an empty namespace", null));
            }
        } else if(content instanceof Content) {
            contentManager.deleteContent((Content) content);
            pageContent.getNamespaceContents().remove(content);
            theTree.createTreeModel();
        } else if(content instanceof Style) {
            contentManager.deleteStyle((Style) content);
            pageContent.getNamespaceContents().remove(content);
            theTree.createTreeModel();
        }
    }


    /**
     * Action listener that creates the embedded database from the initial screen.
     */
    public void createEmbeddedDb() {
        File file = new File(derbyPath);
        String jdbc = "jdbc:derby:";
        String props = ";create=true";
        Properties properties = new Properties();

        properties.put("javax.persistence.jdbc.driver", "org.apache.derby.jdbc.EmbeddedDriver");
        properties.put("javax.persistence.jdbc.url", jdbc + derbyPath + props);

        if(!file.getParentFile().exists()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Invalid path: " + derbyPath + ".  Directory " + file.getParentFile().getAbsolutePath()
                            + " does not exist.", null));
            derbyPath = null;
            throw new FacesException();
        }

        ((JpaEntityManagerProvider)CmfContext.getInstance().getEntityManagerProvider()).createEmbeddedDb(properties);

        embeddedDbNeedsConfig = false;

        theTree.createTreeModel();
    }

    public String getIncomingNamespace() {
        return incomingNamespace;
    }

    public void setIncomingNamespace(String incomingNamespace) {
        this.incomingNamespace = incomingNamespace;
    }

    public String getIncomingContentName() {
        return incomingContentName;
    }

    public void setIncomingContentName(String incomingContentName) {
        this.incomingContentName = incomingContentName;
    }

    public String getContentCss() {
        return contentCss;
    }

    public void setContentCss(String contentCss) {
        this.contentCss = contentCss;
    }

    public String getSelectedGroup() {
        return selectedGroup;
    }

    public void setSelectedGroup(String selectedGroup) {
        this.selectedGroup = selectedGroup;
    }

    public Toolbar getEditorToolbar() {
        return editorToolbar;
    }

    public DataTable getGroupsDataTable() {
        return groupsDataTable;
    }

    public void setGroupsDataTable(DataTable groupsDataTable) {
        this.groupsDataTable = groupsDataTable;
    }

    public String getNewNamespace() {
        return newNamespace;
    }

    public void setNewNamespace(String newNamespace) {
        this.newNamespace = newNamespace;
    }

    public DataTable getDialogGroupsDataTable() {
        return dialogGroupsDataTable;
    }

    public void setDialogGroupsDataTable(DataTable dialogGroupsDataTable) {
        this.dialogGroupsDataTable = dialogGroupsDataTable;
    }

    public String getNewContentName() {
        return newContentName;
    }

    public void setNewContentName(String newContentName) {
        this.newContentName = newContentName;
    }

    public DataTable getContentDialogGroupsDataTable() {
        return contentDialogGroupsDataTable;
    }

    public void setContentDialogGroupsDataTable(DataTable contentDialogGroupsDataTable) {
        this.contentDialogGroupsDataTable = contentDialogGroupsDataTable;
    }

    public String getNewStyleName() {
        return newStyleName;
    }

    public void setNewStyleName(String newStyleName) {
        this.newStyleName = newStyleName;
    }

    public Editor getEditor() {
        return editor;
    }

    public void setEditor(Editor editor) {
        this.editor = editor;
    }

    public UIComponent getStylePanel() {
        return stylePanel;
    }

    public void setStylePanel(UIComponent stylePanel) {
        this.stylePanel = stylePanel;
    }

    public UIComponent getTheTreeComponent() {
        return theTreeComponent;
    }

    public void setTheTreeComponent(UIComponent theTreeComponent) {
        this.theTreeComponent = theTreeComponent;
    }

    public UIComponent getDropper() {
        return dropper;
    }

    public void setDropper(UIComponent dropper) {
        this.dropper = dropper;
    }

    public DataTable getStyleDialogGroupsDataTable() {
        return styleDialogGroupsDataTable;
    }

    public void setStyleDialogGroupsDataTable(DataTable styleDialogGroupsDataTable) {
        this.styleDialogGroupsDataTable = styleDialogGroupsDataTable;
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

    private boolean isAjaxRequest() {
        return facesContext.getPartialViewContext().isAjaxRequest();
    }

    private void saveBaseContent() {
        BaseContent content = pageContent.getBaseContent();

        saveBaseContent(content);
    }

    private void saveBaseContent(BaseContent content) {
        if(content instanceof Content)
            theTree.getContentManager().saveContent((Content)content);
        else if(content instanceof Style)
            theTree.getContentManager().saveStyle((Style) content);
        else if(content instanceof Namespace)
            theTree.getContentManager().saveNamespace((Namespace) content);
    }

    private void fetchNamespaceContents() {
        Namespace namespace = pageContent.getNamespace();
        pageContent.setNamespaceContents(new Vector<BaseContent>());
        List<BaseContent> namespaceContents = pageContent.getNamespaceContents();
        ContentManager contentManager = theTree.getContentManager();

        namespaceContents.addAll(contentManager.loadContent(namespace));
        namespaceContents.addAll(contentManager.loadStyle(namespace));
        namespaceContents.addAll(contentManager.loadChildNamespaces(namespace));
        if(contentManager.loadChildNamespaces(namespace).isEmpty() && namespace.getParent() == null)
            namespaceContents.add(namespace);
    }

    private void makeContentCss() {
        contentCss = "";
        if(pageContent.getContent() != null && pageContent.getContent().getStyles() != null) {
            for(Style style : pageContent.getContent().getStyles())
                contentCss =  contentCss + style.getStyle();
        }
        contentCss = contentCss.replace('\r', ' ');
        contentCss = contentCss.replace('\n', ' ');
    }

    private List<GroupPermissions> newDefaultGroupPermissions() {
        List<GroupPermissions> defaultGroupPermissionsList = new Vector<GroupPermissions>();
        String group = CmfContext.getInstance().getCurrentUser();
        GroupPermissions groupPermissions = new GroupPermissions(group, true, true, true, true);
        defaultGroupPermissionsList.add(groupPermissions);
        return defaultGroupPermissionsList;
    }

    private void loadHelper(String type) {
        TreeNode newContent;
        if("namespace".equals(type)) {
            newContent = theTree.getContentHolder().find(new ContentKey(null, incomingNamespace, "namespace"));
        } else {
            newContent = theTree.getContentHolder().find(
                    new ContentKey(incomingContentName, incomingNamespace, type));
        }
        TreeNode selectedNode = theTree.getSelectedNode();

        if(newContent != null) {
            newContent.setSelected(true);
            if(selectedNode != null)
                selectedNode.setSelected(false);
        } else {
            if("namespace".equals(type))
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Namespace [" + incomingNamespace + "] not found.", null));
            else
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Style for namespace [" + incomingNamespace + "] and " + type + " name [" +
                                incomingContentName + "] not found.", null));
            return;
        }

        theTree.setSelectedNode(newContent);
        pageContent.setTheContent((BaseContent) newContent.getData());
        theTree.createTreeModel();
    }
}
