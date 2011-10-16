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

package net.tralfamadore.newAdmin;

import com.google.code.ckJsfEditor.Toolbar;
import com.google.code.ckJsfEditor.ToolbarButtonGroup;
import com.google.code.ckJsfEditor.ToolbarItem;
import net.tralfamadore.client.ContentKey;
import net.tralfamadore.cmf.*;
import net.tralfamadore.config.CmfContext;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.model.TreeNode;

import javax.enterprise.context.RequestScoped;
import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * User: billreh
 * Date: 10/11/11
 * Time: 8:41 PM
 */
@Named("adminController")
@RequestScoped
public class AdminController {
    private static final Toolbar editorToolbar = new Toolbar(
                ToolbarButtonGroup.DOCUMENT.remove(ToolbarItem.SAVE).remove(ToolbarItem.NEW_PAGE),
                ToolbarButtonGroup.CLIPBOARD,
                ToolbarButtonGroup.EDITING,
                ToolbarButtonGroup.COLORS.item(ToolbarItem.BREAK),
                ToolbarButtonGroup.PARAGRAPH,
                ToolbarButtonGroup.INSERT.remove(ToolbarItem.FLASH).remove(ToolbarItem.IFRAME),
                ToolbarButtonGroup.LINKS.item(ToolbarItem.BREAK),
                ToolbarButtonGroup.STYLES,
                ToolbarButtonGroup.TOOLS
        );
    @Inject
    private PageContent pageContent;
    @Inject
    private TheTree theTree;
    @Inject
    private DialogGroups dialogGroups;
    @Inject
    private FacesContext facesContext;

    private String contentCss;
    private String incomingNamespace;
    private String incomingContentName;
    private List<String> allGroups;
    private String selectedGroup;

    private DataTable groupsDataTable;
    private String newNamespace;
    private String newContentName;
    private String newStyleName;
    private DataTable dialogGroupsDataTable;
    private DataTable contentDialogGroupsDataTable;


    public void loadNamespace() {
        if(isAjaxRequest())
            return;
        TreeNode newContent = theTree.getContentHolder().find(new ContentKey(null, incomingNamespace, "namespace"));
        TreeNode selectedNode = theTree.getSelectedNode();

        if(newContent != null) {
            newContent.setSelected(true);
            if(selectedNode != null)
                selectedNode.setSelected(false);
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Namespace [" + incomingNamespace + "] not found.", null));
            return;
        }

        theTree.setSelectedNode(newContent);
        pageContent.setTheContent((BaseContent) newContent.getData());
        fetchNamespaceContents();
    }

    public void loadContent() {
        if(isAjaxRequest())
            return;
        TreeNode newContent = theTree.getContentHolder().find(new ContentKey(incomingContentName,
                incomingNamespace, "content"));
        TreeNode selectedNode = theTree.getSelectedNode();

        if(newContent != null) {
            newContent.setSelected(true);
            if(selectedNode != null)
                selectedNode.setSelected(false);
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Content for namespace [" + incomingNamespace + "] and content name [" +
                            incomingContentName + "] not found.", null));
            return;
        }

