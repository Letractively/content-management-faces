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
import java.util.Set;

/**
 * User: billreh
 * Date: 1/30/11
 * Time: 7:32 AM
 */
@Entity(name = "namespace")
public class NamespaceEntity implements Serializable {
    private long id;
    private String name;
    private Long parentId;
    private Set<GroupPermissionsEntity> groupPermissions;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "namespaceGen")
    @TableGenerator(name = "namespaceGen",
            table = "id_gen",
            pkColumnName = "gen_name",
            valueColumnName = "gen_val",
            pkColumnValue = "namespace_id",
            initialValue = 0,
            allocationSize = 50
    )
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "parent_id")
    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "group_permissions_to_namespace",
            joinColumns = { @JoinColumn(name = "namespace_id") },
            inverseJoinColumns = { @JoinColumn(name = "group_permissions_id") })
    public Set<GroupPermissionsEntity> getGroupPermissions() {
        return groupPermissions;
    }

    public void setGroupPermissions(Set<GroupPermissionsEntity> groupPermissions) {
        this.groupPermissions = groupPermissions;
    }
/*
    private Set<ContentEntity> content;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "content",
            joinColumns = { @JoinColumn(name = "namespace_id") },
            inverseJoinColumns = { @JoinColumn(name = "id") })
    public Set<ContentEntity> getContent() {
        return content;
    }

    public void setContent(Set<ContentEntity> content) {
        this.content = content;
    }
    */
}
