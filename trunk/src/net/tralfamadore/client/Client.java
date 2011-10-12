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

import com.google.code.ckJsfEditor.Config;
import com.google.code.ckJsfEditor.Toolbar;
import com.google.code.ckJsfEditor.ToolbarButtonGroup;
import com.google.code.ckJsfEditor.ToolbarItem;
import com.google.code.ckJsfEditor.component.SaveEvent;
import net.tralfamadore.cmf.*;
import net.tralfamadore.config.CmfContext;
import net.tralfamadore.persistence.JpaEntityManagerProvider;
import org.primefaces.component.picklist.PickList;
import org.primefaces.context.RequestContext;
import org.primefaces.event.DragDropEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import java.io.File;
import java.util.*;

/**
 * User: billreh
 * Date: 2/26/11
 * Time: 10:09 PM
 */
@ManagedBean
@SessionScoped
public class Client {
    /** the path to the embedded db directory */
    private String derbyPath;
    private List<BaseContent> namespaceContents = new Vector<BaseContent>();
//    private DataTable groupsDataTable;
    private Content contentToAdd;
    private boolean addingContent = false;
    private Namespace namespaceToAdd;
    private boolean addingNamespace = false;
    private boolean addingNewNamespace = false;
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
    /** the context manager */
    private final CmfContext cmfContext = CmfContext.getInstance();

    /** the content manager */
    private final ContentManager contentManager = cmfContext.getContentManager();

    /** true if we need to configure the embedded database */
    private boolean embeddedDbNeedsConfig = cmfContext.isEmbeddedDbNeedsConfig();
    private boolean addingStyle = false;
    private Style styleToAdd;
    private Config editorConfig;

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
        editorConfig = new Config();
        editorConfig.toolbar(editorToolbar);
    }

    /**
     * Creates the tree model for the tree component from information gotten from the content manager.  Used internally
     * and by other components to "re-render" the model when it is updated.
     *
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

    public void addStyle(ActionEvent event) {
        Style style = styleToAdd;
        style.setName(newStyleName);
        style.setNamespace((Namespace) currentNamespace.getData());
        style.setGroupPermissionsList(getGroupData());
        contentHolder.add(style);
        contentManager.saveStyle(style);
        addingStyle = false;
        namespaceContents.add(style);
        createTreeModel(event);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Style " + style.getFullName() + " added.", ""));
    }

    public void addContent(ActionEvent event) {
        loadNamespace();
        Content content = contentToAdd;
        content.setName(newContentName);
        content.setNamespace((Namespace) currentNamespace.getData());
        content.setDateCreated(new Date());
        content.setDateModified(content.getDateCreated());
        contentHolder.add(content);
        contentManager.saveContent(content);
        addingContent = false;
        namespaceContents.add(content);
        createTreeModel(event);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Content " + content.getFullName() + " added.", ""));
    }

    /*
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
    */

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
//        System.out.println(event.getTreeNode().getData());
    }

    public void addGroupListener(ActionEvent e) {
        if(selectedNode.getData() instanceof BaseContent) {
            BaseContent content = (BaseContent) selectedNode.getData();
            for(Iterator<GroupPermissions> it = content.getGroupPermissionsList().iterator(); it.hasNext(); ) {
                GroupPermissions groupPermissions = it.next();
                System.out.println(groupPermissions);
            }
        }
        BaseContent content = (BaseContent) selectedNode.getData();
        List<GroupPermissions> groupPermissionsList = getGroupData();
        groupPermissionsList.add(new GroupPermissions(selectedGroup, true, false, false, false));
        content.setGroupPermissionsList(groupPermissionsList);
        if(content instanceof Content)
            contentManager.saveContent((Content)content);
        else if(content instanceof Style)
            contentManager.saveStyle((Style) content);
        else if(content instanceof Namespace)
            contentManager.saveNamespace((Namespace) content);

//        String clientId = groupsDataTable.getClientId(FacesContext.getCurrentInstance());
//        RequestContext requestContext = RequestContext.getCurrentInstance();
//        requestContext.addPartialUpdateTarget(clientId);
        //requestContext.execute("handy!");
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
        createTreeModel(event);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Namespace " + namespace.getFullName() + " saved successfully.", ""));
    }

    public void save() {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Content [" + editorContent + "] saved successfully.", ""));
        Content content = (Content) currentContent.getData();
        content.setContent(editorContent);
        contentManager.saveContent(content);
    }

    public void saveStyle() {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Style [" + styleEditorContent + "] saved successfully.", ""));
        Style style = (Style) currentStyle.getData();
        style.setStyle(styleEditorContent);
        contentManager.saveStyle(style);
    }

    public void saveContent(SaveEvent event) {
        try {
            Content content = (Content) currentContent.getData();
            content.setContent(event.getEditorData());
            contentManager.saveContent(content);
            ELContext elContext = FacesContext.getCurrentInstance().getELContext();
            ELResolver elResolver = elContext.getELResolver();
            elResolver.setValue(elContext, "cmf", content.getFullName(), event.getEditorData());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Content " + content.getName() + " saved successfully.", ""));
        } catch(Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Content [" + event.getEditorData() + "] saved successfully.", ""));
        }
    }

    /*
    public void saveStyle(ActionEvent event) {
        Style style = (Style) currentStyle.getData();
        style.setStyle(styleEditorContent);
        contentManager.saveStyle(style);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Style " + style.getName() + " saved successfully.", ""));
    }
    */

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
        ((Content) currentContent.getData()).setStyles(content.getStyles());
        makeContentCss();
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
        if(addingNamespace) {
            return namespaceToAdd.getGroupPermissionsList();
        } else if(addingContent) {
            return contentToAdd.getGroupPermissionsList();
        } else if(addingStyle) {
            return styleToAdd.getGroupPermissionsList();
        }
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
            if(selectedNode != null)
                selectedNode.setSelected(false);
            editorContent = ((Content)newContent.getData()).getContent();
        } else {
            editorContent = null;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Content for namespace [" + incomingNamespace + "] and content name [" +
                            incomingContentName + "] not found.", null));
        }
