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
 * Date: 1/30/11
 * Time: 8:27 AM
 */
@Entity(name = "style_to_content")
public class StyleToContentEntity implements Serializable {
    private long id;
    private long contentId;
    private long styleId;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "styleToContentGen")
    @TableGenerator(name = "styleToContentGen",
            table = "id_gen",
            pkColumnName = "gen_name",
            valueColumnName = "gen_val",
            pkColumnValue = "style_to_content_id",
            initialValue = 0,
            allocationSize = 50
    )
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "content_id")
    public long getContentId() {
        return contentId;
    }

    public void setContentId(long contentId) {
        this.contentId = contentId;
    }

    @Column(name = "style_id")
    public long getStyleId() {
        return styleId;
    }

    public void setStyleId(long styleId) {
        this.styleId = styleId;
    }
}