        theTree.setSelectedNode(newContent);
        pageContent.setTheContent((BaseContent) newContent.getData());
        makeContentCss();
    }

    public void loadStyle() {
        if(isAjaxRequest())
            return;
        TreeNode newContent = theTree.getContentHolder().find(
                new ContentKey(incomingContentName, incomingNamespace, "style"));
        TreeNode selectedNode = theTree.getSelectedNode();

        if(newContent != null) {
            newContent.setSelected(true);
            if(selectedNode != null)
                selectedNode.setSelected(false);
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Style for namespace [" + incomingNamespace + "] and style name [" +
                            incomingContentName + "] not found.", null));
            return;
        }

        theTree.setSelectedNode(newContent);
        pageContent.setTheContent((BaseContent) newContent.getData());
    }

    public void addNewTopLevelNamespace(ActionEvent e) {
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

    public void addGroup(ActionEvent e) {
        BaseContent content = pageContent.getBaseContent();

        content.getGroupPermissionsList().add(new GroupPermissions(selectedGroup, true, false, false, false));

        saveBaseContent();
    }

    public void addGroupToNewContent(ActionEvent e) {
        pageContent.addGroupToNewContent(new GroupPermissions(selectedGroup, true, false, false, false));
    }

    public void addContent(ActionEvent e) {
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

    public void addStyle(ActionEvent e) {
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
        if(pageContent.getBaseContent() instanceof Namespace) {
            Namespace namespace = pageContent.getNamespace();
            for(Iterator<GroupPermissions> it = namespace.getGroupPermissionsList().iterator(); it.hasNext(); ) {
                GroupPermissions groupPermissions = it.next();
                if(groupPermissions.getGroup().equals(groupName)) {
                    it.remove();
                    break;
                }
            }
            theTree.getContentManager().saveNamespace(namespace);
        } else {
            BaseContent content = pageContent.getBaseContent();
            for(Iterator<GroupPermissions> it = content.getGroupPermissionsList().iterator(); it.hasNext(); ) {
                GroupPermissions groupPermissions = it.next();
                if(groupPermissions.getGroup().equals(groupName)) {
                    it.remove();
                    break;
                }
            }
            saveBaseContent();
        }

        String clientId = groupsDataTable.getClientId(facesContext);
        RequestContext requestContext = RequestContext.getCurrentInstance();
        if(clientId.matches(".*[0-9]+$")) {
            clientId = clientId.replaceAll(":[0-9]+$", "");
        }
        requestContext.addPartialUpdateTarget(clientId);
    }

    public void removeGroupForNewContent(ActionEvent e) {
        String groupName = (String) e.getComponent().getAttributes().get("group");
        if(pageContent.isAddingNamespace() || pageContent.isAddingTopLevelNamespace()) {
            Namespace namespace = pageContent.getNamespaceToAdd();
            for(Iterator<GroupPermissions> it = namespace.getGroupPermissionsList().iterator(); it.hasNext(); ) {
                GroupPermissions groupPermissions = it.next();
                if(groupPermissions.getGroup().equals(groupName)) {
                    it.remove();
                    break;
                }
            }
        } else {
            BaseContent content = null;
            if(pageContent.isAddingContent()) {
                content = pageContent.getContentToAdd();
            } else if(pageContent.isAddingStyle()) {
                content = pageContent.getStyleToAdd();
            } else {
                throw new FacesException("Can't save groups for content in dialog: " + content);
            }
            for(Iterator<GroupPermissions> it = content.getGroupPermissionsList().iterator(); it.hasNext(); ) {
                GroupPermissions groupPermissions = it.next();
                if(groupPermissions.getGroup().equals(groupName)) {
                    it.remove();
                    break;
                }
            }
        }

        String clientId = dialogGroupsDataTable.getClientId(facesContext);
        RequestContext requestContext = RequestContext.getCurrentInstance();
        if(clientId.matches(".*[0-9]+$")) {
            clientId = clientId.replaceAll(":[0-9]+$", "");
        }
        requestContext.addPartialUpdateTarget(clientId);
    }

    public void permissionChanged(AjaxBehaviorEvent e) {
        saveBaseContent();
    }

    public void saveNewNamespace() {
        pageContent.getNamespaceToAdd().setNodeName(newNamespace);

        theTree.getContentManager().saveNamespace(pageContent.getNamespaceToAdd());
        pageContent.getNamespaceContents().add(pageContent.getNamespaceToAdd());
        theTree.createTreeModel();
        pageContent.setNamespaceToAdd(null);
        pageContent.setAddingNamespace(false);
        pageContent.setAddingTopLevelNamespace(false);
        newNamespace = null;
    }

    public void saveContent() {
        Content content = pageContent.getContent();
        theTree.getContentManager().saveContent(content);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Content " + content.getName() + " saved successfully.", ""));
    }

    public void saveStyle() {
    }

    public void applyStyle() {
    }

    public void removeBaseContent(ActionEvent e) {
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

    public PageContent getPageContent() {
        return pageContent;
    }

    public void setPageContent(PageContent pageContent) {
        this.pageContent = pageContent;
    }

    public TheTree getTheTree() {
        return theTree;
    }

    public void setTheTree(TheTree theTree) {
        this.theTree = theTree;
    }

    public DialogGroups getDialogGroups() {
        return dialogGroups;
    }

    public void setDialogGroups(DialogGroups dialogGroups) {
        this.dialogGroups = dialogGroups;
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

    public List<String> getAllGroups() {
        return allGroups;
    }

    public void setAllGroups(List<String> allGroups) {
        this.allGroups = allGroups;
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
        if(pageContent.getContent() != null) {
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
}