//        currentNamespace = null;
        currentStyle = null;
        currentContent = newContent;
        selectedNode = currentContent;
        makeContentCss();
    }

    public void loadNamespace() {
        TreeNode newContent = contentHolder.find(new ContentKey(null, incomingNamespace, "namespace"));

        if(newContent != null) {
            newContent.setSelected(true);
            if(selectedNode != null)
                selectedNode.setSelected(false);
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Namespace [" + incomingNamespace + "] not found.", null));
        }

        currentContent = null;
        currentStyle = null;
        currentNamespace = newContent;
        selectedNode = currentNamespace;
        fetchNamespaceContents();
    }

    public void loadStyle() {
        TreeNode newContent = contentHolder.find(new ContentKey(incomingContentName, incomingNamespace, "style"));
        if(newContent != null) {
            newContent.setSelected(true);
            if(selectedNode != null)
                selectedNode.setSelected(false);
            styleEditorContent = ((Style)newContent.getData()).getStyle();
        } else {
            styleEditorContent = null;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Style for namespace [" + incomingNamespace + "] and style name [" +
                            incomingContentName + "] not found.", null));
        }
//        currentNamespace = null;
        currentContent = null;
        currentStyle = newContent;
        selectedNode = currentStyle;
        ELContext elContext = FacesContext.getCurrentInstance().getELContext();
        elContext.getELResolver().setValue(elContext, "cmf", "style", currentStyle.getData());
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
            requestContext.addPartialUpdateTarget("theForm:editor");
        }
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

    public void removeGroup(ActionEvent e) {
        String groupName = (String) e.getComponent().getAttributes().get("group");
        if(selectedNode.getData() instanceof Namespace) {
            Namespace namespace = (Namespace) selectedNode.getData();
            for(Iterator<GroupPermissions> it = namespace.getGroupPermissionsList().iterator(); it.hasNext(); ) {
                GroupPermissions groupPermissions = it.next();
                if(groupPermissions.getGroup().equals(groupName)) {
                    it.remove();
                    break;
                }
            }
            contentManager.saveNamespace(namespace);
        } else if(selectedNode.getData() instanceof BaseContent) {
            BaseContent content = (BaseContent) selectedNode.getData();
            for(Iterator<GroupPermissions> it = content.getGroupPermissionsList().iterator(); it.hasNext(); ) {
                GroupPermissions groupPermissions = it.next();
                if(groupPermissions.getGroup().equals(groupName)) {
                    it.remove();
                    break;
                }
            }
            if(content instanceof Style) {
                contentManager.saveStyle((Style)content);
            } else if(content instanceof Content) {
                    contentManager.saveContent((Content) content);
            } else if(content instanceof Script) {
                contentManager.saveScript((Script) content);
            }
        }
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

    public void addNewTopLevelNamespace(ActionEvent e) {
        namespaceToAdd = new Namespace();
        namespaceToAdd.setGroupPermissionsList(getDefaultGroupPermissions());
        addingNamespace = true;
        addingNewNamespace = true;
    }

    public void addNewNamespace(ActionEvent e) {
        Namespace parent = (Namespace) currentNamespace.getData();
        namespaceToAdd = new Namespace(parent, "");
        namespaceToAdd.setGroupPermissionsList(getDefaultGroupPermissions());
        addingNamespace = true;
    }

    public void saveNewNamespace(ActionEvent e) {
        namespaceToAdd.setNodeName(newNamespace);
        contentManager.saveNamespace(namespaceToAdd);
        getNamespaceContents().add(namespaceToAdd);
        createTreeModel(e);
        namespaceToAdd = null;
        addingNamespace = false;
        addingNewNamespace = false;
        newNamespace = null;
    }

    public boolean isAddingNamespace() {
        return addingNamespace;
    }

    public void setAddingNamespace(boolean addingNamespace) {
        this.addingNamespace = addingNamespace;
    }

    public boolean isAddingNewNamespace() {
        return addingNewNamespace;
    }

    public void setAddingNewNamespace(boolean addingNewNamespace) {
        this.addingNewNamespace = addingNewNamespace;
    }

    public void fetchNamespaceContents() {
        Namespace namespace = (Namespace) currentNamespace.getData();
        namespaceContents = new Vector<BaseContent>();

        namespaceContents.addAll(contentManager.loadContent(namespace));
        namespaceContents.addAll(contentManager.loadStyle(namespace));
        namespaceContents.addAll(contentManager.loadChildNamespaces(namespace));
        if(contentManager.loadChildNamespaces(namespace).isEmpty() && namespace.getParent() == null)
            namespaceContents.add(namespace);
    }

    public List<BaseContent> getNamespaceContents() {
        return namespaceContents;
    }

    public void setNamespaceContents(List<BaseContent> namespaceContents) {
        this.namespaceContents = namespaceContents;
    }

    public void removeBaseContent(ActionEvent e) {
        String contentName = getContentToRemove();
        String contentType = getContentTypeToRemove();

        if(contentType.equals("Namespace")) {
            Namespace namespace = Namespace.createFromString(contentName);
            if(contentManager.loadChildNamespaces(namespace).isEmpty()
                    && contentManager.loadStyle(namespace).isEmpty())
            {
                contentManager.deleteNamespace(namespace);
                getNamespaceContents().remove(namespace);
                createTreeModel(e);
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "You can only delete an empty namespace", null));
            }
        } else if(contentType.equals("Content")) {
            String namespaceName = contentName.substring(0, contentName.lastIndexOf('.'));
            contentName = contentName.substring(contentName.lastIndexOf('.') + 1);
            Content content = (Content) contentHolder.find(new ContentKey(contentName, namespaceName, "content")).getData();
            contentManager.deleteContent(content);
            getNamespaceContents().remove(content);
            createTreeModel(e);
        } else if(contentType.equals("Style")) {
            String namespaceName = contentName.substring(0, contentName.lastIndexOf('.'));
            contentName = contentName.substring(contentName.lastIndexOf('.') + 1);
            Style style = (Style) contentHolder.find(new ContentKey(contentName, namespaceName, "style")).getData();
            contentManager.deleteStyle(style);
            getNamespaceContents().remove(style);
            createTreeModel(e);
        }
    }

    private String contentToRemove;
    private String contentTypeToRemove;

    public String getContentToRemove() {
        return contentToRemove;
    }

    public void setContentToRemove(String contentToRemove) {
        this.contentToRemove = contentToRemove;
    }

    public String getContentTypeToRemove() {
        return contentTypeToRemove;
    }

    public void setContentTypeToRemove(String contentTypeToRemove) {
        this.contentTypeToRemove = contentTypeToRemove;
    }

    public void addNewContent(ActionEvent e) {
        addingContent = true;
        contentToAdd = new Content();
        contentToAdd.setGroupPermissionsList(getDefaultGroupPermissions());
    }

    public void addNewStyle(ActionEvent e) {
        addingStyle = true;
        styleToAdd = new Style();
        styleToAdd.setGroupPermissionsList(getDefaultGroupPermissions());
    }

    public boolean isAddingContent() {
        return addingContent;
    }

    public void setAddingContent(boolean addingContent) {
        this.addingContent = addingContent;
    }

    public Content getContentToAdd() {
        return contentToAdd;
    }

    public void setContentToAdd(Content contentToAdd) {
        this.contentToAdd = contentToAdd;
    }

//    public DataTable getGroupsDataTable() {
//        return groupsDataTable;
//    }
//
//    public void setGroupsDataTable(DataTable groupsDataTable) {
//        this.groupsDataTable = groupsDataTable;
//    }

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

        createTreeModel(new ActionEvent(pickList));
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

    public Config getEditorConfig() {
        return editorConfig;
    }

    public void setEditorConfig(Config editorConfig) {
        this.editorConfig = editorConfig;
    }
}
