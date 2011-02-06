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

import net.tralfamadore.cmf.Namespace;

/**
 * User: billreh
 * Date: 1/20/11
 * Time: 3:03 PM
 *
 * Stores data for namespace nodes.
 */
public class NamespaceTreeNode extends TreeNode {
    /** The {@link Namespace} object */
    private Namespace namespace;

    /**
     * Create a new namespace node with <code>parent</code> and <code>namespace</code>.
     *
     * @param parent The parent node, <code>null</code> if this is a root node.
     * @param namespace The {@link Namespace}.
     */
    public NamespaceTreeNode(TreeNode parent, Namespace namespace) {
        super(parent, namespace.getNodeName());
        this.namespace = namespace;
    }


    /* getters and setters */

    public Namespace getNamespace() {
        return namespace;
    }

    public void setNamespace(Namespace namespace) {
        this.namespace = namespace;
    }

    @Override
    /**
     * Match nodes of type "namespace".
     *
     * @return "namespace"
     */
    public String getType() {
        return "namespace";
    }
}
