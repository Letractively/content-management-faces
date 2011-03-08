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

import net.tralfamadore.cmf.Content;
import net.tralfamadore.cmf.Namespace;
import net.tralfamadore.cmf.Style;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * User: billreh
 * Date: 2/28/11
 * Time: 6:15 AM
 */
public class ContentHolder implements Serializable {
    private final Map<ContentKey,TreeNode> allContent = new HashMap<ContentKey, TreeNode>();
    private TreeNode rootNode;

    public ContentHolder(TreeNode rootNode) {
        this.rootNode = rootNode;
    }

    public TreeNode getRootNode() {
        return rootNode;
    }

    public void setRootNode(TreeNode rootNode) {
        this.rootNode = rootNode;
    }

    public Map<ContentKey, TreeNode> getAllContentReadOnly() {
        return Collections.unmodifiableMap(allContent);
    }

    public void clear() {
        allContent.clear();
        rootNode.getChildren().clear();
    }

    public void add(Namespace namespace) {
        ContentKey contentKey = new ContentKey(null, namespace.getFullName(), "namespace");
        ContentKey parentKey = null;
        if(namespace.getParent() != null)
            parentKey = new ContentKey(null, namespace.getParent().getFullName(), "namespace");

        TreeNode parent = allContent.get(parentKey);
        parent = parent == null ? rootNode : parent;
        TreeNode newNode = new DefaultTreeNode("namespace", namespace, parent);
        newNode.setExpanded(true);
        namespace.setTreeNode(newNode);
        allContent.put(contentKey, newNode);
    }

    public void add(Content content) {
        ContentKey contentKey = new ContentKey(null, content.getNamespace().getFullName(), "content");
        ContentKey parentKey = new ContentKey(null, content.getNamespace().getFullName(), "namespace");

        TreeNode parent = allContent.get(parentKey);
        TreeNode newNode = new DefaultTreeNode("content", content, parent);
        newNode.setExpanded(true);
        allContent.put(contentKey, newNode);
    }

    public void add(Style style) {
        ContentKey contentKey = new ContentKey(null, style.getNamespace().getFullName(), "style");
        ContentKey parentKey = new ContentKey(null, style.getNamespace().getFullName(), "namespace");

        TreeNode parent = allContent.get(parentKey);
        TreeNode newNode = new DefaultTreeNode("style", style, parent);
        newNode.setExpanded(true);
        if(!allContent.containsKey(contentKey))
            allContent.put(contentKey, newNode);
    }

    public TreeNode find(ContentKey contentKey) {
        return allContent.get(contentKey);
    }

    public void remove(ContentKey contentKey) {
        TreeNode node = find(contentKey);
        node.getParent().getChildren().remove(node);
        allContent.remove(contentKey);
    }
}
