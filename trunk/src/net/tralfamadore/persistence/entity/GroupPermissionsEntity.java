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

package net.tralfamadore.persistence.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * User: billreh
 * Date: 2/16/11
 * Time: 12:38 AM
 */
@Entity(name = "group_permissions")
public class GroupPermissionsEntity implements Serializable {
    private long id;
    private GroupEntity group;
    private char canView;
    private char canEdit;
    private char canDelete;
    private char canAdmin;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "groupPermissionGen")
    @TableGenerator(name = "groupPermissionGen",
            table = "id_gen",
            pkColumnName = "gen_name",
            valueColumnName = "gen_val",
            pkColumnValue = "group_permissions_id",
            initialValue = 0,
            allocationSize = 50
    )
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "group_id", referencedColumnName = "ID")
    public GroupEntity getGroup() {
        return group;
    }

    public void setGroup(GroupEntity group) {
        this.group = group;
    }

    @Column(name = "can_view", nullable = false)
    public char getCanView() {
        return canView;
    }

    public void setCanView(char canView) {
        this.canView = canView;
    }

    @Column(name = "can_edit", nullable = false)
    public char getCanEdit() {
        return canEdit;
    }

    public void setCanEdit(char canEdit) {
        this.canEdit = canEdit;
    }

    @Column(name = "can_delete", nullable = false)
    public char getCanDelete() {
        return canDelete;
    }

    public void setCanDelete(char canDelete) {
        this.canDelete = canDelete;
    }

    @Column(name = "can_admin", nullable = false)
    public char getCanAdmin() {
        return canAdmin;
    }

    public void setCanAdmin(char canAdmin) {
        this.canAdmin = canAdmin;
    }

    public boolean canView() {
        return getCanView() == 'T';
    }

    public void setCanView(boolean canView) {
        setCanView(canView ? 'T' : 'F');
    }

    public boolean canEdit() {
        return getCanEdit() == 'T';
    }

    public void setCanEdit(boolean canEdit) {
        setCanEdit(canEdit ? 'T' : 'F');
    }

    public boolean canDelete() {
        return getCanDelete() == 'T';
    }

    public void setCanDelete(boolean canDelete) {
        setCanDelete(canDelete ? 'T' : 'F');
    }

    public boolean canAdmin() {
        return getCanAdmin() == 'T';
    }

    public void setCanAdmin(boolean canAdmin) {
        setCanAdmin(canAdmin ? 'T' : 'F');
    }
}
