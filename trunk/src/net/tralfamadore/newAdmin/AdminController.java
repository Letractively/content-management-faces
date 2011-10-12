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

import net.tralfamadore.client.ContentKey;
import net.tralfamadore.cmf.*;
import org.primefaces.model.TreeNode;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import java.util.List;
import java.util.Vector;

/**
 * User: billreh
 * Date: 10/11/11
 * Time: 8:41 PM
 */
@ManagedBean
@RequestScoped
public class AdminController {
    @ManagedProperty(value = "#{pageContent}")
    private PageContent pageContent;

    @ManagedProperty(value = "#{theTree}")
    private TheTree theTree;

    @ManagedProperty(value = "#{dialogGroups}")
    private DialogGroups dialogGroups;

    private String contentCss;
    private String incomingNamespace;
    private String incomingContentName;
    private List<String> allGroups;
    private String selectedGroup;

    @PostConstruct
    public void init() {
        allGroups = ((JpaContentManager)theTree.getContentManager()).getAllGroups();
    }


    public void loadNamespace() {
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

    public void addNewNamespace() {
    }

    public void addNewContent() {
    }

    public void addNewStyle() {
    }

    public void saveNamespace() {
    }

    public void saveContent() {
    }

    public void saveStyle() {
    }

    public void applyStyle() {
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
}
