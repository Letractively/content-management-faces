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

package net.tralfamadore.admin.tree;

import net.tralfamadore.cmf.Style;

/**
 * User: billreh
 * Date: 1/20/11
 * Time: 3:03 PM
 *
 * Stores data for style nodes.
 */
public class StyleTreeNode extends TreeNode {
    /** The {@link Style} object */
    private Style style;

    /**
     * Create a new style node with <code>parent</code> and <code>style</code>.
     *
     * @param parent The parent node, <code>null</code> if this is a root node.
     * @param style The {@link Style}.
     */
    public StyleTreeNode(TreeNode parent, Style style) {
        super(parent, style.getName());
        this.style = style;
    }


    /* getters and setters */

    public Style getStyle() {
        return style;
    }

    public void setStyle(Style style) {
        this.style = style;
    }

    /**
     * Match nodes of type "Style".
     *
     * @return "Style"
     */
    @Override
    public String getType() {
        return "style";
    }
}
