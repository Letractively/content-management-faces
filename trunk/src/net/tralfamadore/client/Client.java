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

import com.google.code.ckJsfEditor.Toolbar;
import com.google.code.ckJsfEditor.ToolbarButtonGroup;
import com.google.code.ckJsfEditor.ToolbarItem;
import com.google.code.ckJsfEditor.component.SaveEvent;
import net.tralfamadore.cmf.*;
import net.tralfamadore.config.CmfContext;
import org.primefaces.component.picklist.PickList;
import org.primefaces.context.RequestContext;
import org.primefaces.event.DragDropEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import java.util.Date;
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
    private String contentCss;
    private String incomingNamespace;
    private String incomingContentName;
    private Toolbar editorToolbar;
    private String filter;
    private TreeNode rootNode;
    private List<GroupPermissions> groupData;
    private List<String> allGroups;
    private DualListModel<GroupPermissions> groups;
    private PickList pickList = new PickList();
    private GroupPermissionsConverter converter = new GroupPermissionsConverter();
    private ContentHolder contentHolder;
    private ContentManager contentManager = CmfContext.getInstance().getContentManager();
    private TreeNode currentNamespace;
    private TreeNode currentContent;
    private TreeNode currentStyle;
    private TreeNode selectedNode;
    private String newNamespace;
    private String newContentName;
    private String newStyleName;
    private String showNamespace;
    private String selectedGroup;
    private String editorContent;
    private String styleEditorContent = "p {\n\tcolor: black;\n}";

    public Client() {
        rootNode = new DefaultTreeNode("Root", null);
        contentHolder = new ContentHolder(rootNode);

        createTreeModel(new ActionEvent(new PickList()));


        groupData = new Vector<GroupPermissions>();
        allGroups = ((JpaContentManager)contentManager).getAllGroups();

        editorToolbar = new Toolbar(
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

        Namespace namespace = new Namespace(null, "com");
        contentHolder.add(namespace);
        namespace = new Namespace(namespace, "google");
        contentHolder.add(namespace);
        Content content = new Content();
        content.setName("page");
        content.setContent("Some content");
        content.setNamespace(namespace);
        contentHolder.add(content);
    }

    public void selectNamespace(ActionEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map requestMap = context.getExternalContext().getRequestParameterMap();
        String value = (String)requestMap.get("namespace");
        selectedNode = currentNamespace = contentHolder.find(new ContentKey(null, value, "namespace"));
    }

    public void addStyle(ActionEvent event) {
        Style style = new Style();
        style.setName(newStyleName);
        style.setNamespace((Namespace) currentNamespace.getData());
        style.setGroupPermissionsList(getGroupData());
        contentHolder.add(style);
        contentManager.saveStyle(style);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Style " + style.getFullName() + " added.", ""));
    }

    public void addContent(ActionEvent event) {
        Content content = new Content();
        content.setName(newContentName);
        content.setNamespace((Namespace) currentNamespace.getData());
        content.setDateCreated(new Date());
        content.setDateModified(content.getDateCreated());
        content.setGroupPermissionsList(getGroupData());
        contentHolder.add(content);
        contentManager.saveContent(content);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Content " + content.getFullName() + " added.", ""));
    }

    public void selectContent(ActionEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map requestMap = context.getExternalContext().getRequestParameterMap();
        String namespace = (String)requestMap.get("namespace");
        String name = (String)requestMap.get("name");

        selectedNode = currentContent = contentHolder.find(new ContentKey(name, namespace, "content"));
        Content content = (Content) currentContent.getData();
        editorContent = content == null? null : content.getContent();
        addStyles(content == null ? new Vector<Style>() : content.getStyles());
    }

    public void selectStyle(ActionEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map requestMap = context.getExternalContext().getRequestParameterMap();
        String namespace = (String)requestMap.get("namespace");
        String name = (String)requestMap.get("name");

        selectedNode = currentStyle = contentHolder.find(new ContentKey(name, namespace, "style"));
        Style style = (Style) currentStyle.getData();
        styleEditorContent = style == null? null : style.getStyle();
    }

    public void nodeSelected(NodeSelectEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Selected", event.getTreeNode().toString());
        FacesContext.getCurrentInstance().addMessage(null, message);
        System.out.println(event.getTreeNode().getData());
    }

    public void addGroupListener(ActionEvent e) {
        this.getGroupData().add(new GroupPermissions(selectedGroup, true, false, false, false));
        contentManager.saveContent((Content)currentContent.getData());
    }

    /**
     * Action listener to save namespace.
     *
     * @param event event info
     */
    public void addNamespace(ActionEvent event) {
        Namespace namespace;
        Namespace n = currentNamespace == null ? null : (Namespace)currentNamespace.getData();
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

    public void save() {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Content [" + editorContent + "] saved successfully.", ""));
    }

    public void saveContent(SaveEvent event) {
        try {
        Content content = (Content) currentContent.getData();
        content.setContent(event.getEditorData());
        contentManager.saveContent(content);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Content " + content.getName() + " saved successfully.", ""));
        } catch(Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Content [" + event.getEditorData() + "] saved successfully.", ""));
        }
    }

    public void saveStyle(ActionEvent event) {
        Style style = (Style) currentStyle.getData();
        style.setStyle(styleEditorContent);
        contentManager.saveStyle(style);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Style " + style.getName() + " saved successfully.", ""));
    }

    public void deleteNamespace(ActionEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map requestMap = context.getExternalContext().getRequestParameterMap();
        String value = (String)requestMap.get("namespace");
        selectedNode = currentNamespace = contentHolder.find(new ContentKey(null, value, "namespace"));
        if(!selectedNode.isLeaf())
            return;
        if(selectedNode != null) {
            contentHolder.remove(new ContentKey(null, value, "namespace"));
            Namespace namespace = (Namespace) selectedNode.getData();
            contentManager.deleteNamespace(namespace);
            selectedNode = null;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Namespace " + namespace.getFullName() + " deleted.", ""));
        }
    }

    public void deleteContent(ActionEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map requestMap = context.getExternalContext().getRequestParameterMap();
        String value = (String)requestMap.get("namespace");
        String name = (String)requestMap.get("name");
        selectedNode = currentContent = contentHolder.find(new ContentKey(name, value, "content"));
        if(!selectedNode.isLeaf())
            return;
        if(selectedNode != null) {
            contentHolder.remove(new ContentKey(name, value, "content"));
            Content content = (Content) selectedNode.getData();
            contentManager.deleteContent(content);
            selectedNode = null;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Content " + content.getFullName() + "." + content.getName() + " deleted.", ""));
        }
    }

    public void applyStyle(ActionEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map requestMap = context.getExternalContext().getRequestParameterMap();
        String value = (String)requestMap.get("namespace");
        String name = (String)requestMap.get("name");
        currentStyle = contentHolder.find(new ContentKey(name, value, "style"));
        Content content = (Content) currentContent.getData();
        Style style = (Style) currentStyle.getData();
        if(!content.getStyles().contains(style)) {
            content.getStyles().add(style);
            contentManager.saveContent(content);
        }
        addStyles(content.getStyles());
        ((Content) currentContent.getData()).setStyles(content.getStyles());
        createTreeModel(new ActionEvent(pickList));
    }

    public void removeStyle(ActionEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map requestMap = context.getExternalContext().getRequestParameterMap();
        UIComponent component = (UIComponent) event.getSource();
        String namespace = (String) component.getAttributes().get("namespace");
        String name = (String) component.getAttributes().get("styleName");
        currentStyle = contentHolder.find(new ContentKey(name, namespace, "style"));
        Content content = (Content) currentContent.getData();
        Style style = (Style) currentStyle.getData();
        if(content.getStyles().contains(style)) {
            content.getStyles().remove(style);
            contentManager.saveContent(content);
        }
        ((Content) currentContent.getData()).setStyles(content.getStyles());
        createTreeModel(new ActionEvent(pickList));
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addPartialUpdateTarget("theForm:acPanel");
        requestContext.addPartialUpdateTarget("theForm:theTree");
        requestContext.addPartialUpdateTarget("theForm:editor");
        makeContentCss();
        addStyles(content.getStyles());
    }

    public void deleteStyle(ActionEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map requestMap = context.getExternalContext().getRequestParameterMap();
        String value = (String)requestMap.get("namespace");
        String name = (String)requestMap.get("name");
        selectedNode = currentStyle = contentHolder.find(new ContentKey(name, value, "style"));
        if(!selectedNode.isLeaf())
            return;
        if(selectedNode != null) {
            contentHolder.remove(new ContentKey(name, value, "style"));
            Style style = (Style) selectedNode.getData();
            contentManager.deleteStyle(style);
            selectedNode = null;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Style " + style.getFullName() + "." + style.getName() + " deleted.", ""));
        }
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

    public String getEditorContent() {
        return editorContent;
    }

    public void setEditorContent(String editorContent) {
        this.editorContent = editorContent;
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

    public String getSelectedGroup() {
        return selectedGroup;
    }

    public void setSelectedGroup(String selectedGroup) {
        this.selectedGroup = selectedGroup;
    }

    public String getShowNamespace() {
        return showNamespace;
    }

    public void setShowNamespace(String showNamespace) {
        this.showNamespace = showNamespace;
    }

    public String getNewContentName() {
        return newContentName;
    }

    public void setNewContentName(String newContentName) {
        this.newContentName = newContentName;
    }

    public String getNewStyleName() {
        return newStyleName;
    }

    public void setNewStyleName(String newStyleName) {
        this.newStyleName = newStyleName;
    }

    public String getStyleEditorContent() {
        return styleEditorContent;
    }

    public void setStyleEditorContent(String styleEditorContent) {
        this.styleEditorContent = styleEditorContent;
    }

    public Toolbar getEditorToolbar() {
        return editorToolbar;
    }

    public void setEditorToolbar(Toolbar editorToolbar) {
        this.editorToolbar = editorToolbar;
    }

    private void addStyles(List<Style> styles) {
        StringBuilder buf = new StringBuilder();
        for(Style style : styles)
            buf.append(style.getStyle()).append("\n");
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("css", buf.toString());
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

    public void loadContents() {
        TreeNode newContent = contentHolder.find(new ContentKey(incomingContentName, incomingNamespace, "content"));
        if(newContent != null) {
            newContent.setSelected(true);
            if(currentContent != null)
                currentContent.setSelected(false);
            editorContent = ((Content)newContent.getData()).getContent();
        } else {
            editorContent = null;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Content for namespace [" + incomingNamespace + "] and content name [" +
                            incomingContentName + "] not found.", null));
        }
        currentContent = newContent;
        selectedNode = currentContent;
        makeContentCss();
    }

    private void makeContentCss() {
        contentCss = "";
        if(currentContent != null) {
            for(Style style : ((Content)currentContent.getData()).getStyles())
                contentCss =  contentCss + style.getStyle();
        }
        contentCss = contentCss.replace('\r', ' ');
        contentCss = contentCss.replace('\n', ' ');
    }

    public void loadStyle() {
        TreeNode newContent = contentHolder.find(new ContentKey(incomingContentName, incomingNamespace, "style"));
        if(newContent != null) {
            newContent.setSelected(true);
            if(currentContent != null)
                currentContent.setSelected(false);
            styleEditorContent = ((Style)newContent.getData()).getStyle();
        } else {
            styleEditorContent = null;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Style for namespace [" + incomingNamespace + "] and style name [" +
                            incomingContentName + "] not found.", null));
        }
        currentContent = newContent;
        selectedNode = currentContent;
    }

    public void styleDrop(DragDropEvent event) {
        Map<String,String> paramMap = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String namespace = paramMap.get("namespace");
        String styleName = paramMap.get("styleName");
        Style style = contentManager.loadStyle(new Namespace(namespace), styleName);
        Content content = (Content) currentContent.getData();
        if(!content.getStyles().contains(style)) {
            content.getStyles().add(style);
            contentManager.saveContent(content);

            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addPartialUpdateTarget(event.getDropId());
            requestContext.addPartialUpdateTarget("theForm:acPanel");
            requestContext.addPartialUpdateTarget("theForm:acPanel:dropper");
            requestContext.addPartialUpdateTarget("theForm:acPanel:slot");
            requestContext.addPartialUpdateTarget("theForm:theTree");
        }
        addStyles(content.getStyles());
    }

    public boolean isCurrentContentHasStyles() {
        Content content = currentContent == null ? null : (Content) currentContent.getData();
        List<Style> styles = content == null ? null : content.getStyles();
        return styles == null || styles.isEmpty();
    }

    public String getContentCss() {
        return contentCss;
    }

    public void setContentCss(String contentCss) {
        this.contentCss = contentCss;
    }

    public void permissionChanged(AjaxBehaviorEvent e) {
        Object content = selectedNode.getData();
        if(content instanceof Content)
            contentManager.saveContent((Content)content);
        else if(content instanceof Style)
            contentManager.saveStyle((Style) content);
        else if(content instanceof Namespace)
            contentManager.saveNamespace((Namespace) content);
    }
}
