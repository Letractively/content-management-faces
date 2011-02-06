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

import net.tralfamadore.cmf.Script;

/**
 * User: billreh
 * Date: 1/20/11
 * Time: 3:03 PM
 *
 * Stores data for script nodes.
 */
public class ScriptTreeNode extends TreeNode {
    /** The {@link Script} object */
    private Script script;

    /**
     * Create a new script node with <code>parent</code> and <code>script</code>.
     *
     * @param parent The parent node, <code>null</code> if this is a root node.
     * @param script The {@link Script}.
     */
    public ScriptTreeNode(TreeNode parent, Script script) {
        super(parent, script.getName());
        this.script = script;
    }


    /* getters and setters */

    public Script getScript() {
        return script;
    }

    public void setScript(Script script) {
        this.script = script;
    }

    /**
     * Match nodes of type "script".
     *
     * @return "script"
     */
    @Override
    public String getType() {
        return "script";
    }
}
