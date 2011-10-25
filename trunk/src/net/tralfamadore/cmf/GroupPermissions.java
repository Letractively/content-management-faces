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

package net.tralfamadore.cmf;

/**
 * User: billreh
 * Date: 2/16/11
 * Time: 1:26 AM
 */
public class GroupPermissions {
    private String group;
    private boolean canView;
    private boolean canEdit;
    private boolean canDelete;
    private boolean canAdmin;

    public GroupPermissions() {
    }

    public GroupPermissions(String group) {
        this.group = group;
    }

    public GroupPermissions(String group, boolean canView, boolean canEdit, boolean canDelete, boolean canAdmin) {
        this.group = group;
        this.canView = canView;
        this.canEdit = canEdit;
        this.canDelete = canDelete;
        this.canAdmin = canAdmin;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public boolean isCanView() {
        return canView;
    }

    public void setCanView(boolean canView) {
        this.canView = canView;
    }

    public boolean isCanEdit() {
        return canEdit;
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }

    public boolean isCanDelete() {
        return canDelete;
    }

    public void setCanDelete(boolean canDelete) {
        this.canDelete = canDelete;
    }

    public boolean isCanAdmin() {
        return canAdmin;
    }

    public void setCanAdmin(boolean canAdmin) {
        this.canAdmin = canAdmin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GroupPermissions)) return false;

        GroupPermissions that = (GroupPermissions) o;

        if (canAdmin != that.canAdmin) return false;
        if (canDelete != that.canDelete) return false;
        if (canEdit != that.canEdit) return false;
        if (canView != that.canView) return false;
        if (group != null ? !group.equals(that.group) : that.group != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = group != null ? group.hashCode() : 0;
        result = 31 * result + (canView ? 1 : 0);
        result = 31 * result + (canEdit ? 1 : 0);
        result = 31 * result + (canDelete ? 1 : 0);
        result = 31 * result + (canAdmin ? 1 : 0);
        return result;
    }
}
