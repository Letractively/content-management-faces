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

package net.tralfamadore.cmf;

import java.io.Serializable;

/**
 * User: billreh
 * Date: 1/18/11
 * Time: 11:42 PM
 */
public abstract class BaseContent implements Serializable {
    private String name;
    private Namespace namespace;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Namespace getNamespace() {
        return namespace;
    }

    public void setNamespace(Namespace namespace) {
        this.namespace = namespace;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseContent)) return false;

        BaseContent that = (BaseContent) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        //noinspection RedundantIfStatement
        if (namespace != null ? !namespace.equals(that.namespace) : that.namespace != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (namespace != null ? namespace.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "BaseContent{" +
                "name='" + name + '\'' +
                ", namespace=" + namespace +
                '}';
    }
}
