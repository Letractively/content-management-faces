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

package net.tralfamadore.client;

import net.tralfamadore.cmf.*;
import net.tralfamadore.config.CmfContext;
import org.primefaces.component.picklist.PickList;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * User: billreh
 * Date: 2/26/11
 * Time: 10:09 PM
 */
@ManagedBean
@SessionScoped
public class Client {
    private String filter;
    private TreeNode rootNode;
    private List<GroupPermissions> groupData;
    private List<String> allGroups;
    private DualListModel<GroupPermissions> groups;
    private PickList pickList;
    private GroupPermissionsConverter converter = new GroupPermissionsConverter();
    private ContentHolder contentHolder;
    private ContentManager contentManager = CmfContext.getInstance().getContentManager();
    private TreeNode currentNamespace;
    private TreeNode currentContent;
    private TreeNode currentStyle;
    private TreeNode selectedNode;
    private String newNamespace;
    private SelectOneMenu menu;

    public Client() {
        rootNode = new DefaultTreeNode("Root", null);
        contentHolder = new ContentHolder(rootNode);

        createTreeModel(new ActionEvent(new PickList()));


        groupData = new Vector<GroupPermissions>();
        allGroups = ((JpaContentManager)contentManager).getAllGroups();
    }

    /**
     * Creates the tree model for the tree component from information gotten from the content manager.  Used internally
     * and by other components to "re-render" the model when it is updated.
     *
     * @param event the event
     */
    public void createTreeModel(ActionEvent event) {
        contentHolder.clear();
        List<Namespace> namespaces = contentManager.loadAllNamespaces();
        for(Namespace namespace : namespaces) {
            contentHolder.add(namespace);
        }

        List<Content> contentList = contentManager.loadAllContent();
        for(Content content : contentList) {
            contentHolder.add(content);
        }

        List<Style> styles = contentManager.loadAllStyles();
        for(Style style : styles) {
            contentHolder.add(style);
        }
    }

    public void selectNamespace(ActionEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map requestMap = context.getExternalContext().getRequestParameterMap();
        String value = (String)requestMap.get("namespace");
        selectedNode = currentNamespace = contentHolder.find(new ContentKey(null, value, "namespace"));
    }

    public void selectContent(ActionEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map requestMap = context.getExternalContext().getRequestParameterMap();
        String namespace = (String)requestMap.get("namespace");
        String name = (String)requestMap.get("name");

        selectedNode = currentContent = contentHolder.find(new ContentKey(name, namespace, "content"));
    }

    /**
     * Action listener to save namespace.
     *
     * @param e event info
     */
    public void addNamespace(ActionEvent e) {
        Namespace namespace;
        Namespace n = (Namespace)currentNamespace.getData();
        String parentNamespace = n == null ? null : n.getFullName();

        if(parentNamespace == null)
            namespace = Namespace.createFromString(newNamespace);
        else
            namespace = Namespace.createFromString(parentNamespace + '.' + newNamespace);

        namespace.setGroupPermissionsList(getDefaultGroupPermissions());
        contentManager.saveNamespace(namespace);
        contentHolder.add(namespace);
        newNamespace = null;
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Namespace " + namespace.getFullName() + " saved successfully.", ""));
    }

    public void updateGroups(ActionEvent event) {
        groupData.add(new GroupPermissions("users", true, true, false, false));
    }

    public List<GroupPermissions> getDefaultGroupPermissions() {
        List<GroupPermissions> defaultGroupPermissionsList = new Vector<GroupPermissions>();
        String group = CmfContext.getInstance().getCurrentUser();
        GroupPermissions groupPermissions = new GroupPermissions(group, true, true, true, true);
        defaultGroupPermissionsList.add(groupPermissions);
        return defaultGroupPermissionsList;
    }

    public String getNewNamespace() {
        return newNamespace;
    }

    public void setNewNamespace(String newNamespace) {
        this.newNamespace = newNamespace;
    }

    public PickList getPickList() {
        return pickList;
    }

    public void setPickList(PickList pickList) {
        this.pickList = pickList;
    }

    public DualListModel<GroupPermissions> getGroups() {
        return groups;
    }

    public void setGroups(DualListModel<GroupPermissions> groups) {
        this.groups = groups;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public TreeNode getRootNode() {
        return rootNode;
    }

    public void setRootNode(TreeNode rootNode) {
        this.rootNode = rootNode;
    }

    public List<GroupPermissions> getGroupData() {
        if(selectedNode == null)
            return new Vector<GroupPermissions>();
        Object o = selectedNode.getData();
        if(o instanceof Namespace)
            return ((Namespace)o).getGroupPermissionsList();
        else if(o instanceof BaseContent)
            return ((BaseContent)o).getGroupPermissionsList();
        else
            return new Vector<GroupPermissions>();
    }

    public void setGroupData(List<GroupPermissions> groupData) {
        this.groupData = groupData;
    }

    public List<String> getAllGroups() {
        return allGroups;
    }

    public void setAllGroups(List<String> allGroups) {
        this.allGroups = allGroups;
    }

    public GroupPermissionsConverter getConverter() {
        return converter;
    }

    public void setConverter(GroupPermissionsConverter converter) {
        this.converter = converter;
    }

    public TreeNode getCurrentNamespace() {
        return currentNamespace;
    }

    public void setCurrentNamespace(TreeNode currentNamespace) {
        this.currentNamespace = currentNamespace;
    }

    public TreeNode getCurrentContent() {
        return currentContent;
    }

    public void setCurrentContent(TreeNode currentContent) {
        this.currentContent = currentContent;
    }

    public TreeNode getCurrentStyle() {
        return currentStyle;
    }

    public void setCurrentStyle(TreeNode currentStyle) {
        this.currentStyle = currentStyle;
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    private String selectedGroup;

    public String getSelectedGroup() {
        return selectedGroup;
    }

    public void setSelectedGroup(String selectedGroup) {
        this.selectedGroup = selectedGroup;
    }

    public void addGroupListener(ActionEvent e) {
        this.getGroupData().add(new GroupPermissions(selectedGroup, true, false, false, false));
    }

    public void nodeSelected(NodeSelectEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Selected", event.getTreeNode().toString());
        FacesContext.getCurrentInstance().addMessage(null, message);
        System.out.println(event.getTreeNode().getData());
    }

    private String theText;

    public String getTheText() {
        return theText;
    }

    public void setTheText(String theText) {
        this.theText = theText;
    }
}
