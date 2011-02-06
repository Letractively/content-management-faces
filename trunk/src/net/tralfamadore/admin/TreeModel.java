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

import net.tralfamadore.cmf.Content;
import net.tralfamadore.cmf.Namespace;
import net.tralfamadore.cmf.Script;
import net.tralfamadore.cmf.Style;

import java.io.Serializable;
import java.util.List;

/**
 * User: billreh
 * Date: 1/20/11
 * Time: 3:03 PM
 *
 * The model that the Tree component uses when rendering the tree on the page.
 */
public class TreeModel implements Serializable {
    /** the root node */
    private final TreeNode root = new NamespaceTreeNode(null, Namespace.createFromString("root"));


    /**
     * Get the root node.
     *
     * @return the root node.
     */
    public TreeNode root() {
        return root;
    }

    /**
     * Find a content node with name <code>name</code> and Namespace <code>namespace</code> under
     * node <code>root</code>.
     *
     * @param namespace the namespace of the content node
     * @param name the name of the content node
     * @param rootNode the node to look under
     *
     * @return The found node, or <code>null</code> if not found.
     */
    public ContentTreeNode findContentNode(Namespace namespace, String name, TreeNode rootNode) {
        for(TreeNode n : rootNode.getChildren()) {
            if(n instanceof ContentTreeNode) {
                ContentTreeNode cn = (ContentTreeNode) n;
                if(name.equals(cn.getContent().getName()) && namespace.equals(cn.getContent().getNamespace()))
                    return cn;
            } else if(n instanceof NamespaceTreeNode) {
                ContentTreeNode node = findContentNode(namespace, name, n);
                if(node != null)
                    return  node;
            }

        }
        return null;
    }

    /**
     * Find a style node with name <code>name</code> and Namespace <code>namespace</code> under
     * node <code>root</code>.
     *
     * @param namespace the namespace of the content node
     * @param name the name of the content node
     * @param rootNode the node to look under
     *
     * @return The found node, or <code>null</code> if not found.
     */
    public StyleTreeNode findStyleNode(Namespace namespace, String name, TreeNode rootNode) {
        for(TreeNode n : rootNode.getChildren()) {
            if(n instanceof StyleTreeNode) {
                StyleTreeNode cn = (StyleTreeNode) n;
                if(name.equals(cn.getStyle().getName()) && namespace.equals(cn.getStyle().getNamespace()))
                    return cn;
            } else if(n instanceof NamespaceTreeNode) {
                StyleTreeNode node = findStyleNode(namespace, name, n);
                if(node != null)
                    return  node;
            }

        }
        return null;
    }

    /**
     * Add node <code>child</code> to <code>parent</code>.
     *
     * @param parent The parent node (may not be <code>null</code>).
     * @param child The child node.
     */
    public void addNode(TreeNode parent, TreeNode child) {
        for(TreeNode node : parent.getChildren()) {
            if(node.getClass() != child.getClass())
                continue;
            if(node.getText().equals(child.getText()))
                return;
        }
        parent.addChild(child);
        child.setParent(parent);
    }

    /**
     * Add a new {@link NamespaceTreeNode} with <code>namespace</code> as its data to the model, placing it
     * in the correct place in the tree.
     *
     * @param namespace the {@link Namespace} to add.
     */
    public void addNode(Namespace namespace) {
        if(namespace.getParent() == null) {
            for(TreeNode node : root.getChildren()) {
                if(node instanceof NamespaceTreeNode && ((NamespaceTreeNode) node).getNamespace().equals(namespace))
                    return;
            }
            root.addChild(new NamespaceTreeNode(null, namespace));
            return;
        }
        List<Namespace> namespaces = namespace.getParentNamespaces();
        for(Namespace ns : namespaces) {
            addNode((NamespaceTreeNode) root, ns);
        }
    }

