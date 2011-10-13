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

package net.tralfamadore.newAdmin;

import net.tralfamadore.cmf.GroupPermissions;
import net.tralfamadore.config.CmfContext;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import java.util.List;
import java.util.Vector;

/**
 * User: billreh
 * Date: 10/11/11
 * Time: 9:05 PM
 */
@Named
@RequestScoped
public class DialogGroups {
    private List<GroupPermissions> groupPermissions = new Vector<GroupPermissions>();

    public DialogGroups() {
        String group = CmfContext.getInstance().getCurrentUser();
        GroupPermissions defaultGroupPermission = new GroupPermissions(group, true, true, true, true);
        groupPermissions.add(defaultGroupPermission);
    }

    public List<GroupPermissions> getGroupPermissions() {
        return groupPermissions;
    }

    public void setGroupPermissions(List<GroupPermissions> groupPermissions) {
        this.groupPermissions = groupPermissions;
    }
}
