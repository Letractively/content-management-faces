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
 * Date: 2/15/11
 * Time: 11:41 PM
 */
@Entity(name = "group_permissions_to_style")
public class GroupPermissionsToStyleEntity implements Serializable {
    private long id;
    private long styleId;
    private long groupId;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "groupPermissionsToStyleGen")
    @TableGenerator(name = "groupPermissionsToStyleGen",
            table = "id_gen",
            pkColumnName = "gen_name",
            valueColumnName = "gen_val",
            pkColumnValue = "group_permissions_to_style_id",
            initialValue = 0,
            allocationSize = 50
    )
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "style_id")
    public long getStyleId() {
        return styleId;
    }

    public void setStyleId(long styleId) {
        this.styleId = styleId;
    }

    @Column(name = "group_permissions_id")
    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }
}
