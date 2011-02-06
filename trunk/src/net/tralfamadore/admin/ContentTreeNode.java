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

/**
 * User: billreh
 * Date: 1/20/11
 * Time: 3:03 PM
 *
 * Stores data for content nodes.
 */
public class ContentTreeNode extends TreeNode {
    /** The {@link Content} object */
    private Content content;

    /**
     * Create a new content node with <code>parent</code> and <code>content</code>.
     *
     * @param parent The parent node, <code>null</code> if this is a root node.
     * @param content The {@link Content}.
     */
    public ContentTreeNode(TreeNode parent, Content content) {
        super(parent, content.getName());
        this.content = content;
    }


    /* getters and setters */
    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    /**
     * Match nodes of type "content".
     *
     * @return "content"
     */
    @Override
    public String getType() {
        return "content";
    }
}
