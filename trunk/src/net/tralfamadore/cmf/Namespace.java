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
import java.util.List;
import java.util.Vector;

/**
 * User: billreh
 * Date: 1/18/11
 * Time: 10:26 PM
 */
public class Namespace implements Serializable {
    private String nodeName;
    private Namespace parent;
    private List<GroupPermissions> groupPermissionsList = new Vector<GroupPermissions>();


    public static Namespace createFromString(String namespace) {
        String[] nodes = namespace.split("\\.");
        Namespace cur = new Namespace(), parent = null;

        for(String node : nodes) {
            cur = new Namespace(parent, node);
            parent = cur;
        }

        return cur;
    }


    public Namespace() {
        this(null);
    }

    public Namespace(String nodeName) {
        this(null, nodeName);
    }

    public Namespace(Namespace parent, String nodeName) {
        this.parent = parent;
        this.nodeName = nodeName;
    }


    public String getFullName() {
        List<String> names = new Vector<String>();
        if(getNodeName() != null)
            names.add(getNodeName());

        Namespace p = parent;
        while(p != null) {
            names.add(p.getNodeName());
            p = p.parent;
        }

        StringBuffer buf = new StringBuffer();
        for(int i = names.size() - 1; i > 0; i--) {
            buf.append(names.get(i)).append('.');
        }
        if(!names.isEmpty())
            buf.append(names.get(0));

        return buf.length() == 0 ? null :  buf.toString();
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public Namespace getParent() {
        return parent;
    }

    public void setParent(Namespace parent) {
        this.parent = parent;
    }

    public List<GroupPermissions> getGroupPermissionsList() {
        return groupPermissionsList;
    }

    public void setGroupPermissionsList(List<GroupPermissions> groupPermissionsList) {
        this.groupPermissionsList = groupPermissionsList;
    }

    public List<Namespace> getParentNamespaces() {
        List<Namespace> namespaces = new Vector<Namespace>();

        if(getParent() == null)
            return namespaces;

        String[] nodes = getFullName().split("\\.");
        Namespace cur = new Namespace(), parent = null;

        for(String node : nodes) {
            cur = new Namespace(parent, node);
            namespaces.add(cur);
            parent = cur;
        }

        return namespaces;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Namespace)) return false;

        Namespace namespace = (Namespace) o;

        return namespace.getFullName().equals(getFullName());
    }

    @Override
    public int hashCode() {
        int result = nodeName != null ? nodeName.hashCode() : 0;
        result = 31 * result + (parent != null ? parent.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return getFullName();
    }
}
