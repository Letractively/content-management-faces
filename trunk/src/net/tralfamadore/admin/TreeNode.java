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

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

/**
 * User: billreh
 * Date: 1/20/11
 * Time: 3:03 PM
 *
 * Abstract base class for all tree nodes, which are what make up the tree model ({@link TreeModel}).
 */
public abstract class TreeNode implements Serializable {
    private TreeNode parent;
    private List<TreeNode> children = new Vector<TreeNode>();
    private String text;


    /**
     * Create a new tree node with parent <code>parent</code> that will display <code>text</code>.
     *
     * @param parent The parent of this node.  If this is a root node, this will be set to <code>null</code>.
     * @param text The text that will be displayed.
     */
    public TreeNode(TreeNode parent, String text) {
        this.parent = parent;
        this.text = text;
    }

    /* getters and setters */
    public TreeNode getParent() {
        return parent;
    }

    public void setParent(TreeNode parent) {
        this.parent = parent;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<TreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNode> children) {
        this.children = children;
    }

    public void addChild(TreeNode child) {
        children.add(child);
    }

    public void removeChild(TreeNode child) {
        children.remove(child);
    }

    /**
     * Subclasses must implement this method to return the type of node this is, where type is the same as
     * the {@link net.tralfamadore.component.tree.UITreeNode} type from the component that is represented in
     * this model.
     * <br/>
     * <strong>Example</strong><br/>
     *     Return a String in the overriding getType() method that matches "file" or "directory" from the
     *   treeNode tag attributes below.
     * <code>
     *      ...
     *      <cmfAdmin:tree var="node" value="#{someBean.treeModel}"/>
     *          <cmfAdmin:treeNode type="file>
     *              <img alt="file" src="images/file.png"/>
     *              <h:outputText value="#{node.fileName}"/>
     *              <h:outputText value="#{node.fileSize}"/>
     *          </cmfAdmin:treeNode>
     *
     *          <cmfAdmin:treeNode type="directory>
     *              <img alt="directory" src="images/directory.png"/>
     *              <h:outputText value="#{node.directoryName}"/>
     *          </cmfAdmin:treeNode>
     *      </cmfAdmin:tree>
     *      ...
     * </code>
     *
     *   In your backing bean:
     * <code>
     *      ...
     * </code>
     *
     *
     * @return The type of node.
     */
    public abstract String getType();
}
