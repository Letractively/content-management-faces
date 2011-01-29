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

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;
import java.io.Serializable;
import java.util.List;

/**
 * User: billreh
 * Date: 1/20/11
 * Time: 4:14 AM
 */
@ManagedBean
@SessionScoped
public class Tree implements Serializable {
    private TreeModel treeModel = new TreeModel();
    private Namespace selectedNamespace;
    private String newNamespace;
    private String newContentName;
    private String newStyleName;


    public Tree() {
        createTreeModel();
    }


    public void testTree(ActionEvent event) {
        render();
    }

    public void createTreeModel() {
        ContentManager contentManager = TestContentManager.getInstance();
        ((TestContentManager)contentManager).init();
        List<Namespace> namespaces = contentManager.loadAllNamespaces();
        treeModel = new TreeModel();
        for(Namespace namespace : namespaces) {
            treeModel.addNode(namespace);
        }

        List<Content> contentList = contentManager.loadAllContent();
        for(Content content : contentList) {
            treeModel.addNode(content);
        }

        List<Style> styles = contentManager.loadAllStyles();
        for(Style style : styles) {
            treeModel.addNode(style);
        }

        List<Script> scripts = contentManager.loadAllScripts();
        for(Script script : scripts) {
            treeModel.addNode(script);
        }
    }

    public void render() {
        StringBuffer b = printNode(treeModel.root(), new StringBuffer(), 0);
        System.out.println("\n" + b.toString());
    }

    private StringBuffer printNode(TreeNode node, StringBuffer buf, int depth) {
        for(int i = 0; i < depth; i++)
            buf.append("\t");
        buf.append(node.getText());
        buf.append(" (");
        buf.append(node.getClass().getSimpleName().toLowerCase().replaceAll("treenode", ""));
        buf.append(")\n");
        for(TreeNode n : node.getChildren())
            printNode(n, buf, depth + 1);

        return buf;
    }

    public String getTreeHtml() {
        StringBuffer buf = new StringBuffer();
        buf.append("<div id=\"tree\"><ul>");
        for(TreeNode n : treeModel.root().getChildren()) {
            renderHtml(n, buf);
        }
        buf.append("</ul></div>");
        return buf.toString();
    }

    private StringBuffer renderHtml(TreeNode node, StringBuffer buf) {
        buf.append("<li>");
        renderNode(node, buf);
        if(!node.getChildren().isEmpty())
            buf.append("<ul>");
        for(TreeNode n : node.getChildren())
            renderHtml(n, buf);
        if(!node.getChildren().isEmpty())
            buf.append("</ul>");
        buf.append("</li>");

        return buf;
    }

    private void renderNode(TreeNode node, StringBuffer buf) {
        buf.append("<span>");
        buf.append(node.getText());
        if(node instanceof ContentTreeNode)
            buf.append(" (content)");
        else if(node instanceof NamespaceTreeNode)
            buf.append(" (namespace)");
        else if(node instanceof ScriptTreeNode)
            buf.append(" (script)");
        else if(node instanceof StyleTreeNode)
            buf.append(" (style)");
        buf.append("</span>");
    }

    public TreeModel getTreeModel() {
        return treeModel;
    }

    public void setTreeModel(TreeModel treeModel) {
        this.treeModel = treeModel;
    }

    public Namespace getSelectedNamespace() {
        return selectedNamespace;
    }

    public String getSelectedNamespaceString() {
        return selectedNamespace == null ? null : selectedNamespace.getFullName() + '.';
    }

    public void setSelectedNamespace(Namespace selectedNamespace) {
        this.selectedNamespace = selectedNamespace;
    }

    public String getNewNamespace() {
        return newNamespace;
    }

    public void setNewNamespace(String newNamespace) {
        this.newNamespace = newNamespace;
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
}