    /**
     * Add a new {@link NamespaceTreeNode} to the tree under parent <code>node</code>.
     *
     * @param node The NamespaceTreeNode to add the node under.
     * @param namespace The {@link Namespace} to be put in the new NamespaceTreeNode.
     */
    public void addNode(NamespaceTreeNode node, Namespace namespace) {
        for(TreeNode n : node.getChildren()) {
            if(!(n instanceof NamespaceTreeNode))
                continue;
            NamespaceTreeNode namespaceNode = (NamespaceTreeNode) n;
            if(namespaceNode.getNamespace().equals(namespace.getParent())) {
                addNode(namespaceNode, new NamespaceTreeNode(namespaceNode, namespace));
                return;
            }
        }
        if(node.equals(root) && namespace.getFullName().equals(namespace.getNodeName())) {
            addNode(root,new NamespaceTreeNode(root, namespace));
        } else {
            for(TreeNode n : node.getChildren()) {
                if(!(n instanceof NamespaceTreeNode))
                    continue;
                NamespaceTreeNode namespaceNode = (NamespaceTreeNode) n;
                addNode(namespaceNode, namespace);
            }
        }
    }

    /**
     * Add a new {@link ContentTreeNode} with <code>content</code> as its data to the model, placing it
     * in the correct place in the tree.
     *
     * @param content the {@link Content} to add.
     */
    public void addNode(Content content) {
        addNode(root, content);
    }

    /**
     * Add a new {@link ContentTreeNode} to the tree under parent <code>parent</code>.
     *
     * @param parent The TreeNode to add the node under.
     * @param content The {@link Content} to be put in the new ContentTreeNode.
     */
    public void addNode(TreeNode parent, Content content) {
        if((parent instanceof NamespaceTreeNode)) {
            NamespaceTreeNode namespaceNode = (NamespaceTreeNode) parent;
            if(namespaceNode.getNamespace().equals(content.getNamespace())) {
                addNode(namespaceNode, new ContentTreeNode(namespaceNode, content));
                return;
            }
        }
        for(TreeNode node : parent.getChildren())
            addNode(node, content);
    }

    /**
     * Add a new {@link StyleTreeNode} with <code>style</code> as its data to the model, placing it
     * in the correct place in the tree.
     *
     * @param style the {@link Style} to add.
     */
    public void addNode(Style style) {
        addNode(root, style);
    }

    /**
     * Add a new {@link StyleTreeNode} to the tree under parent <code>parent</code>.
     *
     * @param parent The TreeNode to add the node under.
     * @param style The {@link Style} to be put in the new StyleTreeNode.
     */
    public void addNode(TreeNode parent, Style style) {
        if((parent instanceof NamespaceTreeNode)) {
            NamespaceTreeNode namespaceNode = (NamespaceTreeNode) parent;
            if(namespaceNode.getNamespace().equals(style.getNamespace())) {
                addNode(namespaceNode, new StyleTreeNode(namespaceNode, style));
                return;
            }
        }

        for(TreeNode node : parent.getChildren())
            addNode(node, style);
    }

    /**
     * Add a new {@link ScriptTreeNode} with <code>script</code> as its data to the model, placing it
     * in the correct place in the tree.
     *
     * @param script the {@link Script} to add.
     */
    public void addNode(Script script) {
        addNode(root, script);
    }

    /**
     * Add a new {@link ScriptTreeNode} to the tree under parent <code>parent</code>.
     *
     * @param parent The TreeNode to add the node under.
     * @param script The {@link Script} to be put in the new ScriptTreeNode.
     */
    public void addNode(TreeNode parent, Script script) {
        if((parent instanceof NamespaceTreeNode)) {
            NamespaceTreeNode namespaceNode = (NamespaceTreeNode) parent;
            if(namespaceNode.getNamespace().equals(script.getNamespace())) {
                addNode(namespaceNode, new ScriptTreeNode(namespaceNode, script));
                return;
            }
        }

        for(TreeNode node : parent.getChildren())
            addNode(node, script);
    }
}
