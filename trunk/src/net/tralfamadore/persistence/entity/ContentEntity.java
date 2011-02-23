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
import java.util.Date;
import java.util.Set;

/**
 * User: billreh
 * Date: 1/30/11
 * Time: 8:09 AM
 */
@Entity(name = "content")
public class ContentEntity implements Serializable {
    private long id;
    private long namespaceId;
    private String name;
    private String content;
    private Date dateCreated;
    private Date dateModified;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "contentGen")
    @TableGenerator(name = "contentGen",
            table = "id_gen",
            pkColumnName = "gen_name",
            valueColumnName = "gen_val",
            pkColumnValue = "content_id",
            initialValue = 0,
            allocationSize = 50
    )
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(insertable = false, updatable = false, name = "namespace_id")
    public long getNamespace_id() {
        return namespaceId;
    }

    public void setNamespace_id(long namespaceId) {
        this.namespaceId = namespaceId;
    }

    @Column
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Column(name = "date_created")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Column(name = "date_modified")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    private Set<StyleEntity> styles;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "style_to_content",
            joinColumns = { @JoinColumn(name = "content_id") },
            inverseJoinColumns = { @JoinColumn(name = "style_id") })
    public Set<StyleEntity> getStyles() {
        return styles;
    }

    public void setStyles(Set<StyleEntity> styles) {
        this.styles = styles;
    }

    private NamespaceEntity namespace;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinColumn(name = "namespace_id", referencedColumnName = "ID")
    public NamespaceEntity getNamespace() {
        return namespace;
    }

    public void setNamespace(NamespaceEntity namespace) {
        this.namespace = namespace;
    }




    private Set<GroupPermissionsEntity> groupPermissions;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "group_permissions_to_content",
            joinColumns = { @JoinColumn(name = "content_id") },
            inverseJoinColumns = { @JoinColumn(name = "group_permissions_id") })
    public Set<GroupPermissionsEntity> getGroupPermissions() {
        return groupPermissions;
    }

    public void setGroupPermissions(Set<GroupPermissionsEntity> groupPermissions) {
        this.groupPermissions = groupPermissions;
    }

}
