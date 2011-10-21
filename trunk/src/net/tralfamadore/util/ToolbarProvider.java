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

package net.tralfamadore.util;

import com.google.code.ckJsfEditor.Toolbar;
import com.google.code.ckJsfEditor.ToolbarButtonGroup;
import com.google.code.ckJsfEditor.ToolbarItem;

import javax.enterprise.inject.Produces;

/**
 * User: billreh
 * Date: 10/20/11
 * Time: 9:25 PM
 */
public class ToolbarProvider {
    @Produces
    public Toolbar getToolbar() {
        return new Toolbar(
                ToolbarButtonGroup.DOCUMENT.remove(ToolbarItem.SAVE).remove(ToolbarItem.NEW_PAGE),
                ToolbarButtonGroup.CLIPBOARD,
                ToolbarButtonGroup.EDITING,
                ToolbarButtonGroup.COLORS.item(ToolbarItem.BREAK),
                ToolbarButtonGroup.PARAGRAPH,
                ToolbarButtonGroup.INSERT.remove(ToolbarItem.FLASH).remove(ToolbarItem.IFRAME),
                ToolbarButtonGroup.LINKS.item(ToolbarItem.BREAK),
                ToolbarButtonGroup.STYLES,
                ToolbarButtonGroup.TOOLS
        );
    }
}
