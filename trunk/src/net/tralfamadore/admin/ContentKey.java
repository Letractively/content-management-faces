package net.tralfamadore.admin;

import java.io.Serializable;

/**
* User: billreh
* Date: 2/28/11
* Time: 6:17 AM
*/
public class ContentKey implements Serializable {
    private String name;
    private String namespace;
    private String type;

    public ContentKey() {
    }

    public ContentKey(String name, String namespace, String type) {
        this.name = name;
        this.namespace = namespace;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContentKey)) return false;

        ContentKey that = (ContentKey) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (namespace != null ? !namespace.equals(that.namespace) : that.namespace != null) return false;
        //noinspection RedundantIfStatement
        if (type != null ? !type.equals(that.type) : that.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (namespace != null ? namespace.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return namespace + (name != null ? "." + name : "") + "-" + type;
    }
}
