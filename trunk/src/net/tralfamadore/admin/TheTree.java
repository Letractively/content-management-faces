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

import net.tralfamadore.admin.util.Current;
import net.tralfamadore.cmf.Content;
import net.tralfamadore.cmf.ContentManager;
import net.tralfamadore.cmf.Namespace;
import net.tralfamadore.cmf.Style;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

/**
 * User: billreh
 * Date: 10/11/11
 * Time: 8:43 PM
 */
@Named
@SessionScoped
public class TheTree implements Serializable {
    @Inject @Current
    private ContentManager contentManager;

    private TreeNode root;
    private TreeNode selectedNode;
    private ContentHolder contentHolder;

    public TheTree() {
        root = new DefaultTreeNode("Root", null);
        contentHolder = new ContentHolder(root);
    }
    @PostConstruct
    private void init() {
        createTreeModel();
    }

    public TreeNode getRoot() {
        return root;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    void createTreeModel() {
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

    public ContentHolder getContentHolder() {
        return contentHolder;
    }

    public void setContentHolder(ContentHolder contentHolder) {
        this.contentHolder = contentHolder;
    }

    public ContentManager getContentManager() {
        return contentManager;
    }
}
